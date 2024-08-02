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

import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.DeterministicUpgradeRequiresPassword;
import org.bitcoinj.wallet.Wallet;

import com.google.common.util.concurrent.ListenableFuture;

import de.schildbach.wallet.Constants;
import de.schildbach.wallet.WalletApplication;
import de.schildbach.wallet.data.AbstractWalletLiveData;
import de.schildbach.wallet.data.BlockchainStateLiveData;
import de.schildbach.wallet.service.BlockchainState;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;

/**
 * @author Andreas Schildbach
 */
public class MaybeMaintenanceViewModel extends AndroidViewModel {
    private final WalletApplication application;
    private final WalletMaintenanceRecommendedLiveData walletMaintenanceRecommended;
    private final BlockchainStateLiveData blockchainState;
    public final MediatorLiveData<Void> showDialog = new MediatorLiveData<Void>();
    private boolean dialogWasShown = false;

    public MaybeMaintenanceViewModel(final Application application) {
        super(application);
        this.application = (WalletApplication) application;
        this.walletMaintenanceRecommended = new WalletMaintenanceRecommendedLiveData(this.application);
        this.blockchainState = new BlockchainStateLiveData(this.application);
        showDialog.addSource(walletMaintenanceRecommended, new Observer<Boolean>() {
            @Override
            public void onChanged(final Boolean maintenanceRecommended) {
                maybeShowDialog();
            }
        });
        showDialog.addSource(blockchainState, new Observer<BlockchainState>() {
            @Override
            public void onChanged(final BlockchainState blockchainState) {
                maybeShowDialog();
            }
        });
    }

    private void maybeShowDialog() {
        final BlockchainState blockchainState = MaybeMaintenanceViewModel.this.blockchainState.getValue();
        final Boolean maintenanceRecommended = MaybeMaintenanceViewModel.this.walletMaintenanceRecommended.getValue();
        if (blockchainState != null && !blockchainState.replaying && maintenanceRecommended != null
                && maintenanceRecommended == true)
            showDialog.postValue(null);
    }

    public void setDialogWasShown() {
        dialogWasShown = true;
    }

    public boolean getDialogWasShown() {
        return dialogWasShown;
    }

    public static class WalletMaintenanceRecommendedLiveData extends AbstractWalletLiveData<Boolean> {
        public WalletMaintenanceRecommendedLiveData(final WalletApplication application) {
            super(application);
        }

        @Override
        protected void onWalletActive(final Wallet wallet) {
            load();
        }

        @Override
        protected void load() {
            final Wallet wallet = getWallet();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    org.bitcoinj.core.Context.propagate(Constants.CONTEXT);
                    try {
                        final ListenableFuture<List<Transaction>> result = wallet.doMaintenance(null, false);
                        postValue(!result.get().isEmpty());
                    } catch (final DeterministicUpgradeRequiresPassword x) {
                        postValue(true);
                    } catch (final Exception x) {
                        throw new RuntimeException(x);
                    }
                }
            });
        }
    }
}
