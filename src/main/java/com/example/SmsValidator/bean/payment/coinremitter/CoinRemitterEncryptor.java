package com.example.SmsValidator.bean.payment.coinremitter;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class CoinRemitterEncryptor {
    public static final String TRANSFORMATION_CIPHER = "AES/ECB/PKCS5Padding";
    public static final String CIPHER_ALGORITHM = "AES";
    @Value("${payment.coin-remitter.secretKey}")
    private String secretKey;

    public String encryptCustomData(CustomData customData)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String plainText = new Gson().toJson(customData);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION_CIPHER);
        SecretKey secretKeyObj = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeyObj);

        byte[] encryptedMessageBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder()
                .encodeToString(encryptedMessageBytes);
    }

    public CustomData decryptCustomData(String encryptedCustomData)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_CIPHER);
        SecretKey secretKeyObj = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeyObj);

        byte[] decryptedMessageBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedCustomData));

        return new Gson().fromJson(new String(decryptedMessageBytes), CustomData.class);
    }
}
