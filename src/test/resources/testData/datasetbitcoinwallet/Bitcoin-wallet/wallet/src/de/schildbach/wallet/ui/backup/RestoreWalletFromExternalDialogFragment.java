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

package de.schildbach.wallet.ui.backup;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bitcoinj.core.Coin;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.schildbach.wallet.Configuration;
import de.schildbach.wallet.Constants;
import de.schildbach.wallet.R;
import de.schildbach.wallet.WalletApplication;
import de.schildbach.wallet.service.BlockchainService;
import de.schildbach.wallet.ui.AbstractWalletActivity;
import de.schildbach.wallet.ui.DialogBuilder;
import de.schildbach.wallet.ui.ShowPasswordCheckListener;
import de.schildbach.wallet.util.Crypto;
import de.schildbach.wallet.util.Io;
import de.schildbach.wallet.util.WalletUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

//&begin[RestoreWallet]
/**
 * @author Andreas Schildbach
 */
public class RestoreWalletFromExternalDialogFragment extends DialogFragment {
    private static final String FRAGMENT_TAG = RestoreWalletFromExternalDialogFragment.class.getName();
    private static final String KEY_BACKUP_URI = "backup_uri";

    public static void show(final FragmentManager fm, final Uri backupUri) {
        final DialogFragment newFragment = new RestoreWalletFromExternalDialogFragment();
        final Bundle args = new Bundle();
        args.putParcelable(KEY_BACKUP_URI, backupUri);
        newFragment.setArguments(args);
        newFragment.show(fm, FRAGMENT_TAG);
    }

    private AbstractWalletActivity activity;
    private WalletApplication application;
    private ContentResolver contentResolver;
    private Configuration config;
    private Uri backupUri;

    //&line[SetPIN]
    private EditText passwordView;
    private CheckBox showView;
    private View replaceWarningView;

    private RestoreWalletViewModel viewModel;

    private static final Logger log = LoggerFactory.getLogger(RestoreWalletFromExternalDialogFragment.class);

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        this.activity = (AbstractWalletActivity) context;
        this.application = activity.getWalletApplication();
        this.contentResolver = application.getContentResolver();
        this.config = application.getConfiguration();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.backupUri = (Uri) getArguments().getParcelable(KEY_BACKUP_URI);

        viewModel = ViewModelProviders.of(this).get(RestoreWalletViewModel.class);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final View view = LayoutInflater.from(activity).inflate(R.layout.restore_wallet_from_external_dialog, null);
        //&line[SetPIN]
        passwordView = (EditText) view.findViewById(R.id.import_keys_from_content_dialog_password);
        showView = (CheckBox) view.findViewById(R.id.import_keys_from_content_dialog_show);
        replaceWarningView = view.findViewById(R.id.restore_wallet_from_content_dialog_replace_warning);

