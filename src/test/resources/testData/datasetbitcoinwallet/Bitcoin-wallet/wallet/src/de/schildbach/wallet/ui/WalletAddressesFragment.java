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

package de.schildbach.wallet.ui;

import java.util.List;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.schildbach.wallet.Constants;
import de.schildbach.wallet.R;
import de.schildbach.wallet.WalletApplication;
import de.schildbach.wallet.data.AddressBookDao;
import de.schildbach.wallet.data.AddressBookEntry;
import de.schildbach.wallet.data.AppDatabase;
import de.schildbach.wallet.util.Qr;
import de.schildbach.wallet.util.Toast;
import de.schildbach.wallet.util.WalletUtils;
import de.schildbach.wallet.util.WholeStringBuilder;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

//&begin[AddressBook]
/**
 * @author Andreas Schildbach
 */
public final class WalletAddressesFragment extends FancyListFragment {
    private WalletApplication application;
    private AbstractWalletActivity activity;
    private AddressBookDao addressBookDao;
    private ClipboardManager clipboardManager;

    private WalletAddressesAdapter adapter;

    private WalletAddressesViewModel viewModel;

    private static final Logger log = LoggerFactory.getLogger(WalletAddressesFragment.class);

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        this.activity = (AbstractWalletActivity) context;
        this.application = activity.getWalletApplication();
        this.addressBookDao = AppDatabase.getDatabase(context).addressBookDao();
        this.clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        viewModel = ViewModelProviders.of(this).get(WalletAddressesViewModel.class);
        //&begin[IssueReporter]
        viewModel.issuedReceiveKeys.observe(this, new Observer<List<ECKey>>() {
            @Override
            public void onChanged(final List<ECKey> issuedReceiveKeys) {
                adapter.replaceDerivedKeys(issuedReceiveKeys);
            }
        });
        //&end[IssueReporter]
        viewModel.importedKeys.observe(this, new Observer<List<ECKey>>() {
            @Override
            public void onChanged(final List<ECKey> importedKeys) {
                adapter.replaceRandomKeys(importedKeys);
            }
        });
        viewModel.wallet.observe(this, new Observer<Wallet>() {
            @Override
            public void onChanged(final Wallet wallet) {
                adapter.setWallet(wallet);
            }
        });
        viewModel.addressBook.observe(this, new Observer<List<AddressBookEntry>>() {
            @Override
            public void onChanged(final List<AddressBookEntry> addressBook) {
                adapter.setAddressBook(AddressBookEntry.asMap(addressBook));
            }
        });
        //&begin[OwnName]
        viewModel.ownName.observe(this, new Observer<String>() {
            @Override
            public void onChanged(final String ownName) {
                adapter.notifyDataSetChanged();
            }
        });
        //&end[OwnName]

        adapter = new WalletAddressesAdapter(activity);
        setListAdapter(adapter);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(WholeStringBuilder.bold(getString(R.string.address_book_empty_text)));
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.wallet_addresses_fragment_options, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        activity.startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
                final MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.wallet_addresses_context, menu);
                //&line[BlockExplorer]
                menu.findItem(R.id.wallet_addresses_context_browse).setVisible(Constants.ENABLE_BROWSE);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
                final ECKey key = getKey(position);
				//&begin[Codecs]
                //&line[base58]
                final String address = key.toAddress(Constants.NETWORK_PARAMETERS).toBase58();
				//&end[Codecs]
                final String label = addressBookDao.resolveLabel(address);
                mode.setTitle(label != null ? label
                        : WalletUtils.formatHash(address, Constants.ADDRESS_FORMAT_GROUP_SIZE, 0));
                return true;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
                switch (item.getItemId()) {
                case R.id.wallet_addresses_context_edit:
                    handleEdit(getAddress(position));
                    mode.finish();
                    return true;

                    //&begin[QRCode]
                case R.id.wallet_addresses_context_show_qr:
                    handleShowQr(getAddress(position));
                    mode.finish();
                    return true;
                    //&end[QRCode]

                case R.id.wallet_addresses_context_copy_to_clipboard:
                    handleCopyToClipboard(getAddress(position));
                    mode.finish();
                    return true;

                    //&begin[BlockExplorer]
                case R.id.wallet_addresses_context_browse:
					//&begin[Codecs]
                    //&line[base58]
                    final String address = getAddress(position).toBase58();
					//&end[Codecs]
                    final Uri blockExplorerUri = application.getConfiguration().getBlockExplorer();
                    log.info("Viewing address {} on {}", address, blockExplorerUri);
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.withAppendedPath(blockExplorerUri, "address/" + address)));
                    mode.finish();
                    return true;
                    //&end[BlockExplorer]
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(final ActionMode mode) {
            }

            private ECKey getKey(final int position) {
                return (ECKey) getListAdapter().getItem(position);
            }

            private Address getAddress(final int position) {
                return getKey(position).toAddress(Constants.NETWORK_PARAMETERS);
            }

            private void handleEdit(final Address address) {
                EditAddressBookEntryFragment.edit(getFragmentManager(), address);
            }

            //&begin[QRCode]
            private void handleShowQr(final Address address) {
                //&line[OwnName]
                final String uri = BitcoinURI.convertToBitcoinURI(address, null, viewModel.ownName.getValue(), null);
                BitmapFragment.show(getFragmentManager(), Qr.bitmap(uri));
            }
            //&end[QRCode]

            private void handleCopyToClipboard(final Address address) {
				//&begin[Codecs]
                //&line[base58]
                clipboardManager.setPrimaryClip(ClipData.newPlainText("Bitcoin address", address.toBase58()));
				//&end[Codecs]
                log.info("wallet address copied to clipboard: {}", address);
                new Toast(activity).toast(R.string.wallet_address_fragment_clipboard_msg);
            }
        });
    }
}
//&end[AddressBook]
