package com.example.SmsValidator.bean.payment.coinremitter;

import com.example.SmsValidator.exception.customexceptions.payment.UnknownCoinException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
public class CoinRemitterPaymentProvider {

    private final CoinRemitterPaymentInfo coinRemitterPaymentInfo;
    private final CoinRemitterEncryptor encryptor;

    public HttpEntity<MultiValueMap<String, String>> createInvoiceRequest(String coin, Float amount, CustomData customData)
            throws UnknownCoinException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (!checkIfExists(coin)) {
            throw new UnknownCoinException("Unknown coin", this.getClass());
        }
        CoinRemitterCoinInfo coinInfo = coinRemitterPaymentInfo.getCoins().get(coin);
        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("amount", String.valueOf(amount));
        map.add("currency", coinRemitterPaymentInfo.getCurrency());
        map.add("api_key", coinInfo.getApiKey());
        map.add("password", coinInfo.getPassword());
        map.add("expire_time", String.valueOf(coinRemitterPaymentInfo.getExpireTime()));
        map.add("notify_url", coinRemitterPaymentInfo.getNotifyUrl());
        map.add("custom_data1", encryptor.encryptCustomData(customData));

        return new HttpEntity<>(map, headers);
    }

    public boolean checkIfExists(String coinName) {
        return coinRemitterPaymentInfo.getCoins().containsKey(coinName);
    }

    public String createInvoiceUrl(String coinName) {
        return String.format(coinRemitterPaymentInfo.getInvoiceUrl(), coinName);
    }

    public CoinRemitterCoinInfo getCoinInfo(String coin) throws UnknownCoinException {
        if (!checkIfExists(coin)) throw new UnknownCoinException("Unknown coin", this.getClass());
        return coinRemitterPaymentInfo.getCoins().get(coin);
    }

    public CoinRemitterPaymentInfo getInfo() {
        return coinRemitterPaymentInfo;
    }
}
