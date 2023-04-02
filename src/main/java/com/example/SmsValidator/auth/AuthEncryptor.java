package com.example.SmsValidator.auth;

import com.example.SmsValidator.bean.authentication.providerfactory.ValidationProviderFactory;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class AuthEncryptor {
    private final String TRANSFORMATION_CIPHER = "AES/ECB/PKCS5Padding";
    private final String CIPHER_ALGORITHM = "AES";

    public String encrypt(String dataToEncrypt, ValidationProviderFactory validationProvider)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance(TRANSFORMATION_CIPHER);
        SecretKey secretKeyObj = new SecretKeySpec(validationProvider.getSecretKey().getBytes(StandardCharsets.UTF_8), CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeyObj);

        byte[] encryptedMessageBytes = cipher.doFinal(dataToEncrypt.getBytes());
        return Base64.getUrlEncoder()
                .encodeToString(encryptedMessageBytes);
    }

    public Object decrypt(String encryptedValidationData, ValidationProviderFactory validationProvider)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_CIPHER);
        SecretKey secretKeyObj = new SecretKeySpec(validationProvider.getSecretKey().getBytes(StandardCharsets.UTF_8), CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeyObj);

        byte[] decryptedMessageBytes = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedValidationData));

        return new Gson().fromJson(new String(decryptedMessageBytes), validationProvider.getType());
    }
}
