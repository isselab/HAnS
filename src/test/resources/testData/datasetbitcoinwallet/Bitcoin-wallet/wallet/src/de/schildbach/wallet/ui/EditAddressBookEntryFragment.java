/*
 * Copyright 2011-2015 the original author or authors.
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
import org.bitcoinj.wallet.Wallet;

import de.schildbach.wallet.Constants;
import de.schildbach.wallet.R;
import de.schildbach.wallet.WalletApplication;
import de.schildbach.wallet.data.AddressBookDao;
import de.schildbach.wallet.data.AddressBookEntry;
import de.schildbach.wallet.data.AppDatabase;
import de.schildbach.wallet.util.WalletUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

//&begin[AddressBook]
/**
 * @author Andreas Schildbach
 */
public final class EditAddressBookEntryFragment extends DialogFragment {
    private static final String FRAGMENT_TAG = EditAddressBookEntryFragment.class.getName();

    private static final String KEY_ADDRESS = "address";
    private static final String KEY_SUGGESTED_ADDRESS_LABEL = "suggested_address_label";

    public static void edit(final FragmentManager fm, final String address) {
		//&begin[Codecs]
        //&line[base58]
        edit(fm, Address.fromBase58(Constants.NETWORK_PARAMETERS, address), null);
		//&end[Codecs]
    }

    public static void edit(final FragmentManager fm, final Address address) {
        edit(fm, address, null);
    }

    public static void edit(final FragmentManager fm, final Address address,
            @Nullable final String suggestedAddressLabel) {
        final DialogFragment newFragment = EditAddressBookEntryFragment.instance(address, suggestedAddressLabel);
        newFragment.show(fm, FRAGMENT_TAG);
    }

    private static EditAddressBookEntryFragment instance(final Address address,
            @Nullable final String suggestedAddressLabel) {
        final EditAddressBookEntryFragment fragment = new EditAddressBookEntryFragment();

        final Bundle args = new Bundle();
		//&begin[Codecs]
        //&line[base58]
        args.putString(KEY_ADDRESS, address.toBase58());
		//&end[Codecs]
        args.putString(KEY_SUGGESTED_ADDRESS_LABEL, suggestedAddressLabel);
        fragment.setArguments(args);

        return fragment;
    }

    private AbstractWalletActivity activity;
    private AddressBookDao addressBookDao;
    private Wallet wallet;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        this.activity = (AbstractWalletActivity) context;
        final WalletApplication application = activity.getWalletApplication();
        this.addressBookDao = AppDatabase.getDatabase(context).addressBookDao();
        this.wallet = application.getWallet();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Bundle args = getArguments();
		//&begin[Codecs]
        //&line[base58]
        final Address address = Address.fromBase58(Constants.NETWORK_PARAMETERS, args.getString(KEY_ADDRESS));
		//&end[Codecs]
        final String suggestedAddressLabel = args.getString(KEY_SUGGESTED_ADDRESS_LABEL);

        final LayoutInflater inflater = LayoutInflater.from(activity);

		//&begin[Codecs]
        //&line[base58]
        final String label = addressBookDao.resolveLabel(address.toBase58());
		//&end[Codecs]

        final boolean isAdd = label == null;
        final boolean isOwn = wallet.isPubKeyHashMine(address.getHash160());

        final DialogBuilder dialog = new DialogBuilder(activity);

        if (isOwn)
            dialog.setTitle(isAdd ? R.string.edit_address_book_entry_dialog_title_add_receive
                    : R.string.edit_address_book_entry_dialog_title_edit_receive);
        else
            dialog.setTitle(isAdd ? R.string.edit_address_book_entry_dialog_title_add
                    : R.string.edit_address_book_entry_dialog_title_edit);

        final View view = inflater.inflate(R.layout.edit_address_book_entry_dialog, null);

        final TextView viewAddress = (TextView) view.findViewById(R.id.edit_address_book_entry_address);
        viewAddress.setText(WalletUtils.formatAddress(address, Constants.ADDRESS_FORMAT_GROUP_SIZE,
                Constants.ADDRESS_FORMAT_LINE_SIZE));

        final TextView viewLabel = (TextView) view.findViewById(R.id.edit_address_book_entry_label);
        viewLabel.setText(label != null ? label : suggestedAddressLabel);

        dialog.setView(view);

        final DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    final String newLabel = viewLabel.getText().toString().trim();
                    if (!newLabel.isEmpty())
						//&begin[Codecs]
                        //&line[base58]
                        addressBookDao.insertOrUpdate(new AddressBookEntry(address.toBase58(), newLabel));
						//&end[Codecs]
                    else if (!isAdd)
						//&begin[Codecs]
                        //&line[base58]
                        addressBookDao.delete(address.toBase58());
						//&end[Codecs]
                } else if (which == DialogInterface.BUTTON_NEUTRAL) {
					//&begin[Codecs]
                    //&line[base58]
                    addressBookDao.delete(address.toBase58());
					//&end[Codecs]
                }

                dismiss();
            }
        };

        dialog.setPositiveButton(isAdd ? R.string.button_add : R.string.edit_address_book_entry_dialog_button_edit,
                onClickListener);
        if (!isAdd)
            dialog.setNeutralButton(R.string.button_delete, onClickListener);
        dialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dismissAllowingStateLoss();
            }
        });

        return dialog.create();
    }
}
//&end[AddressBook]