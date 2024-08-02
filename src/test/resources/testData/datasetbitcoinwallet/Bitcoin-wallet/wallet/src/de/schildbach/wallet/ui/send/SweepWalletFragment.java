/*
 * Copyright 2014-2015 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.schildbach.wallet.ui.send;

import static android.support.v4.util.Preconditions.checkState;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionConfidence.ConfidenceType;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.UTXO;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.VersionedChecksummedBytes;
import org.bitcoinj.crypto.BIP38PrivateKey;
import org.bitcoinj.utils.MonetaryFormat;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.Wallet.BalanceType;
import org.bitcoinj.wallet.WalletTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ComparisonChain;

import de.schildbach.wallet.Configuration;
import de.schildbach.wallet.Constants;
import de.schildbach.wallet.R;
import de.schildbach.wallet.WalletApplication;
import de.schildbach.wallet.data.DynamicFeeLiveData;
import de.schildbach.wallet.data.PaymentIntent;
import de.schildbach.wallet.ui.AbstractWalletActivity;
import de.schildbach.wallet.ui.DialogBuilder;
import de.schildbach.wallet.ui.InputParser.StringInputParser;
import de.schildbach.wallet.ui.ProgressDialogFragment;
import de.schildbach.wallet.ui.TransactionsAdapter;
import de.schildbach.wallet.ui.scan.ScanActivity;
import de.schildbach.wallet.util.MonetarySpannable;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

//&begin[SweepPaperWallet]
/**
 * @author Andreas Schildbach
 */
public class SweepWalletFragment extends Fragment {
    private AbstractWalletActivity activity;
    private WalletApplication application;
    private Configuration config;
    private FragmentManager fragmentManager;

    private final Handler handler = new Handler();
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    private TextView messageView;
    //&begin[SetPIN]
    private View passwordViewGroup;
    private EditText passwordView;
    private View badPasswordView;
    //&end[SetPIN]
    //&line[BitcoinBalance]
    private TextView balanceView;
    private View hintView;
    private ViewGroup sweepTransactionViewGroup;
    private TransactionsAdapter.TransactionViewHolder sweepTransactionViewHolder;
    private Button viewGo;
    private Button viewCancel;

    private MenuItem reloadAction;
    private MenuItem scanAction;

    private ViewModel viewModel;

    private static final int REQUEST_CODE_SCAN = 0;

    private enum State {
        DECODE_KEY, // ask for password
        CONFIRM_SWEEP, // displays balance and asks for confirmation
        PREPARATION, SENDING, SENT, FAILED // sending states
    }

    private static final Logger log = LoggerFactory.getLogger(SweepWalletFragment.class);

    public static class ViewModel extends AndroidViewModel {
        private final WalletApplication application;
        //&line[Fee]
        private DynamicFeeLiveData dynamicFees;

        private State state = State.DECODE_KEY;
        @Nullable
        private VersionedChecksummedBytes privateKeyToSweep = null;
        @Nullable
        private Wallet walletToSweep = null;
        @Nullable
        private Transaction sentTransaction = null;

        public ViewModel(final Application application) {
            super(application);
            this.application = (WalletApplication) application;
        }

        //&begin[Fee]
        public DynamicFeeLiveData getDynamicFees() {
            if (dynamicFees == null)
                dynamicFees = new DynamicFeeLiveData(application);
            return dynamicFees;
        }
        //&end[Fee]
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        this.activity = (AbstractWalletActivity) context;
        this.application = activity.getWalletApplication();
        this.config = application.getConfiguration();
        this.fragmentManager = getFragmentManager();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (!Constants.ENABLE_SWEEP_WALLET)
            throw new IllegalStateException("ENABLE_SWEEP_WALLET is disabled");

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        //&begin[Fee]
        viewModel.getDynamicFees().observe(this, new Observer<Map<FeeCategory, Coin>>() {
            @Override
            public void onChanged(final Map<FeeCategory, Coin> dynamicFees) {
                updateView();
            }
        });
        //&end[Fee]

