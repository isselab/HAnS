/*
 * Copyright 2015 the original author or authors.
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

package de.schildbach.wallet.ui;

import javax.annotation.Nullable;

import org.bitcoinj.core.Address;
import org.bitcoinj.uri.BitcoinURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.schildbach.wallet.Constants;
import de.schildbach.wallet.R;
import de.schildbach.wallet.util.Qr;
import de.schildbach.wallet.util.WalletUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Andreas Schildbach
 */
public class WalletAddressDialogFragment extends DialogFragment {
    private static final String FRAGMENT_TAG = WalletAddressDialogFragment.class.getName();
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_ADDRESS_LABEL = "address_label";

    private static final Logger log = LoggerFactory.getLogger(WalletAddressDialogFragment.class);

    public static void show(final FragmentManager fm, final Address address, @Nullable final String addressLabel) {
        instance(address, addressLabel).show(fm, FRAGMENT_TAG);
    }

    private static WalletAddressDialogFragment instance(final Address address, @Nullable final String addressLabel) {
        final WalletAddressDialogFragment fragment = new WalletAddressDialogFragment();

        final Bundle args = new Bundle();
        args.putSerializable(KEY_ADDRESS, address);
        if (addressLabel != null)
            args.putString(KEY_ADDRESS_LABEL, addressLabel);
        fragment.setArguments(args);

        return fragment;
    }

    private Activity activity;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        this.activity = (AbstractWalletActivity) context;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Bundle args = getArguments();
        //&begin[ShareAddress]
        final Address address = (Address) args.getSerializable(KEY_ADDRESS);
		//&begin[Codecs]
        //&line[base58]
		//&end[Codecs]
        final String addressStr = address.toBase58();
        final String addressLabel = args.getString(KEY_ADDRESS_LABEL);
        //&end[ShareAddress]

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wallet_address_dialog);
        dialog.setCanceledOnTouchOutside(true);

        final String uri = BitcoinURI.convertToBitcoinURI(address, null, addressLabel, null);
        //&begin[QRCode]
        final BitmapDrawable bitmap = new BitmapDrawable(getResources(), Qr.bitmap(uri));
        bitmap.setFilterBitmap(false);
        //&end[QRCode]
        final ImageView imageView = (ImageView) dialog.findViewById(R.id.wallet_address_dialog_image);
        imageView.setImageDrawable(bitmap);

        final View labelButtonView = dialog.findViewById(R.id.wallet_address_dialog_label_button);
        final TextView labelView = (TextView) dialog.findViewById(R.id.wallet_address_dialog_label);
        final CharSequence label = WalletUtils.formatHash(addressStr, Constants.ADDRESS_FORMAT_GROUP_SIZE,
                Constants.ADDRESS_FORMAT_LINE_SIZE);
        labelView.setText(label);
        labelButtonView.setVisibility(View.VISIBLE);
        labelButtonView.setOnClickListener(new OnClickListener() {
            //&begin[ShareAddress]
            @Override
            public void onClick(final View v) {
                final ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(activity);
                builder.setType("text/plain");
                builder.setText(addressStr);
                builder.setChooserTitle(R.string.bitmap_fragment_share);
                builder.startChooser();
                log.info("wallet address shared via intent: {}", addressStr);
            }
            //&end[ShareAddress]
        });

        final View hintView = dialog.findViewById(R.id.wallet_address_dialog_hint);
        hintView.setVisibility(
                getResources().getBoolean(R.bool.show_wallet_address_dialog_hint) ? View.VISIBLE : View.GONE);

        final View dialogView = dialog.findViewById(R.id.wallet_address_dialog_group);
        dialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismissAllowingStateLoss();
            }
        });

        return dialog;
    }
}
