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

import org.bitcoinj.core.Coin;
import org.bitcoinj.utils.Fiat;

import de.schildbach.wallet.Configuration;
import de.schildbach.wallet.Constants;
import de.schildbach.wallet.R;
import de.schildbach.wallet.WalletApplication;
import de.schildbach.wallet.data.BlockchainStateLiveData;
import de.schildbach.wallet.data.ExchangeRate;
import de.schildbach.wallet.data.SelectedExchangeRateLiveData;
import de.schildbach.wallet.data.WalletBalanceLiveData;
import de.schildbach.wallet.service.BlockchainState;
import de.schildbach.wallet.ui.send.FeeCategory;
import de.schildbach.wallet.ui.send.SendCoinsActivity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

//&begin[BitcoinBalance]
/**
 * @author Andreas Schildbach
 */
public final class WalletBalanceFragment extends Fragment {
    private WalletActivity activity;
    private WalletApplication application;
    private Configuration config;

    private View viewBalance;
    private CurrencyTextView viewBalanceBtc;
    private TextView viewBalanceWarning;
    private CurrencyTextView viewBalanceLocal;
    private TextView viewProgress;

    private boolean showLocalBalance;
    private boolean installedFromGooglePlay;

    private ViewModel viewModel;

    //&line[BlockchainSync]
    private static final long BLOCKCHAIN_UPTODATE_THRESHOLD_MS = DateUtils.HOUR_IN_MILLIS;

    public static class ViewModel extends AndroidViewModel {
        private final WalletApplication application;
        private BlockchainStateLiveData blockchainState;
        private WalletBalanceLiveData balance;
        //&line[ExchangeRates]
        private SelectedExchangeRateLiveData exchangeRate;

        public ViewModel(final Application application) {
            super(application);
            this.application = (WalletApplication) application;
        }

        public BlockchainStateLiveData getBlockchainState() {
            if (blockchainState == null)
                blockchainState = new BlockchainStateLiveData(application);
            return blockchainState;
        }

        public WalletBalanceLiveData getBalance() {
            if (balance == null)
                balance = new WalletBalanceLiveData(application);
            return balance;
        }

        //&begin[ExchangeRates]
        public SelectedExchangeRateLiveData getExchangeRate() {
            if (exchangeRate == null)
                exchangeRate = new SelectedExchangeRateLiveData(application);
            return exchangeRate;
        }
        //&end[ExchangeRates]
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        this.activity = (WalletActivity) context;
        this.application = activity.getWalletApplication();
        this.config = application.getConfiguration();

        showLocalBalance = getResources().getBoolean(R.bool.show_local_balance);
        installedFromGooglePlay = "com.android.vending"
                .equals(application.getPackageManager().getInstallerPackageName(application.getPackageName()));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        viewModel.getBlockchainState().observe(this, new Observer<BlockchainState>() {
            @Override
            public void onChanged(final BlockchainState blockchainState) {
                updateView();
            }
        });
        viewModel.getBalance().observe(this, new Observer<Coin>() {
            @Override
            public void onChanged(final Coin balance) {
                activity.invalidateOptionsMenu();
                updateView();
                ViewModelProviders.of(activity).get(WalletViewModel.class).balanceLoadingFinished();
            }
        });
        //&begin[ExchangeRates]
        if (Constants.ENABLE_EXCHANGE_RATES) {
            viewModel.getExchangeRate().observe(this, new Observer<ExchangeRate>() {
                @Override
                public void onChanged(final ExchangeRate exchangeRate) {
                    updateView();
                }
            });
        }
        //&end[ExchangeRates]
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wallet_balance_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //&begin[ExchangeRates]
        final boolean showExchangeRatesOption = Constants.ENABLE_EXCHANGE_RATES
                && getResources().getBoolean(R.bool.show_exchange_rates_option);

