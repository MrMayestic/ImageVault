package com.imagevault.model;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptData {

    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    public static SecretKey generateKey(String secret) throws Exception {
        byte[] keyBytes = MessageDigest
                .getInstance("SHA-256")
                .digest(secret.getBytes("UTF-8"));

        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String encrypt(String data, SecretKey key) throws Exception {

        byte[] initVector = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(initVector);

        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, initVector);
        Cipher cipher;
        byte[] cipherText;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            cipherText = cipher.doFinal(data.getBytes());
        } catch (Exception e) {
            throw new Exception("Failed to encrypt text: " + e.getMessage());
        }

        byte[] dataPrepared = new byte[initVector.length + cipherText.length];
        System.arraycopy(initVector, 0, dataPrepared, 0, initVector.length);
        System.arraycopy(cipherText, 0, dataPrepared, initVector.length, cipherText.length);

        return Base64.getEncoder().encodeToString(dataPrepared);
    }

    public static String decrypt(String data, SecretKey key) throws Exception {
        byte[] dataDecoded = Base64.getDecoder().decode(data);

        byte[] initVector = new byte[GCM_IV_LENGTH];
        System.arraycopy(dataDecoded, 0, initVector, 0, initVector.length);

        int cipherTextLength = dataDecoded.length - GCM_IV_LENGTH;
        byte[] cipherText = new byte[cipherTextLength];
        System.arraycopy(dataDecoded, GCM_IV_LENGTH, cipherText, 0, cipherTextLength);

        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, initVector));
        } catch (Exception e) {
            throw new Exception("Failed to decrypt text: " + e.getMessage());
        }

        byte[] plainText;

        try {
            plainText = cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new Exception("Failed to decrypt text: " + e.getMessage());
        }

        return new String(plainText);
    }

    public static void main(String[] args) throws Exception {
    }

}
