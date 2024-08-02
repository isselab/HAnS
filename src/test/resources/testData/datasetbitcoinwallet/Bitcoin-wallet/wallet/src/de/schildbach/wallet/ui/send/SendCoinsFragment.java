/*
 * Copyright the original author or authors.
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bitcoin.protocols.payments.Protos.Payment;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionConfidence.ConfidenceType;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.VersionedChecksummedBytes;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.bitcoinj.utils.MonetaryFormat;
import org.bitcoinj.wallet.KeyChain.KeyPurpose;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.Wallet.BalanceType;
import org.bitcoinj.wallet.Wallet.CouldNotAdjustDownwards;
import org.bitcoinj.wallet.Wallet.DustySendRequested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import com.google.common.base.Joiner;

import de.schildbach.wallet.Configuration;
import de.schildbach.wallet.Constants;
import de.schildbach.wallet.R;
import de.schildbach.wallet.WalletApplication;
import de.schildbach.wallet.data.AddressBookDao;
import de.schildbach.wallet.data.AddressBookEntry;
import de.schildbach.wallet.data.AppDatabase;
import de.schildbach.wallet.data.ExchangeRate;
import de.schildbach.wallet.data.PaymentIntent;
import de.schildbach.wallet.data.PaymentIntent.Standard;
import de.schildbach.wallet.integration.android.BitcoinIntegration;
import de.schildbach.wallet.offline.DirectPaymentTask;
import de.schildbach.wallet.service.BlockchainService;
import de.schildbach.wallet.service.BlockchainState;
import de.schildbach.wallet.ui.AbstractWalletActivity;
import de.schildbach.wallet.ui.AddressAndLabel;
import de.schildbach.wallet.ui.CurrencyAmountView;
import de.schildbach.wallet.ui.CurrencyCalculatorLink;
import de.schildbach.wallet.ui.DialogBuilder;
import de.schildbach.wallet.ui.InputParser.BinaryInputParser;
import de.schildbach.wallet.ui.InputParser.StreamInputParser;
import de.schildbach.wallet.ui.InputParser.StringInputParser;
import de.schildbach.wallet.ui.ProgressDialogFragment;
import de.schildbach.wallet.ui.TransactionsAdapter;
import de.schildbach.wallet.ui.scan.ScanActivity;
import de.schildbach.wallet.util.Bluetooth;
import de.schildbach.wallet.util.Nfc;
import de.schildbach.wallet.util.WalletUtils;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.TextView;

//&begin[SendCoins]
/**
 * @author Andreas Schildbach
 */
public final class SendCoinsFragment extends Fragment {
    private AbstractWalletActivity activity;
    private WalletApplication application;
    private Configuration config;
	//&line[AddressBook]
    private AddressBookDao addressBookDao;
    private ContentResolver contentResolver;
    private FragmentManager fragmentManager;
    //&begin[Bluetooth]
    @Nullable
    private BluetoothAdapter bluetoothAdapter;
    //&end[Bluetooth]

    private final Handler handler = new Handler();
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    private View payeeGroup;
    private TextView payeeNameView;
    private TextView payeeVerifiedByView;
    private AutoCompleteTextView receivingAddressView;
    private ReceivingAddressViewAdapter receivingAddressViewAdapter;
    private View receivingStaticView;
    private TextView receivingStaticAddressView;
    private TextView receivingStaticLabelView;
    private View amountGroup;
    //&line[CurrencyCalculator]
    private CurrencyCalculatorLink amountCalculatorLink;
    private CheckBox directPaymentEnableView;

    private TextView hintView;
    private TextView directPaymentMessageView;
    private ViewGroup sentTransactionViewGroup;
    private TransactionsAdapter.TransactionViewHolder sentTransactionViewHolder;
    //&begin[SetPIN]
    private View privateKeyPasswordViewGroup;
    private EditText privateKeyPasswordView;
    private View privateKeyBadPasswordView;
    //&end[SetPIN]
    private Button viewGo;
    private Button viewCancel;

    private static final int REQUEST_CODE_SCAN = 0;
    //&begin[Bluetooth]
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH_FOR_PAYMENT_REQUEST = 1;
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH_FOR_DIRECT_PAYMENT = 2;
    //&end[Bluetooth]

    private SendCoinsViewModel viewModel;

    private static final Logger log = LoggerFactory.getLogger(SendCoinsFragment.class);