        viewBalance = view.findViewById(R.id.wallet_balance);
        if (showExchangeRatesOption) {
            viewBalance.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    startActivity(new Intent(getActivity(), ExchangeRatesActivity.class));
                }
            });
        } else {
            viewBalance.setEnabled(false);
        }
        //&end[ExchangeRates]

        viewBalanceBtc = (CurrencyTextView) view.findViewById(R.id.wallet_balance_btc);
        viewBalanceBtc.setPrefixScaleX(0.9f);

        viewBalanceWarning = (TextView) view.findViewById(R.id.wallet_balance_warning);

        viewBalanceLocal = (CurrencyTextView) view.findViewById(R.id.wallet_balance_local);
        viewBalanceLocal.setInsignificantRelativeSize(1);
        viewBalanceLocal.setStrikeThru(Constants.TEST);

        viewProgress = (TextView) view.findViewById(R.id.wallet_balance_progress);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.wallet_balance_fragment_options, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        final Coin balance = viewModel.getBalance().getValue();
        final boolean hasSomeBalance = balance != null && !balance.isLessThan(Constants.SOME_BALANCE_THRESHOLD);
        //&begin[DonateCoins]
        menu.findItem(R.id.wallet_balance_options_donate)
                .setVisible(Constants.DONATION_ADDRESS != null && (!installedFromGooglePlay || hasSomeBalance));
        //&end[DonateCoins]
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            //&begin[DonateCoins]
        case R.id.wallet_balance_options_donate:
            handleDonate();
            return true;
            //&end[DonateCoins]
        }
        return super.onOptionsItemSelected(item);
    }

    //&begin[DonateCoins]
    private void handleDonate() {
        //&begin[SendCoins]
        //&line[Fee]
        SendCoinsActivity.startDonate(activity, null, FeeCategory.ECONOMIC, 0);
        //&end[SendCoins]
    }
    //&end[DonateCoins]

    private void updateView() {
        //&begin[BlockchainSync]
        final BlockchainState blockchainState = viewModel.getBlockchainState().getValue();
        final Coin balance = viewModel.getBalance().getValue();
        //&line[ExchangeRates]
        final ExchangeRate exchangeRate = viewModel.getExchangeRate().getValue();

        final boolean showProgress;

        if (blockchainState != null && blockchainState.bestChainDate != null) {
            final long blockchainLag = System.currentTimeMillis() - blockchainState.bestChainDate.getTime();
            final boolean blockchainUptodate = blockchainLag < BLOCKCHAIN_UPTODATE_THRESHOLD_MS;
            final boolean noImpediments = blockchainState.impediments.isEmpty();

            showProgress = !(blockchainUptodate || !blockchainState.replaying);

            final String downloading = getString(noImpediments ? R.string.blockchain_state_progress_downloading
                    : R.string.blockchain_state_progress_stalled);

            if (blockchainLag < 2 * DateUtils.DAY_IN_MILLIS) {
                final long hours = blockchainLag / DateUtils.HOUR_IN_MILLIS;
                viewProgress.setText(getString(R.string.blockchain_state_progress_hours, downloading, hours));
            } else if (blockchainLag < 2 * DateUtils.WEEK_IN_MILLIS) {
                final long days = blockchainLag / DateUtils.DAY_IN_MILLIS;
                viewProgress.setText(getString(R.string.blockchain_state_progress_days, downloading, days));
            } else if (blockchainLag < 90 * DateUtils.DAY_IN_MILLIS) {
                final long weeks = blockchainLag / DateUtils.WEEK_IN_MILLIS;
                viewProgress.setText(getString(R.string.blockchain_state_progress_weeks, downloading, weeks));
            } else {
                final long months = blockchainLag / (30 * DateUtils.DAY_IN_MILLIS);
                viewProgress.setText(getString(R.string.blockchain_state_progress_months, downloading, months));
            }
        } else {
            showProgress = false;
        }
        //&end[BlockchainSync]

        if (!showProgress) {
            viewBalance.setVisibility(View.VISIBLE);

            if (!showLocalBalance)
                viewBalanceLocal.setVisibility(View.GONE);

            if (balance != null) {
                viewBalanceBtc.setVisibility(View.VISIBLE);
                //&line[Denomination]
                viewBalanceBtc.setFormat(config.getFormat());
                viewBalanceBtc.setAmount(balance);

                if (showLocalBalance) {
                    //&begin[ExchangeRates]
                    if (exchangeRate != null) {
                        final Fiat localValue = exchangeRate.rate.coinToFiat(balance);
                        viewBalanceLocal.setVisibility(View.VISIBLE);
                        viewBalanceLocal.setFormat(Constants.LOCAL_FORMAT.code(0,
                                Constants.PREFIX_ALMOST_EQUAL_TO + exchangeRate.getCurrencyCode()));
                        viewBalanceLocal.setAmount(localValue);
                        viewBalanceLocal.setTextColor(getResources().getColor(R.color.fg_less_significant));
                    } else {
                        viewBalanceLocal.setVisibility(View.INVISIBLE);
                    }
                    //&end[ExchangeRates]
                }
            } else {
                viewBalanceBtc.setVisibility(View.INVISIBLE);
            }

            if (balance != null && balance.isGreaterThan(Constants.TOO_MUCH_BALANCE_THRESHOLD)) {
                viewBalanceWarning.setVisibility(View.VISIBLE);
                viewBalanceWarning.setText(R.string.wallet_balance_fragment_too_much);
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                viewBalanceWarning.setVisibility(View.VISIBLE);
                viewBalanceWarning.setText(R.string.wallet_balance_fragment_insecure_device);
            } else {
                viewBalanceWarning.setVisibility(View.GONE);
            }

            viewProgress.setVisibility(View.GONE);
        } else {
            viewProgress.setVisibility(View.VISIBLE);
            viewBalance.setVisibility(View.INVISIBLE);
        }
    }
}
//&end[BitcoinBalance]
