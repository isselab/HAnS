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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.utils.Threading;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.KeyChainEventListener;

import de.schildbach.wallet.WalletApplication;
import de.schildbach.wallet.data.AbstractWalletLiveData;
import de.schildbach.wallet.data.AddressBookEntry;
import de.schildbach.wallet.data.AppDatabase;
import de.schildbach.wallet.data.ConfigOwnNameLiveData;
import de.schildbach.wallet.data.WalletLiveData;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

//&begin[AddressBook]
/**
 * @author Andreas Schildbach
 */
public class WalletAddressesViewModel extends AndroidViewModel {
    private final WalletApplication application;
    //&line[IssueReporter]
    public final IssuedReceiveKeysLiveData issuedReceiveKeys;
    public final ImportedKeysLiveData importedKeys;
    public final LiveData<List<AddressBookEntry>> addressBook;
    public final WalletLiveData wallet;
    //&line[OwnName]
    public final ConfigOwnNameLiveData ownName;

    public WalletAddressesViewModel(final Application application) {
        super(application);
        this.application = (WalletApplication) application;
        //&line[IssueReporter]
        this.issuedReceiveKeys = new IssuedReceiveKeysLiveData(this.application);
        this.importedKeys = new ImportedKeysLiveData(this.application);
        this.addressBook = AppDatabase.getDatabase(this.application).addressBookDao().getAll();
        this.wallet = new WalletLiveData(this.application);
        //&line[OwnName]
        this.ownName = new ConfigOwnNameLiveData(this.application);
    }

    //&begin[IssueReporter]
    public static class IssuedReceiveKeysLiveData extends AbstractWalletLiveData<List<ECKey>>
            implements KeyChainEventListener {
        public IssuedReceiveKeysLiveData(final WalletApplication application) {
            super(application);
        }

        @Override
        protected void onWalletActive(final Wallet wallet) {
            wallet.addKeyChainEventListener(Threading.SAME_THREAD, this);
            loadKeys();
        }

        @Override
        protected void onWalletInactive(final Wallet wallet) {
            wallet.removeKeyChainEventListener(this);
        }

        @Override
        public void onKeysAdded(final List<ECKey> keys) {
            loadKeys();
        }

        private void loadKeys() {
            final Wallet wallet = getWallet();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    postValue(wallet.getIssuedReceiveKeys());
                }
            });
        }
    }
    //&end[IssueReporter]

    public static class ImportedKeysLiveData extends AbstractWalletLiveData<List<ECKey>>
            implements KeyChainEventListener {
        public ImportedKeysLiveData(final WalletApplication application) {
            super(application);
        }

        @Override
        protected void onWalletActive(final Wallet wallet) {
            wallet.addKeyChainEventListener(Threading.SAME_THREAD, this);
            loadKeys();
        }

        @Override
        protected void onWalletInactive(final Wallet wallet) {
            wallet.removeKeyChainEventListener(this);
        }

        @Override
        public void onKeysAdded(final List<ECKey> keys) {
            loadKeys();
        }

        private void loadKeys() {
            final Wallet wallet = getWallet();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    final List<ECKey> importedKeys = wallet.getImportedKeys();
                    Collections.sort(importedKeys, new Comparator<ECKey>() {
                        @Override
                        public int compare(final ECKey lhs, final ECKey rhs) {
                            final boolean lhsRotating = wallet.isKeyRotating(lhs);
                            final boolean rhsRotating = wallet.isKeyRotating(rhs);

                            if (lhsRotating != rhsRotating)
                                return lhsRotating ? 1 : -1;
                            if (lhs.getCreationTimeSeconds() != rhs.getCreationTimeSeconds())
                                return lhs.getCreationTimeSeconds() > rhs.getCreationTimeSeconds() ? 1 : -1;
                            return 0;
                        }
                    });
                    postValue(importedKeys);
                }
            });
        }
    }
}
//&end[AddressBook]