    private final class ReceivingAddressListener
            implements OnFocusChangeListener, TextWatcher, AdapterView.OnItemClickListener {
        @Override
        public void onFocusChange(final View v, final boolean hasFocus) {
            if (!hasFocus) {
                validateReceivingAddress();
                updateView();
            }
        }

        @Override
        public void afterTextChanged(final Editable s) {
            final String constraint = s.toString().trim();
            if (!constraint.isEmpty())
                validateReceivingAddress();
            else
                updateView();
        }

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
			//&begin[AddressBook]
            final AddressBookEntry entry = receivingAddressViewAdapter.getItem(position);
            try {
                viewModel.validatedAddress = new AddressAndLabel(Constants.NETWORK_PARAMETERS, entry.getAddress(),
                        entry.getLabel());
                receivingAddressView.setText(null);
                log.info("Picked valid address from suggestions: {}", viewModel.validatedAddress);
            } catch (final AddressFormatException x) {
                // swallow
            }
			//&end[AddressBook]
        }
    }

    private final ReceivingAddressListener receivingAddressListener = new ReceivingAddressListener();

    private final CurrencyAmountView.Listener amountsListener = new CurrencyAmountView.Listener() {
        @Override
        public void changed() {
            updateView();
            handler.post(dryrunRunnable);
        }

        @Override
        public void focusChanged(final boolean hasFocus) {
        }
    };

    //&begin[SetPIN]
    private final TextWatcher privateKeyPasswordListener = new TextWatcher() {
        @Override
        public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            privateKeyBadPasswordView.setVisibility(View.INVISIBLE);
            updateView();
        }

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        }

        @Override
        public void afterTextChanged(final Editable s) {
        }
    };
    //&end[SetPIN]

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
                    final ConfidenceType confidenceType = confidence.getConfidenceType();
                    final int numBroadcastPeers = confidence.numBroadcastPeers();

                    if (viewModel.state == SendCoinsViewModel.State.SENDING) {
                        if (confidenceType == ConfidenceType.DEAD) {
                            setState(SendCoinsViewModel.State.FAILED);
                        } else if (numBroadcastPeers > 1 || confidenceType == ConfidenceType.BUILDING) {
                            setState(SendCoinsViewModel.State.SENT);

                            // Auto-close the dialog after a short delay
                            if (config.getSendCoinsAutoclose()) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        activity.finish();
                                    }
                                }, 500);
                            }
                        }
                    }

                    if (reason == ChangeReason.SEEN_PEERS && confidenceType == ConfidenceType.PENDING) {
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

	//&begin[AddressBook]
    private final class ReceivingAddressViewAdapter extends ArrayAdapter<AddressBookEntry> {
        private final LayoutInflater inflater;

        public ReceivingAddressViewAdapter(final Context context) {
            super(context, 0);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View view, final ViewGroup parent) {
            if (view == null)
                view = inflater.inflate(R.layout.address_book_row, parent, false);
            final AddressBookEntry entry = getItem(position);
            ((TextView) view.findViewById(R.id.address_book_row_label)).setText(entry.getLabel());
            ((TextView) view.findViewById(R.id.address_book_row_address)).setText(WalletUtils.formatHash(
                    entry.getAddress(), Constants.ADDRESS_FORMAT_GROUP_SIZE, Constants.ADDRESS_FORMAT_LINE_SIZE));
            return view;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(final CharSequence constraint) {
                    final String trimmedConstraint = constraint.toString().trim();
                    final FilterResults results = new FilterResults();
                    if (viewModel.validatedAddress == null && !trimmedConstraint.isEmpty()) {
                        final List<AddressBookEntry> entries = addressBookDao.get(trimmedConstraint);
                        results.values = entries;
                        results.count = entries.size();
                    } else {
                        results.values = Collections.emptyList();
                        results.count = 0;
                    }
                    return results;
                }

                @Override
                protected void publishResults(final CharSequence constraint, final FilterResults results) {
                    setNotifyOnChange(false);
                    clear();
                    if (results.count > 0)
                        addAll((List<AddressBookEntry>) results.values);
                    notifyDataSetChanged();
                }
            };
        }
    }
	//&end[AddressBook]

    private final DialogInterface.OnClickListener activityDismissListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            activity.finish();
        }
    };

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        this.activity = (AbstractWalletActivity) context;
        this.application = activity.getWalletApplication();
        this.config = application.getConfiguration();
        //&line[AddressBook]
        this.addressBookDao = AppDatabase.getDatabase(context).addressBookDao();
        this.contentResolver = application.getContentResolver();
        this.fragmentManager = getFragmentManager();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        viewModel = ViewModelProviders.of(this).get(SendCoinsViewModel.class);
        viewModel.wallet.observe(this, new Observer<Wallet>() {
            @Override
            public void onChanged(final Wallet wallet) {
                updateView();
            }
        });
        //&begin[AddressBook]
        viewModel.addressBook.observe(this, new Observer<List<AddressBookEntry>>() {
            @Override
            public void onChanged(final List<AddressBookEntry> addressBook) {
                updateView();
            }
        });
        //&end[AddressBook]
        //&begin[ExchangeRates]
        if (Constants.ENABLE_EXCHANGE_RATES) {
            viewModel.exchangeRate.observe(this, new Observer<ExchangeRate>() {
                @Override
                public void onChanged(final ExchangeRate exchangeRate) {
                    final SendCoinsViewModel.State state = viewModel.state;
                    if (state == null || state.compareTo(SendCoinsViewModel.State.INPUT) <= 0)
                        //&line[CurrencyCalculator]
                        amountCalculatorLink.setExchangeRate(exchangeRate.rate);
                }
            });
        }
        //&end[ExchangeRates]
        //&line[Fee]
        viewModel.dynamicFees.observe(this, new Observer<Map<FeeCategory, Coin>>() {
            @Override
            //&line[Fee]
            public void onChanged(final Map<FeeCategory, Coin> dynamicFees) {
                updateView();
                handler.post(dryrunRunnable);
            }
        });
        viewModel.blockchainState.observe(this, new Observer<BlockchainState>() {
            @Override
            public void onChanged(final BlockchainState blockchainState) {
                updateView();
            }
        });
        //&begin[BitcoinBalance]
        viewModel.balance.observe(this, new Observer<Coin>() {
            @Override
            public void onChanged(final Coin coin) {
                activity.invalidateOptionsMenu();
            }
        });
        //&end[BitcoinBalance]

        //&line[Bluetooth]
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        backgroundThread = new HandlerThread("backgroundThread", Process.THREAD_PRIORITY_BACKGROUND);
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        if (savedInstanceState == null) {
            final Intent intent = activity.getIntent();
            final String action = intent.getAction();
            final Uri intentUri = intent.getData();
            final String scheme = intentUri != null ? intentUri.getScheme() : null;
            final String mimeType = intent.getType();

            //&begin[NFC]
            if ((Intent.ACTION_VIEW.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
                    && intentUri != null && "bitcoin".equals(scheme)) {
                initStateFromBitcoinUri(intentUri);
            } else if ((NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
                    && PaymentProtocol.MIMETYPE_PAYMENTREQUEST.equals(mimeType)) {
                final NdefMessage ndefMessage = (NdefMessage) intent
                        .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)[0];
                final byte[] ndefMessagePayload = Nfc.extractMimePayload(PaymentProtocol.MIMETYPE_PAYMENTREQUEST,
                        ndefMessage);
                initStateFromPaymentRequest(mimeType, ndefMessagePayload);
            //&end[NFC]
            } else if ((Intent.ACTION_VIEW.equals(action))
                    && PaymentProtocol.MIMETYPE_PAYMENTREQUEST.equals(mimeType)) {
                final byte[] paymentRequest = BitcoinIntegration.paymentRequestFromIntent(intent);

                if (intentUri != null)
                    initStateFromIntentUri(mimeType, intentUri);
                else if (paymentRequest != null)
                    initStateFromPaymentRequest(mimeType, paymentRequest);
                else
                    throw new IllegalArgumentException();
            } else if (intent.hasExtra(SendCoinsActivity.INTENT_EXTRA_PAYMENT_INTENT)) {
                initStateFromIntentExtras(intent.getExtras());
            } else {
                updateStateFrom(PaymentIntent.blank());
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.send_coins_fragment, container);

        payeeGroup = view.findViewById(R.id.send_coins_payee_group);

        payeeNameView = (TextView) view.findViewById(R.id.send_coins_payee_name);
        payeeVerifiedByView = (TextView) view.findViewById(R.id.send_coins_payee_verified_by);

        receivingAddressView = (AutoCompleteTextView) view.findViewById(R.id.send_coins_receiving_address);
        receivingAddressViewAdapter = new ReceivingAddressViewAdapter(activity);
        receivingAddressView.setAdapter(receivingAddressViewAdapter);
        receivingAddressView.setOnFocusChangeListener(receivingAddressListener);
        receivingAddressView.addTextChangedListener(receivingAddressListener);
        receivingAddressView.setOnItemClickListener(receivingAddressListener);

        receivingStaticView = view.findViewById(R.id.send_coins_receiving_static);
        receivingStaticAddressView = (TextView) view.findViewById(R.id.send_coins_receiving_static_address);
        receivingStaticLabelView = (TextView) view.findViewById(R.id.send_coins_receiving_static_label);

        amountGroup = view.findViewById(R.id.send_coins_amount_group);

        //&begin[Denomination]
        final CurrencyAmountView btcAmountView = (CurrencyAmountView) view.findViewById(R.id.send_coins_amount_btc);
        btcAmountView.setCurrencySymbol(config.getFormat().code());
        btcAmountView.setInputFormat(config.getMaxPrecisionFormat());
        btcAmountView.setHintFormat(config.getFormat());
        //&end[Denomination]

        final CurrencyAmountView localAmountView = (CurrencyAmountView) view.findViewById(R.id.send_coins_amount_local);
        localAmountView.setInputFormat(Constants.LOCAL_FORMAT);
        localAmountView.setHintFormat(Constants.LOCAL_FORMAT);
        //&begin[CurrencyCalculator]
        amountCalculatorLink = new CurrencyCalculatorLink(btcAmountView, localAmountView);
        //&line[ExchangeRates]
        amountCalculatorLink.setExchangeDirection(config.getLastExchangeDirection());
        //&end[CurrencyCalculator]

        directPaymentEnableView = (CheckBox) view.findViewById(R.id.send_coins_direct_payment_enable);
        directPaymentEnableView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            //&begin[Bluetooth]
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                //&begin[PaymentURL]
                //&begin[Bluetooth]
                if (viewModel.paymentIntent.isBluetoothPaymentUrl() && isChecked && !bluetoothAdapter.isEnabled()) {
                    // ask for permission to enable bluetooth
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                            REQUEST_CODE_ENABLE_BLUETOOTH_FOR_DIRECT_PAYMENT);
                }
                //&end[Bluetooth]
                //&end[PaymentURL]
            }
            //&end[Bluetooth]
        });

        hintView = (TextView) view.findViewById(R.id.send_coins_hint);

        directPaymentMessageView = (TextView) view.findViewById(R.id.send_coins_direct_payment_message);

        sentTransactionViewGroup = (FrameLayout) view.findViewById(R.id.transaction_row);
        sentTransactionViewGroup
                .setLayoutAnimation(AnimationUtils.loadLayoutAnimation(activity, R.anim.transaction_layout_anim));
        sentTransactionViewHolder = new TransactionsAdapter.TransactionViewHolder(view);

        //&begin[SetPIN]
        privateKeyPasswordViewGroup = view.findViewById(R.id.send_coins_private_key_password_group);
        privateKeyPasswordView = (EditText) view.findViewById(R.id.send_coins_private_key_password);
        privateKeyBadPasswordView = view.findViewById(R.id.send_coins_private_key_bad_password);
        //&end[SetPIN]

        viewGo = (Button) view.findViewById(R.id.send_coins_go);
        viewGo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                validateReceivingAddress();

                if (everythingPlausible())
                    handleGo();
                else
                    requestFocusFirst();

                updateView();
            }
        });

        viewCancel = (Button) view.findViewById(R.id.send_coins_cancel);
        viewCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                handleCancel();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //&begin[CurrencyCalculator]
        //&line[ExchangeRates]
        config.setLastExchangeDirection(amountCalculatorLink.getExchangeDirection());
        //&end[CurrencyCalculator]
    }

    @Override
    public void onResume() {
        super.onResume();

        //&line[CurrencyCalculator]
        amountCalculatorLink.setListener(amountsListener);
        //&line[SetPIN]
        privateKeyPasswordView.addTextChangedListener(privateKeyPasswordListener);

        updateView();
        handler.post(dryrunRunnable);
    }

    @Override
    public void onPause() {
        //&line[SetPIN]
        privateKeyPasswordView.removeTextChangedListener(privateKeyPasswordListener);
        //&line[CurrencyCalculator]
        amountCalculatorLink.setListener(null);

        super.onPause();
    }

    @Override
    public void onDetach() {
        handler.removeCallbacksAndMessages(null);

        super.onDetach();
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
        handler.post(new Runnable() {
            @Override
            public void run() {
                onActivityResultResumed(requestCode, resultCode, intent);
            }
        });
    }

    private void onActivityResultResumed(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                final String input = intent.getStringExtra(ScanActivity.INTENT_EXTRA_RESULT);

                new StringInputParser(input) {
                    @Override
                    protected void handlePaymentIntent(final PaymentIntent paymentIntent) {
                        setState(null);

                        updateStateFrom(paymentIntent);
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
        //&begin[Bluetooth]
        } else if (requestCode == REQUEST_CODE_ENABLE_BLUETOOTH_FOR_PAYMENT_REQUEST) {
            if (viewModel.paymentIntent.isBluetoothPaymentRequestUrl())
                requestPaymentRequest();
        } else if (requestCode == REQUEST_CODE_ENABLE_BLUETOOTH_FOR_DIRECT_PAYMENT) {
            //&begin[PaymentURL]
            if (viewModel.paymentIntent.isBluetoothPaymentUrl())
                directPaymentEnableView.setChecked(resultCode == Activity.RESULT_OK);
            //&end[PaymentURL]
        }
        //&end[Bluetooth]
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.send_coins_fragment_options, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        final MenuItem scanAction = menu.findItem(R.id.send_coins_options_scan);
        final PackageManager pm = activity.getPackageManager();
        scanAction.setVisible(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT));
        scanAction.setEnabled(viewModel.state == SendCoinsViewModel.State.INPUT);

        final MenuItem emptyAction = menu.findItem(R.id.send_coins_options_empty);
        emptyAction.setEnabled(viewModel.state == SendCoinsViewModel.State.INPUT
                //&line[BitcoinBalance]
                && viewModel.paymentIntent.mayEditAmount() && viewModel.balance.getValue() != null);

        //&begin[Fee]
        final MenuItem feeCategoryAction = menu.findItem(R.id.send_coins_options_fee_category);
        feeCategoryAction.setEnabled(viewModel.state == SendCoinsViewModel.State.INPUT);
        //&begin[Economic]
        if (viewModel.feeCategory == FeeCategory.ECONOMIC)
            menu.findItem(R.id.send_coins_options_fee_category_economic).setChecked(true);
        //&end[Economic]
        //&begin[Normal]
        else if (viewModel.feeCategory == FeeCategory.NORMAL)
            menu.findItem(R.id.send_coins_options_fee_category_normal).setChecked(true);
        //&end[Normal]
        //&begin[Priority]
        else if (viewModel.feeCategory == FeeCategory.PRIORITY)
            menu.findItem(R.id.send_coins_options_fee_category_priority).setChecked(true);
        //&end[Priority]
        //&end[Fee]

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.send_coins_options_scan:
            ScanActivity.startForResult(this, activity, REQUEST_CODE_SCAN);
            return true;

            //&begin[Fee]
            //&begin[Economic]
        case R.id.send_coins_options_fee_category_economic:
            handleFeeCategory(FeeCategory.ECONOMIC);
            return true;
            //&end[Economic]
            //&begin[Normal]
        case R.id.send_coins_options_fee_category_normal:
            handleFeeCategory(FeeCategory.NORMAL);
            return true;
            //&end[Normal]
            //&begin[Priority]
        case R.id.send_coins_options_fee_category_priority:
            handleFeeCategory(FeeCategory.PRIORITY);
            return true;
            //&end[Priority]
            //&end[Fee]

        case R.id.send_coins_options_empty:
            handleEmpty();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void validateReceivingAddress() {
        try {
            final String addressStr = receivingAddressView.getText().toString().trim();
            if (!addressStr.isEmpty()
                    && Constants.NETWORK_PARAMETERS.equals(Address.getParametersFromAddress(addressStr))) {
				//&line[AddressBook]
                final String label = addressBookDao.resolveLabel(addressStr);
                viewModel.validatedAddress = new AddressAndLabel(Constants.NETWORK_PARAMETERS, addressStr, label);
                receivingAddressView.setText(null);
                log.info("Locked to valid address: {}", viewModel.validatedAddress);
            }
        } catch (final AddressFormatException x) {
            // swallow
        }
    }

    private void handleCancel() {
        if (viewModel.state == null || viewModel.state.compareTo(SendCoinsViewModel.State.INPUT) <= 0)
            activity.setResult(Activity.RESULT_CANCELED);

        activity.finish();
    }

    private boolean isPayeePlausible() {
        if (viewModel.paymentIntent.hasOutputs())
            return true;

        if (viewModel.validatedAddress != null)
            return true;

        return false;
    }

    private boolean isAmountPlausible() {
        if (viewModel.dryrunTransaction != null)
            return viewModel.dryrunException == null;
        else if (viewModel.paymentIntent.mayEditAmount())
            //&line[CurrencyCalculator]
            return amountCalculatorLink.hasAmount();
        else
            return viewModel.paymentIntent.hasAmount();
    }

    //&begin[SetPIN]
    private boolean isPasswordPlausible() {
        final Wallet wallet = viewModel.wallet.getValue();
        if (wallet == null)
            return false;
        if (!wallet.isEncrypted())
            return true;
        return !privateKeyPasswordView.getText().toString().trim().isEmpty();
    }
    //&end[SetPIN]

    private boolean everythingPlausible() {
        return viewModel.state == SendCoinsViewModel.State.INPUT && isPayeePlausible() && isAmountPlausible()
                //&line[SetPIN]
                && isPasswordPlausible();
    }

    private void requestFocusFirst() {
        if (!isPayeePlausible())
            receivingAddressView.requestFocus();
        else if (!isAmountPlausible())
            //&line[CurrencyCalculator]
            amountCalculatorLink.requestFocus();
        //&begin[SetPIN]
        else if (!isPasswordPlausible())
            privateKeyPasswordView.requestFocus();
        //&end[SetPIN]
        else if (everythingPlausible())
            viewGo.requestFocus();
        else
            log.warn("unclear focus");
    }

    private void handleGo() {
        //&line[SetPIN]
        privateKeyBadPasswordView.setVisibility(View.INVISIBLE);

        final Wallet wallet = viewModel.wallet.getValue();
        if (wallet.isEncrypted()) {
            new DeriveKeyTask(backgroundHandler, application.scryptIterationsTarget()) {
                @Override
                protected void onSuccess(final KeyParameter encryptionKey, final boolean wasChanged) {
                    if (wasChanged)
                        //&line[BackupWallet]
                        WalletUtils.autoBackupWallet(activity, wallet);
                    signAndSendPayment(encryptionKey);
                }
            //&line[SetPIN]
            }.deriveKey(wallet, privateKeyPasswordView.getText().toString().trim());

            setState(SendCoinsViewModel.State.DECRYPTING);
        } else {
            signAndSendPayment(null);
        }
    }

    private void signAndSendPayment(final KeyParameter encryptionKey) {
        setState(SendCoinsViewModel.State.SIGNING);

        // final payment intent
        final PaymentIntent finalPaymentIntent = viewModel.paymentIntent.mergeWithEditedValues(
                //&line[CurrencyCalculator]
                amountCalculatorLink.getAmount(),
                viewModel.validatedAddress != null ? viewModel.validatedAddress.address : null);
        final Coin finalAmount = finalPaymentIntent.getAmount();

        // prepare send request
        //&line[Fee]
        final Map<FeeCategory, Coin> fees = viewModel.dynamicFees.getValue();
        final Wallet wallet = viewModel.wallet.getValue();
        final SendRequest sendRequest = finalPaymentIntent.toSendRequest();
        //&begin[EmptyWallet]
        sendRequest.emptyWallet = viewModel.paymentIntent.mayEditAmount()
                //&line[BitcoinBalance]
                && finalAmount.equals(wallet.getBalance(BalanceType.AVAILABLE));
        //&end[EmptyWallet]
        //&line[Fee]
        sendRequest.feePerKb = fees.get(viewModel.feeCategory);
        sendRequest.memo = viewModel.paymentIntent.memo;
        //&begin[CurrencyCalculator]
        //&line[ExchangeRates]
        sendRequest.exchangeRate = amountCalculatorLink.getExchangeRate();
        //&end[CurrencyCalculator]
        sendRequest.aesKey = encryptionKey;

        //&begin[Fee]
        final Coin fee = viewModel.dryrunTransaction.getFee();
        if (fee.isGreaterThan(finalAmount)) {
            setState(SendCoinsViewModel.State.INPUT);

            //&line[Denomination]
            final MonetaryFormat btcFormat = config.getFormat();
            final DialogBuilder dialog = DialogBuilder.warn(activity,
                    R.string.send_coins_fragment_significant_fee_title);
            dialog.setMessage(getString(R.string.send_coins_fragment_significant_fee_message, btcFormat.format(fee),
                    btcFormat.format(finalAmount)));
            dialog.setPositiveButton(R.string.send_coins_fragment_button_send, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    sendPayment(sendRequest, finalAmount);
                }
            });
            dialog.setNegativeButton(R.string.button_cancel, null);
            dialog.show();
        } else {
            sendPayment(sendRequest, finalAmount);
        }
        //&end[Fee]
    }

    private void sendPayment(final SendRequest sendRequest, final Coin finalAmount) {
        final Wallet wallet = viewModel.wallet.getValue();
        new SendCoinsOfflineTask(wallet, backgroundHandler) {
            @Override
            protected void onSuccess(final Transaction transaction) {
                viewModel.sentTransaction = transaction;

                setState(SendCoinsViewModel.State.SENDING);

                viewModel.sentTransaction.getConfidence().addEventListener(sentTransactionConfidenceListener);

				//&begin[Codecs]
                //&line[BIP70]
                final Address refundAddress = viewModel.paymentIntent.standard == Standard.BIP70
				//&end[Codecs]
                        ? wallet.freshAddress(KeyPurpose.REFUND) : null;
                final Payment payment = PaymentProtocol.createPaymentMessage(
                        Arrays.asList(new Transaction[] { viewModel.sentTransaction }), finalAmount, refundAddress,
                        null, viewModel.paymentIntent.payeeData);

                if (directPaymentEnableView.isChecked())
                    directPay(payment);

                //&line[BlockchainSync]
                BlockchainService.broadcastTransaction(activity, viewModel.sentTransaction);

                final ComponentName callingActivity = activity.getCallingActivity();
                if (callingActivity != null) {
                    log.info("returning result to calling activity: {}", callingActivity.flattenToString());

                    final Intent result = new Intent();
                    BitcoinIntegration.transactionHashToResult(result, viewModel.sentTransaction.getHashAsString());
					//&begin[Codecs]
                    //&line[BIP70]
                    if (viewModel.paymentIntent.standard == Standard.BIP70)
					//&end[Codecs]
                        BitcoinIntegration.paymentToResult(result, payment.toByteArray());
                    activity.setResult(Activity.RESULT_OK, result);
                }
            }

            private void directPay(final Payment payment) {
                final DirectPaymentTask.ResultCallback callback = new DirectPaymentTask.ResultCallback() {
                    @Override
                    public void onResult(final boolean ack) {
                        viewModel.directPaymentAck = ack;

                        if (viewModel.state == SendCoinsViewModel.State.SENDING)
                            setState(SendCoinsViewModel.State.SENT);

                        updateView();
                    }

                    @Override
                    public void onFail(final int messageResId, final Object... messageArgs) {
                        final DialogBuilder dialog = DialogBuilder.warn(activity,
                                R.string.send_coins_fragment_direct_payment_failed_title);
                        //&begin[PaymentURL]
                        dialog.setMessage(
                                viewModel.paymentIntent.paymentUrl + "\n" + getString(messageResId, messageArgs)
                                        + "\n\n" + getString(R.string.send_coins_fragment_direct_payment_failed_msg));
                        //&end[PaymentURL]
                        dialog.setPositiveButton(R.string.button_retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                directPay(payment);
                            }
                        });
                        dialog.setNegativeButton(R.string.button_dismiss, null);
                        dialog.show();
                    }
                };

                //&begin[PaymentURL]
                if (viewModel.paymentIntent.isHttpPaymentUrl()) {
                    new DirectPaymentTask.HttpPaymentTask(backgroundHandler, callback,
                            viewModel.paymentIntent.paymentUrl, application.httpUserAgent()).send(payment);
                //&begin[Bluetooth]
                } else if (viewModel.paymentIntent.isBluetoothPaymentUrl() && bluetoothAdapter != null
                        && bluetoothAdapter.isEnabled()) {
                    new DirectPaymentTask.BluetoothPaymentTask(backgroundHandler, callback, bluetoothAdapter,
                            Bluetooth.getBluetoothMac(viewModel.paymentIntent.paymentUrl)).send(payment);
                }
                //&end[Bluetooth]
                //&end[PaymentURL]
            }

            @Override
            protected void onInsufficientMoney(final Coin missing) {
                setState(SendCoinsViewModel.State.INPUT);

                //&begin[BitcoinBalance]
                final Coin estimated = wallet.getBalance(BalanceType.ESTIMATED);
                final Coin available = wallet.getBalance(BalanceType.AVAILABLE);
                //&end[BitcoinBalance]
                final Coin pending = estimated.subtract(available);

                //&line[Denomination]
                final MonetaryFormat btcFormat = config.getFormat();

                final DialogBuilder dialog = DialogBuilder.warn(activity,
                        R.string.send_coins_fragment_insufficient_money_title);
                final StringBuilder msg = new StringBuilder();
                msg.append(getString(R.string.send_coins_fragment_insufficient_money_msg1, btcFormat.format(missing)));

                if (pending.signum() > 0)
                    msg.append("\n\n")
                            .append(getString(R.string.send_coins_fragment_pending, btcFormat.format(pending)));
                if (viewModel.paymentIntent.mayEditAmount())
                    msg.append("\n\n").append(getString(R.string.send_coins_fragment_insufficient_money_msg2));
                dialog.setMessage(msg);
                if (viewModel.paymentIntent.mayEditAmount()) {
                    dialog.setPositiveButton(R.string.send_coins_options_empty, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            handleEmpty();
                        }
                    });
                    dialog.setNegativeButton(R.string.button_cancel, null);
                } else {
                    dialog.setNeutralButton(R.string.button_dismiss, null);
                }
                dialog.show();
            }

            @Override
            protected void onInvalidEncryptionKey() {
                setState(SendCoinsViewModel.State.INPUT);

                //&begin[SetPIN]
                privateKeyBadPasswordView.setVisibility(View.VISIBLE);
                privateKeyPasswordView.requestFocus();
                //&end[SetPIN]
            }

            //&begin[EmptyWallet]
            @Override
            protected void onEmptyWalletFailed() {
                setState(SendCoinsViewModel.State.INPUT);

                final DialogBuilder dialog = DialogBuilder.warn(activity,
                        R.string.send_coins_fragment_empty_wallet_failed_title);
                dialog.setMessage(R.string.send_coins_fragment_hint_empty_wallet_failed);
                dialog.setNeutralButton(R.string.button_dismiss, null);
                dialog.show();
            }
            //&end[EmptyWallet]

            @Override
            protected void onFailure(Exception exception) {
                setState(SendCoinsViewModel.State.FAILED);

                final DialogBuilder dialog = DialogBuilder.warn(activity, R.string.send_coins_error_msg);
                dialog.setMessage(exception.toString());
                dialog.setNeutralButton(R.string.button_dismiss, null);
                dialog.show();
            }
        }.sendCoinsOffline(sendRequest); // send asynchronously
    }

    //&begin[Fee]
    private void handleFeeCategory(final FeeCategory feeCategory) {
        viewModel.feeCategory = feeCategory;
        log.info("switching to {} fee category", feeCategory);

        updateView();
        handler.post(dryrunRunnable);
    }
    //&end[Fee]

    //&begin[EmptyWallet]
    private void handleEmpty() {
        //&line[BitcoinBalance]
        final Coin available = viewModel.balance.getValue();
        //&line[CurrencyCalculator]
        amountCalculatorLink.setBtcAmount(available);

        updateView();
        handler.post(dryrunRunnable);
    }
    //&end[EmptyWallet]

    private Runnable dryrunRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewModel.state == SendCoinsViewModel.State.INPUT)
                executeDryrun();

            updateView();
        }

        private void executeDryrun() {
            viewModel.dryrunTransaction = null;
            viewModel.dryrunException = null;

            final Wallet wallet = viewModel.wallet.getValue();
            final Map<FeeCategory, Coin> fees = viewModel.dynamicFees.getValue();
            //&line[CurrencyCalculator]
            final Coin amount = amountCalculatorLink.getAmount();
            if (amount != null && fees != null) {
                try {
                    final Address dummy = wallet.currentReceiveAddress(); // won't be used, tx is never
                                                                          // committed
                    final SendRequest sendRequest = viewModel.paymentIntent.mergeWithEditedValues(amount, dummy)
                            .toSendRequest();
                    sendRequest.signInputs = false;
                    //&begin[EmptyWallet]
                    sendRequest.emptyWallet = viewModel.paymentIntent.mayEditAmount()
                            //&line[BitcoinBalance]
                            && amount.equals(wallet.getBalance(BalanceType.AVAILABLE));
                    //&end[EmptyWallet]
                    sendRequest.feePerKb = fees.get(viewModel.feeCategory);
                    wallet.completeTx(sendRequest);
                    viewModel.dryrunTransaction = sendRequest.tx;
                } catch (final Exception x) {
                    viewModel.dryrunException = x;
                }
            }
        }
    };

    private void setState(final SendCoinsViewModel.State state) {
        viewModel.state = state;

        activity.invalidateOptionsMenu();
        updateView();
    }

    private void updateView() {
        final Wallet wallet = viewModel.wallet.getValue();
        //&line[Fee]
        final Map<FeeCategory, Coin> fees = viewModel.dynamicFees.getValue();
        final BlockchainState blockchainState = viewModel.blockchainState.getValue();
		//&line[AddressBook]
        final Map<String, AddressBookEntry> addressBook = AddressBookEntry.asMap(viewModel.addressBook.getValue());

        if (viewModel.paymentIntent != null) {
            //&line[Denomination]
            final MonetaryFormat btcFormat = config.getFormat();

            getView().setVisibility(View.VISIBLE);

            if (viewModel.paymentIntent.hasPayee()) {
                payeeNameView.setVisibility(View.VISIBLE);
                payeeNameView.setText(viewModel.paymentIntent.payeeName);

                payeeVerifiedByView.setVisibility(View.VISIBLE);
                final String verifiedBy = viewModel.paymentIntent.payeeVerifiedBy != null
                        ? viewModel.paymentIntent.payeeVerifiedBy
                        : getString(R.string.send_coins_fragment_payee_verified_by_unknown);
                payeeVerifiedByView.setText(Constants.CHAR_CHECKMARK
                        + String.format(getString(R.string.send_coins_fragment_payee_verified_by), verifiedBy));
            } else {
                payeeNameView.setVisibility(View.GONE);
                payeeVerifiedByView.setVisibility(View.GONE);
            }

            if (viewModel.paymentIntent.hasOutputs()) {
                payeeGroup.setVisibility(View.VISIBLE);
                receivingAddressView.setVisibility(View.GONE);
                receivingStaticView.setVisibility(
                        !viewModel.paymentIntent.hasPayee() || viewModel.paymentIntent.payeeVerifiedBy == null
                                ? View.VISIBLE : View.GONE);

                receivingStaticLabelView.setText(viewModel.paymentIntent.memo);

                if (viewModel.paymentIntent.hasAddress())
                    receivingStaticAddressView.setText(WalletUtils.formatAddress(viewModel.paymentIntent.getAddress(),
                            Constants.ADDRESS_FORMAT_GROUP_SIZE, Constants.ADDRESS_FORMAT_LINE_SIZE));
                else
                    receivingStaticAddressView.setText(R.string.send_coins_fragment_receiving_address_complex);
            } else if (viewModel.validatedAddress != null) {
                payeeGroup.setVisibility(View.VISIBLE);
                receivingAddressView.setVisibility(View.GONE);
                receivingStaticView.setVisibility(View.VISIBLE);

                receivingStaticAddressView.setText(WalletUtils.formatAddress(viewModel.validatedAddress.address,
                        Constants.ADDRESS_FORMAT_GROUP_SIZE, Constants.ADDRESS_FORMAT_LINE_SIZE));
				//&begin[AddressBook]
                final String addressBookLabel = addressBookDao
						//&begin[Codecs]
                        //&line[base58]
                        .resolveLabel(viewModel.validatedAddress.address.toBase58());
						//&end[Codecs]
                final String staticLabel;
                if (addressBookLabel != null)
                    staticLabel = addressBookLabel;
				//&end[AddressBook]
                else if (viewModel.validatedAddress.label != null)
                    staticLabel = viewModel.validatedAddress.label;
                else
                    staticLabel = getString(R.string.address_unlabeled);
                receivingStaticLabelView.setText(staticLabel);
                receivingStaticLabelView.setTextColor(getResources().getColor(
                        viewModel.validatedAddress.label != null ? R.color.fg_significant : R.color.fg_insignificant));
            } else if (viewModel.paymentIntent.standard == null) {
                payeeGroup.setVisibility(View.VISIBLE);
                receivingStaticView.setVisibility(View.GONE);
                receivingAddressView.setVisibility(View.VISIBLE);
            } else {
                payeeGroup.setVisibility(View.GONE);
            }

            receivingAddressView.setEnabled(viewModel.state == SendCoinsViewModel.State.INPUT);

            amountGroup.setVisibility(viewModel.paymentIntent.hasAmount()
                    || (viewModel.state != null && viewModel.state.compareTo(SendCoinsViewModel.State.INPUT) >= 0)
                            ? View.VISIBLE : View.GONE);
            //&begin[CurrencyCalculator]
            amountCalculatorLink.setEnabled(
                    viewModel.state == SendCoinsViewModel.State.INPUT && viewModel.paymentIntent.mayEditAmount());
            //&end[CurrencyCalculator]

            final boolean directPaymentVisible;
            //&begin[PaymentURL]
            if (viewModel.paymentIntent.hasPaymentUrl()) {
                //&begin[Bluetooth]
                if (viewModel.paymentIntent.isBluetoothPaymentUrl())
                    directPaymentVisible = bluetoothAdapter != null;
                else
                    directPaymentVisible = true;
                //&end[Bluetooth]
            } else {
                directPaymentVisible = false;
            }
            //&end[PaymentURL]
            directPaymentEnableView.setVisibility(directPaymentVisible ? View.VISIBLE : View.GONE);
            directPaymentEnableView.setEnabled(viewModel.state == SendCoinsViewModel.State.INPUT);

            hintView.setVisibility(View.GONE);
            if (viewModel.state == SendCoinsViewModel.State.INPUT) {
                if (blockchainState != null && blockchainState.replaying) {
                    hintView.setTextColor(getResources().getColor(R.color.fg_error));
                    hintView.setVisibility(View.VISIBLE);
                    hintView.setText(R.string.send_coins_fragment_hint_replaying);
                } else if (viewModel.paymentIntent.mayEditAddress() && viewModel.validatedAddress == null
                        && !receivingAddressView.getText().toString().trim().isEmpty()) {
                    hintView.setTextColor(getResources().getColor(R.color.fg_error));
                    hintView.setVisibility(View.VISIBLE);
                    hintView.setText(R.string.send_coins_fragment_receiving_address_error);
                } else if (viewModel.dryrunException != null) {
                    hintView.setTextColor(getResources().getColor(R.color.fg_error));
                    hintView.setVisibility(View.VISIBLE);
                    //&begin[DustSpamProtection]
                    if (viewModel.dryrunException instanceof DustySendRequested)
                        hintView.setText(getString(R.string.send_coins_fragment_hint_dusty_send));
                    //&end[DustSpamProtection]
                    else if (viewModel.dryrunException instanceof InsufficientMoneyException)
                        hintView.setText(getString(R.string.send_coins_fragment_hint_insufficient_money,
                                btcFormat.format(((InsufficientMoneyException) viewModel.dryrunException).missing)));
                    else if (viewModel.dryrunException instanceof CouldNotAdjustDownwards)
                        //&line[EmptyWallet]
                        hintView.setText(getString(R.string.send_coins_fragment_hint_empty_wallet_failed));
                    else
                        hintView.setText(viewModel.dryrunException.toString());
                //&begin[Fee]
                } else if (viewModel.dryrunTransaction != null && viewModel.dryrunTransaction.getFee() != null) {
                    hintView.setVisibility(View.VISIBLE);
                    final int hintResId;
                    final int colorResId;
                    //&begin[Economic]
                    if (viewModel.feeCategory == FeeCategory.ECONOMIC) {
                        hintResId = R.string.send_coins_fragment_hint_fee_economic;
                        colorResId = R.color.fg_less_significant;
                    //&end[Economic]
                    //&begin[Priority]
                    } else if (viewModel.feeCategory == FeeCategory.PRIORITY) {
                        hintResId = R.string.send_coins_fragment_hint_fee_priority;
                        colorResId = R.color.fg_less_significant;
                    //&end[Priority]
                    //&begin[Normal]
                    } else {
                        hintResId = R.string.send_coins_fragment_hint_fee;
                        colorResId = R.color.fg_insignificant;
                    }
                    //&end[Normal]
                    hintView.setTextColor(getResources().getColor(colorResId));
                    hintView.setText(getString(hintResId, btcFormat.format(viewModel.dryrunTransaction.getFee())));
                //&end[Fee]
                } else if (viewModel.paymentIntent.mayEditAddress() && viewModel.validatedAddress != null
                        && wallet != null && wallet.isPubKeyHashMine(viewModel.validatedAddress.address.getHash160())) {
                    hintView.setTextColor(getResources().getColor(R.color.fg_insignificant));
                    hintView.setVisibility(View.VISIBLE);
                    hintView.setText(R.string.send_coins_fragment_receiving_address_own);
                }
            }

            if (viewModel.sentTransaction != null && wallet != null) {
                sentTransactionViewGroup.setVisibility(View.VISIBLE);
                sentTransactionViewHolder
                        .bind(new TransactionsAdapter.ListItem.TransactionItem(activity, viewModel.sentTransaction,
								//&line[AddressBook]
                                wallet, addressBook, btcFormat, application.maxConnectedPeers(), false));
            } else {
                sentTransactionViewGroup.setVisibility(View.GONE);
            }

            if (viewModel.directPaymentAck != null) {
                directPaymentMessageView.setVisibility(View.VISIBLE);
                directPaymentMessageView
                        .setText(viewModel.directPaymentAck ? R.string.send_coins_fragment_direct_payment_ack
                                : R.string.send_coins_fragment_direct_payment_nack);
            } else {
                directPaymentMessageView.setVisibility(View.GONE);
            }

            viewCancel.setEnabled(viewModel.state != SendCoinsViewModel.State.REQUEST_PAYMENT_REQUEST
                    && viewModel.state != SendCoinsViewModel.State.DECRYPTING
                    && viewModel.state != SendCoinsViewModel.State.SIGNING);
            viewGo.setEnabled(everythingPlausible() && viewModel.dryrunTransaction != null && wallet != null
                    //&line[Fee]
                    && fees != null && (blockchainState == null || !blockchainState.replaying));

            if (viewModel.state == null || viewModel.state == SendCoinsViewModel.State.REQUEST_PAYMENT_REQUEST) {
                viewCancel.setText(R.string.button_cancel);
                viewGo.setText(null);
            } else if (viewModel.state == SendCoinsViewModel.State.INPUT) {
                viewCancel.setText(R.string.button_cancel);
                viewGo.setText(R.string.send_coins_fragment_button_send);
            } else if (viewModel.state == SendCoinsViewModel.State.DECRYPTING) {
                viewCancel.setText(R.string.button_cancel);
                viewGo.setText(R.string.send_coins_fragment_state_decrypting);
            } else if (viewModel.state == SendCoinsViewModel.State.SIGNING) {
                viewCancel.setText(R.string.button_cancel);
                viewGo.setText(R.string.send_coins_preparation_msg);
            } else if (viewModel.state == SendCoinsViewModel.State.SENDING) {
                viewCancel.setText(R.string.send_coins_fragment_button_back);
                viewGo.setText(R.string.send_coins_sending_msg);
            } else if (viewModel.state == SendCoinsViewModel.State.SENT) {
                viewCancel.setText(R.string.send_coins_fragment_button_back);
                viewGo.setText(R.string.send_coins_sent_msg);
            } else if (viewModel.state == SendCoinsViewModel.State.FAILED) {
                viewCancel.setText(R.string.send_coins_fragment_button_back);
                viewGo.setText(R.string.send_coins_failed_msg);
            }

            //&begin[SetPIN]
            final boolean privateKeyPasswordViewVisible = (viewModel.state == SendCoinsViewModel.State.INPUT
                    || viewModel.state == SendCoinsViewModel.State.DECRYPTING) && wallet != null
                    && wallet.isEncrypted();
            privateKeyPasswordViewGroup.setVisibility(privateKeyPasswordViewVisible ? View.VISIBLE : View.GONE);
            privateKeyPasswordView.setEnabled(viewModel.state == SendCoinsViewModel.State.INPUT);
            //&end[SetPIN]

            // focus linking
            //&line[CurrencyCalculator]
            final int activeAmountViewId = amountCalculatorLink.activeTextView().getId();
            receivingAddressView.setNextFocusDownId(activeAmountViewId);
            receivingAddressView.setNextFocusForwardId(activeAmountViewId);
            //&begin[CurrencyCalculator]
            amountCalculatorLink.setNextFocusId(
                    privateKeyPasswordViewVisible ? R.id.send_coins_private_key_password : R.id.send_coins_go);
            //&end[CurrencyCalculator]
            //&begin[SetPIN]
            privateKeyPasswordView.setNextFocusUpId(activeAmountViewId);
            privateKeyPasswordView.setNextFocusDownId(R.id.send_coins_go);
            privateKeyPasswordView.setNextFocusForwardId(R.id.send_coins_go);
            viewGo.setNextFocusUpId(
                    privateKeyPasswordViewVisible ? R.id.send_coins_private_key_password : activeAmountViewId);
            //&end[SetPIN]
        } else {
            getView().setVisibility(View.GONE);
        }
    }

    private void initStateFromIntentExtras(final Bundle extras) {
        final PaymentIntent paymentIntent = extras.getParcelable(SendCoinsActivity.INTENT_EXTRA_PAYMENT_INTENT);
        final FeeCategory feeCategory = (FeeCategory) extras
                .getSerializable(SendCoinsActivity.INTENT_EXTRA_FEE_CATEGORY);

        if (feeCategory != null) {
            log.info("got fee category {}", feeCategory);
            viewModel.feeCategory = feeCategory;
        }

        updateStateFrom(paymentIntent);
    }

    private void initStateFromBitcoinUri(final Uri bitcoinUri) {
        final String input = bitcoinUri.toString();

        new StringInputParser(input) {
            @Override
            protected void handlePaymentIntent(final PaymentIntent paymentIntent) {
                updateStateFrom(paymentIntent);
            }

            @Override
            protected void handlePrivateKey(final VersionedChecksummedBytes key) {
                throw new UnsupportedOperationException();
            }

            @Override
            protected void handleDirectTransaction(final Transaction transaction) throws VerificationException {
                throw new UnsupportedOperationException();
            }

            @Override
            protected void error(final int messageResId, final Object... messageArgs) {
                dialog(activity, activityDismissListener, 0, messageResId, messageArgs);
            }
        }.parse();
    }

    private void initStateFromPaymentRequest(final String mimeType, final byte[] input) {
        new BinaryInputParser(mimeType, input) {
            @Override
            protected void handlePaymentIntent(final PaymentIntent paymentIntent) {
                updateStateFrom(paymentIntent);
            }

            @Override
            protected void error(final int messageResId, final Object... messageArgs) {
                dialog(activity, activityDismissListener, 0, messageResId, messageArgs);
            }
        }.parse();
    }

    private void initStateFromIntentUri(final String mimeType, final Uri bitcoinUri) {
        try {
            final InputStream is = contentResolver.openInputStream(bitcoinUri);

            new StreamInputParser(mimeType, is) {
                @Override
                protected void handlePaymentIntent(final PaymentIntent paymentIntent) {
                    updateStateFrom(paymentIntent);
                }

                @Override
                protected void error(final int messageResId, final Object... messageArgs) {
                    dialog(activity, activityDismissListener, 0, messageResId, messageArgs);
                }
            }.parse();
        } catch (final FileNotFoundException x) {
            throw new RuntimeException(x);
        }
    }

    private void updateStateFrom(final PaymentIntent paymentIntent) {
        log.info("got {}", paymentIntent);

        viewModel.paymentIntent = paymentIntent;

        viewModel.validatedAddress = null;
        viewModel.directPaymentAck = null;

        // delay these actions until fragment is resumed
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (paymentIntent.hasPaymentRequestUrl() && paymentIntent.isBluetoothPaymentRequestUrl()) {
                    //&begin[Bluetooth]
                    if (bluetoothAdapter.isEnabled())
                        requestPaymentRequest();
                    else
                        // ask for permission to enable bluetooth
                        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                                REQUEST_CODE_ENABLE_BLUETOOTH_FOR_PAYMENT_REQUEST);
                    //&end[Bluetooth]
                } else if (paymentIntent.hasPaymentRequestUrl() && paymentIntent.isHttpPaymentRequestUrl()) {
                    requestPaymentRequest();
                } else {
                    setState(SendCoinsViewModel.State.INPUT);

                    receivingAddressView.setText(null);
                    //&line[CurrencyCalculator]
                    amountCalculatorLink.setBtcAmount(paymentIntent.getAmount());

                    //&begin[Bluetooth]
                    //&begin[PaymentURL]
                    if (paymentIntent.isBluetoothPaymentUrl())
                        directPaymentEnableView.setChecked(bluetoothAdapter != null && bluetoothAdapter.isEnabled());
                    else if (paymentIntent.isHttpPaymentUrl())
                        directPaymentEnableView.setChecked(true);
                    //&end[PaymentURL]
                    //&end[Bluetooth]

                    requestFocusFirst();
                    updateView();
                    handler.post(dryrunRunnable);
                }
            }
        });
    }

    private void requestPaymentRequest() {
        final String paymentRequestHost;
        //&begin[Bluetooth]
        if (!Bluetooth.isBluetoothUrl(viewModel.paymentIntent.paymentRequestUrl))
            paymentRequestHost = Uri.parse(viewModel.paymentIntent.paymentRequestUrl).getHost();
        else
            paymentRequestHost = Bluetooth
                    .decompressMac(Bluetooth.getBluetoothMac(viewModel.paymentIntent.paymentRequestUrl));
        //&end[Bluetooth]

        ProgressDialogFragment.showProgress(fragmentManager,
                getString(R.string.send_coins_fragment_request_payment_request_progress, paymentRequestHost));
        setState(SendCoinsViewModel.State.REQUEST_PAYMENT_REQUEST);

        final RequestPaymentRequestTask.ResultCallback callback = new RequestPaymentRequestTask.ResultCallback() {
			//&begin[Codecs]
            //&begin[BIP72]
            @Override
            public void onPaymentIntent(final PaymentIntent paymentIntent) {
                ProgressDialogFragment.dismissProgress(fragmentManager);

                if (viewModel.paymentIntent.isExtendedBy(paymentIntent)) {
                    // success
                    setState(SendCoinsViewModel.State.INPUT);
                    updateStateFrom(paymentIntent);
                    updateView();
                    handler.post(dryrunRunnable);
                } else {
                    final List<String> reasons = new LinkedList<>();
                    if (!viewModel.paymentIntent.equalsAddress(paymentIntent))
                        reasons.add("address");
                    if (!viewModel.paymentIntent.equalsAmount(paymentIntent))
                        reasons.add("amount");
                    if (reasons.isEmpty())
                        reasons.add("unknown");

                    final DialogBuilder dialog = DialogBuilder.warn(activity,
                            R.string.send_coins_fragment_request_payment_request_failed_title);
                    dialog.setMessage(getString(R.string.send_coins_fragment_request_payment_request_failed_message,
                            paymentRequestHost, Joiner.on(", ").join(reasons)));
                    dialog.singleDismissButton(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            handleCancel();
                        }
                    });
                    dialog.show();

                    log.info("BIP72 trust check failed: {}", reasons);
                }
            }
            //&end[BIP72]
			//&end[Codecs]

            @Override
            public void onFail(final int messageResId, final Object... messageArgs) {
                ProgressDialogFragment.dismissProgress(fragmentManager);

                final DialogBuilder dialog = DialogBuilder.warn(activity,
                        R.string.send_coins_fragment_request_payment_request_failed_title);
                dialog.setMessage(getString(messageResId, messageArgs));
                dialog.setPositiveButton(R.string.button_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        requestPaymentRequest();
                    }
                });
                dialog.setNegativeButton(R.string.button_dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        if (!viewModel.paymentIntent.hasOutputs())
                            handleCancel();
                        else
                            setState(SendCoinsViewModel.State.INPUT);
                    }
                });
                dialog.show();
            }
        };

        //&begin[Bluetooth]
        if (!Bluetooth.isBluetoothUrl(viewModel.paymentIntent.paymentRequestUrl))
            new RequestPaymentRequestTask.HttpRequestTask(backgroundHandler, callback, application.httpUserAgent())
                    .requestPaymentRequest(viewModel.paymentIntent.paymentRequestUrl);
        else
            new RequestPaymentRequestTask.BluetoothRequestTask(backgroundHandler, callback, bluetoothAdapter)
                    .requestPaymentRequest(viewModel.paymentIntent.paymentRequestUrl);
        //&end[Bluetooth]
    }
}
//&end[SendCoins]
