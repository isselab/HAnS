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

package de.schildbach.wallet.data;

import org.bitcoinj.utils.MonetaryFormat;

import de.schildbach.wallet.Configuration;
import de.schildbach.wallet.WalletApplication;

import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;

/**
 * @author Andreas Schildbach
 */
public class ConfigFormatLiveData extends LiveData<MonetaryFormat>
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final Configuration config;

    public ConfigFormatLiveData(final WalletApplication application) {
        this.config = application.getConfiguration();
    }

    @Override
    protected void onActive() {
        config.registerOnSharedPreferenceChangeListener(this);
        setValue(config.getFormat());	//&line[Denomination]
    }

    @Override
    protected void onInactive() {
        config.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        //&begin[Denomination]
        if (Configuration.PREFS_KEY_BTC_PRECISION.equals(key))
            setValue(config.getFormat());
        //&end[Denomination]
    }
}