        backgroundThread = new HandlerThread("backgroundThread", Process.THREAD_PRIORITY_BACKGROUND);
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        if (savedInstanceState == null) {
            final Intent intent = activity.getIntent();

            if (intent.hasExtra(SweepWalletActivity.INTENT_EXTRA_KEY)) {
                viewModel.privateKeyToSweep = (VersionedChecksummedBytes) intent
                        .getSerializableExtra(SweepWalletActivity.INTENT_EXTRA_KEY);

                // delay until fragment is resumed
                handler.post(maybeDecodeKeyRunnable);
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.sweep_wallet_fragment, container);

        messageView = (TextView) view.findViewById(R.id.sweep_wallet_fragment_message);

        //&begin[SetPIN]
        passwordViewGroup = view.findViewById(R.id.sweep_wallet_fragment_password_group);
        passwordView = (EditText) view.findViewById(R.id.sweep_wallet_fragment_password);
        badPasswordView = view.findViewById(R.id.sweep_wallet_fragment_bad_password);
        //&end[SetPIN]

        //&line[BitcoinBalance]
        balanceView = (TextView) view.findViewById(R.id.sweep_wallet_fragment_balance);

        hintView = view.findViewById(R.id.sweep_wallet_fragment_hint);

        sweepTransactionViewGroup = (FrameLayout) view.findViewById(R.id.transaction_row);
        sweepTransactionViewGroup
                .setLayoutAnimation(AnimationUtils.loadLayoutAnimation(activity, R.anim.transaction_layout_anim));
        sweepTransactionViewHolder = new TransactionsAdapter.TransactionViewHolder(view);

        //&line[SendCoins]
        viewGo = (Button) view.findViewById(R.id.send_coins_go);
        viewGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (viewModel.state == State.DECODE_KEY)
                    handleDecrypt();
                if (viewModel.state == State.CONFIRM_SWEEP)
                    handleSweep();
            }
        });

        //&line[SendCoins]
        viewCancel = (Button) view.findViewById(R.id.send_coins_cancel);
        viewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                activity.finish();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        backgroundThread.getLooper().quit();

        if (viewModel.sentTransaction != null)
            viewModel.sentTransaction.getConfidence().removeEventListener(sentTransactionConfidenceListener);

        super.onDestroy();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                final String input = intent.getStringExtra(ScanActivity.INTENT_EXTRA_RESULT);

                new StringInputParser(input) {
                    @Override
                    protected void handlePrivateKey(final VersionedChecksummedBytes key) {
                        viewModel.privateKeyToSweep = key;
                        setState(State.DECODE_KEY);
                        maybeDecodeKey();
                    }

                    @Override
                    protected void handlePaymentIntent(final PaymentIntent paymentIntent) {
                        cannotClassify(input);
                    }

                    @Override
                    protected void handleDirectTransaction(final Transaction transaction) throws VerificationException {
                        cannotClassify(input);
                    }

                    @Override
                    protected void error(final int messageResId, final Object... messageArgs) {
                        dialog(activity, null, R.string.button_scan, messageResId, messageArgs);
                    }
                }.parse();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.sweep_wallet_fragment_options, menu);

        reloadAction = menu.findItem(R.id.sweep_wallet_options_reload);
        scanAction = menu.findItem(R.id.sweep_wallet_options_scan);

        final PackageManager pm = activity.getPackageManager();
        scanAction.setVisible(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT));

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.sweep_wallet_options_reload:
            handleReload();
            return true;

        case R.id.sweep_wallet_options_scan:
            ScanActivity.startForResult(this, activity, REQUEST_CODE_SCAN);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleReload() {
        if (viewModel.walletToSweep == null)
            return;

        //&line[BitcoinBalance]
        requestWalletBalance();
    }

    private final TransactionConfidence.Listener sentTransactionConfidenceListener = new TransactionConfidence.Listener() {
        @Override
        public void onConfidenceChanged(final TransactionConfidence confidence,
                final TransactionConfidence.Listener.ChangeReason reason) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isResumed())
                        return;

                    final TransactionConfidence confidence = viewModel.sentTransaction.getConfidence();
                    final TransactionConfidence.ConfidenceType confidenceType = confidence.getConfidenceType();
                    final int numBroadcastPeers = confidence.numBroadcastPeers();

                    if (viewModel.state == State.SENDING) {
                        if (confidenceType == TransactionConfidence.ConfidenceType.DEAD)
                            setState(State.FAILED);
                        else if (numBroadcastPeers > 1
                                || confidenceType == TransactionConfidence.ConfidenceType.BUILDING)
                            setState(State.SENT);
                    }

                    if (reason == ChangeReason.SEEN_PEERS
                            && confidenceType == TransactionConfidence.ConfidenceType.PENDING) {
                        // play sound effect
                        final int soundResId = getResources().getIdentifier("send_coins_broadcast_" + numBroadcastPeers,
                                "raw", activity.getPackageName());
                        if (soundResId > 0)
                            RingtoneManager
                                    .getRingtone(activity, Uri.parse(
                                            "android.resource://" + activity.getPackageName() + "/" + soundResId))
                                    .play();
                    }

                    updateView();
                }
            });
        }
    };

    private final Runnable maybeDecodeKeyRunnable = new Runnable() {
        @Override
        public void run() {
            maybeDecodeKey();
        }
    };

    private void maybeDecodeKey() {
        checkState(viewModel.state == State.DECODE_KEY);
        checkState(viewModel.privateKeyToSweep != null);

        if (viewModel.privateKeyToSweep instanceof DumpedPrivateKey) {
            final ECKey key = ((DumpedPrivateKey) viewModel.privateKeyToSweep).getKey();
            askConfirmSweep(key);
        //&begin[SetPIN]
        } else if (viewModel.privateKeyToSweep instanceof BIP38PrivateKey) {
            badPasswordView.setVisibility(View.INVISIBLE);

            final String password = passwordView.getText().toString().trim();
            passwordView.setText(null); // get rid of it asap

            if (!password.isEmpty()) {
                ProgressDialogFragment.showProgress(fragmentManager,
                        getString(R.string.sweep_wallet_fragment_decrypt_progress));

                new DecodePrivateKeyTask(backgroundHandler) {
                    @Override
                    protected void onSuccess(ECKey decryptedKey) {
                        log.info("successfully decoded BIP38 private key");

                        ProgressDialogFragment.dismissProgress(fragmentManager);

                        askConfirmSweep(decryptedKey);
                    }

                    @Override
                    protected void onBadPassphrase() {
                        log.info("failed decoding BIP38 private key (bad password)");

                        ProgressDialogFragment.dismissProgress(fragmentManager);

                        badPasswordView.setVisibility(View.VISIBLE);
                        passwordView.requestFocus();
                    }
                }.decodePrivateKey((BIP38PrivateKey) viewModel.privateKeyToSweep, password);
            }
        //&end[SetPIN]
        } else {
            throw new IllegalStateException("cannot handle type: " + viewModel.privateKeyToSweep.getClass().getName());
        }
    }

    private void askConfirmSweep(final ECKey key) {
        // create non-HD wallet
        final KeyChainGroup group = new KeyChainGroup(Constants.NETWORK_PARAMETERS);
        group.importKeys(key);
        viewModel.walletToSweep = new Wallet(Constants.NETWORK_PARAMETERS, group);

        setState(State.CONFIRM_SWEEP);

        //&begin[BitcoinBalance]
        // delay until fragment is resumed
        handler.post(requestWalletBalanceRunnable);
        //&end[BitcoinBalance]
    }

    //&begin[BitcoinBalance]
    private final Runnable requestWalletBalanceRunnable = new Runnable() {
        @Override
        public void run() {
            requestWalletBalance();
        }
    };
    //&end[BitcoinBalance]

    private static final Comparator<UTXO> UTXO_COMPARATOR = new Comparator<UTXO>() {
        @Override
        public int compare(final UTXO lhs, final UTXO rhs) {
            return ComparisonChain.start().compare(lhs.getHash(), rhs.getHash()).compare(lhs.getIndex(), rhs.getIndex())
                    .result();
        }
    };

    //&begin[BitcoinBalance]
    private void requestWalletBalance() {
        ProgressDialogFragment.showProgress(fragmentManager,
                getString(R.string.sweep_wallet_fragment_request_wallet_balance_progress));

        final RequestWalletBalanceTask.ResultCallback callback = new RequestWalletBalanceTask.ResultCallback() {
            @Override
            public void onResult(final Set<UTXO> utxos) {
                ProgressDialogFragment.dismissProgress(fragmentManager);

                // Filter UTXOs we've already spent and sort the rest.
                final Set<Transaction> walletTxns = application.getWallet().getTransactions(false);
                final Set<UTXO> sortedUtxos = new TreeSet<>(UTXO_COMPARATOR);
                for (final UTXO utxo : utxos)
                    if (!utxoSpentBy(walletTxns, utxo))
                        sortedUtxos.add(utxo);

                // Fake transaction funding the wallet to sweep.
                final Map<Sha256Hash, Transaction> fakeTxns = new HashMap<>();
                for (final UTXO utxo : sortedUtxos) {
                    Transaction fakeTx = fakeTxns.get(utxo.getHash());
                    if (fakeTx == null) {
                        fakeTx = new FakeTransaction(Constants.NETWORK_PARAMETERS, utxo.getHash());
                        fakeTx.getConfidence().setConfidenceType(ConfidenceType.BUILDING);
                        fakeTxns.put(fakeTx.getHash(), fakeTx);
                    }
                    final TransactionOutput fakeOutput = new TransactionOutput(Constants.NETWORK_PARAMETERS, fakeTx,
                            utxo.getValue(), utxo.getScript().getProgram());
                    // Fill with output dummies as needed.
                    while (fakeTx.getOutputs().size() < utxo.getIndex())
                        fakeTx.addOutput(new TransactionOutput(Constants.NETWORK_PARAMETERS, fakeTx,
                                Coin.NEGATIVE_SATOSHI, new byte[] {}));
                    // Add the actual output we will spend later.
                    fakeTx.addOutput(fakeOutput);
                }

                viewModel.walletToSweep.clearTransactions(0);
                for (final Transaction tx : fakeTxns.values())
                    viewModel.walletToSweep
                            .addWalletTransaction(new WalletTransaction(WalletTransaction.Pool.UNSPENT, tx));
                log.info("built wallet to sweep:\n{}", viewModel.walletToSweep.toString(false, true, false, null));

                updateView();
            }

            private boolean utxoSpentBy(final Set<Transaction> transactions, final UTXO utxo) {
                for (final Transaction tx : transactions) {
                    for (final TransactionInput input : tx.getInputs()) {
                        final TransactionOutPoint outpoint = input.getOutpoint();
                        if (outpoint.getHash().equals(utxo.getHash()) && outpoint.getIndex() == utxo.getIndex())
                            return true;
                    }
                }
                return false;
            }

            @Override
            public void onFail(final int messageResId, final Object... messageArgs) {
                ProgressDialogFragment.dismissProgress(fragmentManager);

                final DialogBuilder dialog = DialogBuilder.warn(activity,
                        R.string.sweep_wallet_fragment_request_wallet_balance_failed_title);
                dialog.setMessage(getString(messageResId, messageArgs));
                dialog.setPositiveButton(R.string.button_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        requestWalletBalance();
                    }
                });
                dialog.setNegativeButton(R.string.button_dismiss, null);
                dialog.show();
            }
        };

        final Address address = viewModel.walletToSweep.getImportedKeys().iterator().next()
                .toAddress(Constants.NETWORK_PARAMETERS);
        new RequestWalletBalanceTask(backgroundHandler, callback).requestWalletBalance(activity.getAssets(), address);
    }
    //&end[BitcoinBalance]

    private void setState(final State state) {
        viewModel.state = state;

        updateView();
    }

    private void updateView() {
        //&line[Fee]
        final Map<FeeCategory, Coin> fees = viewModel.getDynamicFees().getValue();
        //&line[Denomination]
        final MonetaryFormat btcFormat = config.getFormat();

        if (viewModel.walletToSweep != null) {
            //&begin[BitcoinBalance]
            balanceView.setVisibility(View.VISIBLE);
            final MonetarySpannable balanceSpannable = new MonetarySpannable(btcFormat,
                    viewModel.walletToSweep.getBalance(BalanceType.ESTIMATED));
            balanceSpannable.applyMarkup(null, null);
            final SpannableStringBuilder balance = new SpannableStringBuilder(balanceSpannable);
            balance.insert(0, ": ");
            balance.insert(0, getString(R.string.sweep_wallet_fragment_balance));
            balanceView.setText(balance);
        } else {
            balanceView.setVisibility(View.GONE);
            //&end[BitcoinBalance]
        }

        if (viewModel.state == State.DECODE_KEY && viewModel.privateKeyToSweep == null) {
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(R.string.sweep_wallet_fragment_wallet_unknown);
        } else if (viewModel.state == State.DECODE_KEY && viewModel.privateKeyToSweep != null) {
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(R.string.sweep_wallet_fragment_encrypted);
        } else if (viewModel.privateKeyToSweep != null) {
            messageView.setVisibility(View.GONE);
        }

        //&begin[SetPIN]
        passwordViewGroup.setVisibility(
                viewModel.state == State.DECODE_KEY && viewModel.privateKeyToSweep != null ? View.VISIBLE : View.GONE);
        //&end[SetPIN]

        hintView.setVisibility(
                viewModel.state == State.DECODE_KEY && viewModel.privateKeyToSweep == null ? View.VISIBLE : View.GONE);

        if (viewModel.sentTransaction != null) {
            sweepTransactionViewGroup.setVisibility(View.VISIBLE);
            sweepTransactionViewHolder
                    .bind(new TransactionsAdapter.ListItem.TransactionItem(activity, viewModel.sentTransaction,
                            application.getWallet(), null, btcFormat, application.maxConnectedPeers(), false));
        } else {
            sweepTransactionViewGroup.setVisibility(View.GONE);
        }

        if (viewModel.state == State.DECODE_KEY) {
            viewCancel.setText(R.string.button_cancel);
            viewGo.setText(R.string.sweep_wallet_fragment_button_decrypt);
            viewGo.setEnabled(viewModel.privateKeyToSweep != null);
        } else if (viewModel.state == State.CONFIRM_SWEEP) {
            viewCancel.setText(R.string.button_cancel);
            viewGo.setText(R.string.sweep_wallet_fragment_button_sweep);
            viewGo.setEnabled(viewModel.walletToSweep != null
                    //&begin[BitcoinBalance]
                    //&line[Fee]
                    && viewModel.walletToSweep.getBalance(BalanceType.ESTIMATED).signum() > 0 && fees != null);
                    //&end[BitcoinBalance]
        //&begin[SendCoins]
        } else if (viewModel.state == State.PREPARATION) {
            viewCancel.setText(R.string.button_cancel);
            viewGo.setText(R.string.send_coins_preparation_msg);
            viewGo.setEnabled(false);
        } else if (viewModel.state == State.SENDING) {
            viewCancel.setText(R.string.send_coins_fragment_button_back);
            viewGo.setText(R.string.send_coins_sending_msg);
            viewGo.setEnabled(false);
        } else if (viewModel.state == State.SENT) {
            viewCancel.setText(R.string.send_coins_fragment_button_back);
            viewGo.setText(R.string.send_coins_sent_msg);
            viewGo.setEnabled(false);
        } else if (viewModel.state == State.FAILED) {
            viewCancel.setText(R.string.send_coins_fragment_button_back);
            viewGo.setText(R.string.send_coins_failed_msg);
            viewGo.setEnabled(false);
        }
        //&end[SendCoins]

        viewCancel.setEnabled(viewModel.state != State.PREPARATION);

        // enable actions
        if (reloadAction != null)
            reloadAction.setEnabled(viewModel.state == State.CONFIRM_SWEEP && viewModel.walletToSweep != null);
        if (scanAction != null)
            scanAction.setEnabled(viewModel.state == State.DECODE_KEY || viewModel.state == State.CONFIRM_SWEEP);
    }

    private void handleDecrypt() {
        handler.post(maybeDecodeKeyRunnable);
    }

    private void handleSweep() {
        setState(State.PREPARATION);

        //&begin[Fee]
        final Map<FeeCategory, Coin> fees = viewModel.getDynamicFees().getValue();
        //&line[EmptyWallet]
        final SendRequest sendRequest = SendRequest.emptyWallet(application.getWallet().freshReceiveAddress());
        // &line[Normal]
        sendRequest.feePerKb = fees.get(FeeCategory.NORMAL);
        //&end[Fee]

        //&begin[SendCoins]
        new SendCoinsOfflineTask(viewModel.walletToSweep, backgroundHandler) {
            @Override
            protected void onSuccess(final Transaction transaction) {
                viewModel.sentTransaction = transaction;

                setState(State.SENDING);

                viewModel.sentTransaction.getConfidence().addEventListener(sentTransactionConfidenceListener);

                application.processDirectTransaction(viewModel.sentTransaction);
            }

            @Override
            protected void onInsufficientMoney(@Nullable final Coin missing) {
                setState(State.FAILED);

                showInsufficientMoneyDialog();
            }

            //&begin[EmptyWallet]
            @Override
            protected void onEmptyWalletFailed() {
                setState(State.FAILED);

                showInsufficientMoneyDialog();
            }
            //&end[EmptyWallet]

            @Override
            protected void onFailure(final Exception exception) {
                setState(State.FAILED);

                //&line[SendCoins]
                final DialogBuilder dialog = DialogBuilder.warn(activity, R.string.send_coins_error_msg);
                dialog.setMessage(exception.toString());
                dialog.setNeutralButton(R.string.button_dismiss, null);
                dialog.show();
            }

            @Override
            protected void onInvalidEncryptionKey() {
                throw new RuntimeException(); // cannot happen
            }

            private void showInsufficientMoneyDialog() {
                final DialogBuilder dialog = DialogBuilder.warn(activity,
                        R.string.sweep_wallet_fragment_insufficient_money_title);
                dialog.setMessage(R.string.sweep_wallet_fragment_insufficient_money_msg);
                dialog.setNeutralButton(R.string.button_dismiss, null);
                dialog.show();
            }
        }.sendCoinsOffline(sendRequest); // send asynchronously
        //&end[SendCoins]
    }

    private static class FakeTransaction extends Transaction {
        private final Sha256Hash hash;

        public FakeTransaction(final NetworkParameters params, final Sha256Hash hash) {
            super(params);
            this.hash = hash;
        }

        @Override
        public Sha256Hash getHash() {
            return hash;
        }
    }
}
//&end[SweepPaperWallet]