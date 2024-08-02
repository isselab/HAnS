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

import de.schildbach.wallet.WalletApplication;
import de.schildbach.wallet.data.WalletBalanceLiveData;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

//&begin[RestoreWallet]
/**
 * @author Andreas Schildbach
 */
public class RestoreWalletViewModel extends AndroidViewModel {
    private final WalletApplication application;
    //&line[BitcoinBalance]
    public final WalletBalanceLiveData balance;

    public RestoreWalletViewModel(final Application application) {
        super(application);
        this.application = (WalletApplication) application;
        //&line[BitcoinBalance]
        this.balance = new WalletBalanceLiveData(this.application);
    }
}
//&end[RestoreWallet]