        final DialogBuilder builder = new DialogBuilder(activity);
        builder.setTitle(R.string.import_keys_dialog_title);
        builder.setView(view);
        builder.setPositiveButton(R.string.import_keys_dialog_button_import, new OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                //&begin[SetPIN]
                final String password = passwordView.getText().toString().trim();
                passwordView.setText(null); // get rid of it asap
                handleRestore(password);
                //&end[SetPIN]
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                //&line[SetPIN]
                passwordView.setText(null); // get rid of it asap
                activity.finish();
            }
        });
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                //&line[SetPIN]
                passwordView.setText(null); // get rid of it asap
                activity.finish();
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(final DialogInterface d) {
                //&begin[SetPIN]
                final ImportDialogButtonEnablerListener dialogButtonEnabler = new ImportDialogButtonEnablerListener(
                        passwordView, dialog) {
                    @Override
                    protected boolean hasFile() {
                        return true;
                    }
                };
                passwordView.addTextChangedListener(dialogButtonEnabler);
                showView.setOnCheckedChangeListener(new ShowPasswordCheckListener(passwordView));
                //&end[SetPIN]

                //&begin[BitcoinBalance]
                viewModel.balance.observe(RestoreWalletFromExternalDialogFragment.this, new Observer<Coin>() {
                    @Override
                    public void onChanged(final Coin balance) {
                        final boolean hasCoins = balance.signum() > 0;
                        replaceWarningView.setVisibility(hasCoins ? View.VISIBLE : View.GONE);
                    }
                });
                //&end[BitcoinBalance]
            }
        });

        return dialog;
    }

    //&line[SetPIN]
    private void handleRestore(final String password) {
        try {
            final InputStream is = contentResolver.openInputStream(backupUri);
            //&line[SetPIN]
            final Wallet restoredWallet = restoreWalletFromEncrypted(is, password);
            application.replaceWallet(restoredWallet);
            config.disarmBackupReminder();
            SuccessDialogFragment.showDialog(getFragmentManager(), restoredWallet.isEncrypted());
            log.info("successfully restored encrypted wallet from external source");
        } catch (final IOException x) {
            FailureDialogFragment.showDialog(getFragmentManager(), x.getMessage(), backupUri);
            log.info("problem restoring wallet", x);
        }
    }

    //&line[SetPIN]
    private Wallet restoreWalletFromEncrypted(final InputStream cipher, final String password) throws IOException {
        final BufferedReader cipherIn = new BufferedReader(new InputStreamReader(cipher, StandardCharsets.UTF_8));
        final StringBuilder cipherText = new StringBuilder();
        //&line[BackupWallet]
        Io.copy(cipherIn, cipherText, Constants.BACKUP_MAX_CHARS);
        cipherIn.close();

        //&line[SetPIN]
        final byte[] plainText = Crypto.decryptBytes(cipherText.toString(), password.toCharArray());
        final InputStream is = new ByteArrayInputStream(plainText);

		//&begin[Codecs]
        //&line[base58]
        return WalletUtils.restoreWalletFromProtobufOrBase58(is, Constants.NETWORK_PARAMETERS);
		//&end[Codecs]
    }

    public static class SuccessDialogFragment extends DialogFragment {
        private static final String FRAGMENT_TAG = SuccessDialogFragment.class.getName();
        private static final String KEY_SHOW_ENCRYPTED_MESSAGE = "show_encrypted_message";

        private Activity activity;

        public static void showDialog(final FragmentManager fm, final boolean showEncryptedMessage) {
            final DialogFragment newFragment = new SuccessDialogFragment();
            final Bundle args = new Bundle();
            args.putBoolean(KEY_SHOW_ENCRYPTED_MESSAGE, showEncryptedMessage);
            newFragment.setArguments(args);
            newFragment.show(fm, FRAGMENT_TAG);
        }

        @Override
        public void onAttach(final Context context) {
            super.onAttach(context);
            this.activity = (Activity) context;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final boolean showEncryptedMessage = getArguments().getBoolean(KEY_SHOW_ENCRYPTED_MESSAGE);
            final DialogBuilder dialog = new DialogBuilder(activity);
            final StringBuilder message = new StringBuilder();
            message.append(getString(R.string.restore_wallet_dialog_success));
            message.append("\n\n");
            message.append(getString(R.string.restore_wallet_dialog_success_replay));
            if (showEncryptedMessage) {
                message.append("\n\n");
                message.append(getString(R.string.restore_wallet_dialog_success_encrypted));
            }
            dialog.setMessage(message);
            dialog.setNeutralButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int id) {
                    //&line[ResetBlockChain]
                    BlockchainService.resetBlockchain(activity);
                    activity.finish();
                }
            });
            return dialog.create();
        }
    }

    public static class FailureDialogFragment extends DialogFragment {
        private static final String FRAGMENT_TAG = FailureDialogFragment.class.getName();
        private static final String KEY_EXCEPTION_MESSAGE = "exception_message";
        private static final String KEY_BACKUP_URI = "backup_uri";

        private Activity activity;

        public static void showDialog(final FragmentManager fm, final String exceptionMessage, final Uri backupUri) {
            final DialogFragment newFragment = new FailureDialogFragment();
            final Bundle args = new Bundle();
            args.putString(KEY_EXCEPTION_MESSAGE, exceptionMessage);
            args.putParcelable(KEY_BACKUP_URI, backupUri);
            newFragment.setArguments(args);
            newFragment.show(fm, FRAGMENT_TAG);
        }

        @Override
        public void onAttach(final Context context) {
            super.onAttach(context);
            this.activity = (Activity) context;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final String exceptionMessage = getArguments().getString(KEY_EXCEPTION_MESSAGE);
            final Uri backupUri = getArguments().getParcelable(KEY_BACKUP_URI);
            final DialogBuilder dialog = DialogBuilder.warn(getContext(),
                    R.string.import_export_keys_dialog_failure_title);
            dialog.setMessage(getString(R.string.import_keys_dialog_failure, exceptionMessage));
            dialog.setPositiveButton(R.string.button_dismiss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    activity.finish();
                }
            });
            dialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(final DialogInterface dialog) {
                    activity.finish();
                }
            });
            dialog.setNegativeButton(R.string.button_retry, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int id) {
                    RestoreWalletFromExternalDialogFragment.show(getFragmentManager(), backupUri);
                }
            });
            return dialog.create();
        }
    }
}
//&end[RestoreWallet]