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

import javax.annotation.Nullable;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.wallet.Wallet;

import de.schildbach.wallet.Constants;
import de.schildbach.wallet.WalletApplication;
import de.schildbach.wallet.data.AbstractWalletLiveData;
import de.schildbach.wallet.data.ConfigOwnNameLiveData;
import de.schildbach.wallet.data.SelectedExchangeRateLiveData;
import de.schildbach.wallet.util.Bluetooth;
import de.schildbach.wallet.util.Qr;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

//&begin[RequestCoins]
/**
 * @author Andreas Schildbach
 */
public class RequestCoinsViewModel extends AndroidViewModel {
    private final WalletApplication application;
    //&line[SenderAddress]
    public final FreshReceiveAddressLiveData freshReceiveAddress;
    //&line[OwnName]
    private final ConfigOwnNameLiveData ownName;
    //&line[ExchangeRates]
    public final SelectedExchangeRateLiveData exchangeRate;
    public final MutableLiveData<Coin> amount = new MutableLiveData<>();
    //&line[Bluetooth]
    public final MutableLiveData<String> bluetoothMac = new MutableLiveData<>();
    //&linde[QRCode]
    public final MediatorLiveData<Bitmap> qrCode = new MediatorLiveData<>();
    public final MediatorLiveData<byte[]> paymentRequest = new MediatorLiveData<>();
    public final MediatorLiveData<Uri> bitcoinUri = new MediatorLiveData<>();

    //&begin[Bluetooth]
    @Nullable
    public Intent bluetoothServiceIntent = null;
    //&end[Bluetooth]

    //&begin[SenderAddress]
    public RequestCoinsViewModel(final Application application) {
        super(application);
        this.application = (WalletApplication) application;
        this.freshReceiveAddress = new FreshReceiveAddressLiveData(this.application);
        //&line[OwnName]
        this.ownName = new ConfigOwnNameLiveData(this.application);
        //&line[ExchangeRate]
        this.exchangeRate = new SelectedExchangeRateLiveData(this.application);
        //&begin[QRCode]
        this.qrCode.addSource(freshReceiveAddress, new Observer<Address>() {
            @Override
            public void onChanged(final Address receiveAddress) {
                maybeGenerateQrCode();
            }
        });
        //&begin[OwnName]
        this.qrCode.addSource(ownName, new Observer<String>() {
            @Override
            public void onChanged(final String label) {
                maybeGenerateQrCode();
            }
        });
        //&end[OwnName]
        this.qrCode.addSource(amount, new Observer<Coin>() {
            @Override
            public void onChanged(final Coin amount) {
                maybeGenerateQrCode();
            }
        });
        //&begin[Bluetooth]
        this.qrCode.addSource(bluetoothMac, new Observer<String>() {
            @Override
            public void onChanged(final String bluetoothMac) {
                maybeGenerateQrCode();
            }
        });
        //&end[Bluetooth]
        //&end[QRCode]
        this.paymentRequest.addSource(freshReceiveAddress, new Observer<Address>() {
            @Override
            public void onChanged(final Address receiveAddress) {
                maybeGeneratePaymentRequest();
            }
        });
        //&begin[OwnName]
        this.paymentRequest.addSource(ownName, new Observer<String>() {
            @Override
            public void onChanged(final String label) {
                maybeGeneratePaymentRequest();
            }
        });
        //&end[OwnName]
        this.paymentRequest.addSource(amount, new Observer<Coin>() {
            @Override
            public void onChanged(final Coin amount) {
                maybeGeneratePaymentRequest();
            }
        });
        //&begin[Bluetooth]
        this.paymentRequest.addSource(bluetoothMac, new Observer<String>() {
            @Override
            public void onChanged(final String bluetoothMac) {
                maybeGeneratePaymentRequest();
            }
        });
        //&end[Bluetooth]
        this.bitcoinUri.addSource(freshReceiveAddress, new Observer<Address>() {
            @Override
            public void onChanged(final Address receiveAddress) {
                maybeGenerateBitcoinUri();
            }
        });
        //&begin[OwnName]
        this.bitcoinUri.addSource(ownName, new Observer<String>() {
            @Override
            public void onChanged(final String label) {
                maybeGenerateBitcoinUri();
            }
        });
        //&end[OwnName]
        this.bitcoinUri.addSource(amount, new Observer<Coin>() {
            @Override
            public void onChanged(final Coin amount) {
                maybeGenerateBitcoinUri();
            }
        });
    }
    //&end[SenderAddress]

    //&begin[QRCode]
    private void maybeGenerateQrCode() {
        //&line[SenderAddress]
        final Address address = freshReceiveAddress.getValue();
        if (address != null) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    qrCode.postValue(
                            //&line[Bluetooth]
                            Qr.bitmap(uri(address, amount.getValue(), ownName.getValue(), bluetoothMac.getValue())));
                }
            });
        }
    }
    //&end[QRCode]

    private void maybeGeneratePaymentRequest() {
        //&line[SenderAddress]
        final Address address = freshReceiveAddress.getValue();
        if (address != null) {
            //&begin[Bluetooth]
            final String bluetoothMac = this.bluetoothMac.getValue();
            //&begin[PaymentURL]
            final String paymentUrl = bluetoothMac != null ? "bt:" + bluetoothMac : null;
            paymentRequest.setValue(PaymentProtocol.createPaymentRequest(Constants.NETWORK_PARAMETERS,
                    //&line[OwnName]
                    amount.getValue(), address, ownName.getValue(), paymentUrl, null).build().toByteArray());
            //&end[PaymentURL]
            //&end[Bluetooth]
        }
    }

    private void maybeGenerateBitcoinUri() {
        //&line[SenderAddress]
        final Address address = freshReceiveAddress.getValue();
        if (address != null) {
            //&line[OwnName]
            bitcoinUri.setValue(Uri.parse(uri(address, amount.getValue(), ownName.getValue(), null)));
        }
    }

    //&begin[Bluetooth]
    private String uri(final Address address, final Coin amount, final String label, final String bluetoothMac) {
        final StringBuilder uri = new StringBuilder(BitcoinURI.convertToBitcoinURI(address, amount, label, null));
        if (bluetoothMac != null) {
            uri.append(amount == null && label == null ? '?' : '&');
            uri.append(Bluetooth.MAC_URI_PARAM).append('=').append(bluetoothMac);
        }
        return uri.toString();
    }
    //&end[Bluetooth]

    //&begin[SenderAddress]
    public static class FreshReceiveAddressLiveData extends AbstractWalletLiveData<Address> {
        public FreshReceiveAddressLiveData(final WalletApplication application) {
            super(application);
        }

        @Override
        public void setValue(final Address address) {
            super.setValue(address);
        }

        @Override
        protected void onWalletActive(final Wallet wallet) {
            maybeLoad();
        }

        private void maybeLoad() {
            if (getValue() == null) {
                final Wallet wallet = getWallet();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        org.bitcoinj.core.Context.propagate(Constants.CONTEXT);
                        postValue(wallet.freshReceiveAddress());
                    }
                });
            }
        }
    }
    //&end[SenderAddress]
}
//&end[RequestCoins]