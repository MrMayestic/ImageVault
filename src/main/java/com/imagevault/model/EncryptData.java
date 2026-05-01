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

    public static String encrypt(String data, SecretKey key) {

        byte[] initVector = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(initVector);

        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, initVector);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Nie udalo sie zakodowac wiadomosci: " + e.getMessage());
            return new String("");
        } catch (NoSuchPaddingException e) {
            System.err.println("Nie udalo sie zakodowac wiadomosci: " + e.getMessage());
            return new String("");
        } catch (InvalidKeyException e) {
            System.err.println("Nie udalo sie zakodowac wiadomosci: " + e.getMessage());
            return new String("");
        } catch (InvalidAlgorithmParameterException e) {
            System.err.println("Nie udalo sie zakodowac wiadomosci: " + e.getMessage());
            return new String("");
        }
        byte[] cipherText;
        try {
            cipherText = cipher.doFinal(data.getBytes());
        } catch (IllegalBlockSizeException e) {
            System.err.println("Nie udalo sie zakodowac wiadomosci: " + e.getMessage());
            return new String("");
        } catch (BadPaddingException e) {
            System.err.println("Nie udalo sie zakodowac wiadomosci: " + e.getMessage());
            return new String("");
        }

        byte[] dataPrepared = new byte[initVector.length + cipherText.length];
        System.arraycopy(initVector, 0, dataPrepared, 0, initVector.length);
        System.arraycopy(cipherText, 0, dataPrepared, initVector.length, cipherText.length);

        return Base64.getEncoder().encodeToString(dataPrepared);
    }

    public static String decrypt(String data, SecretKey key) {
        byte[] dataDecoded = Base64.getDecoder().decode(data);

        byte[] initVector = new byte[GCM_IV_LENGTH];
        System.arraycopy(dataDecoded, 0, initVector, 0, initVector.length);

        int cipherTextLength = dataDecoded.length - GCM_IV_LENGTH;
        byte[] cipherText = new byte[cipherTextLength];
        System.arraycopy(dataDecoded, GCM_IV_LENGTH, cipherText, 0, cipherTextLength);

        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Nie udalo sie zakodowac wiadomosci: " + e.getMessage());
            return new String("");
        } catch (NoSuchPaddingException e) {
            System.err.println("Nie udalo sie zakodowac wiadomosci: " + e.getMessage());
            return new String("");
        }
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, initVector);
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
        } catch (InvalidKeyException e) {
            System.err.println("Nie udalo sie zakodowac wiadomosci: " + e.getMessage());
            return new String("");
        } catch (InvalidAlgorithmParameterException e) {
            System.err.println("Nie udalo sie zakodowac wiadomosci: " + e.getMessage());
            return new String("");
        }

        byte[] plainText;

        try {
            plainText = cipher.doFinal(cipherText);
        } catch (IllegalBlockSizeException e) {
            System.err.println("Nie udalo sie zakodowac wiadomosci: " + e.getMessage());
            return new String("");
        } catch (BadPaddingException e) {
            System.err.println("Nie udalo sie zakodowac wiadomosci: " + e.getMessage());
            return new String("");
        }

        return new String(plainText);
    }

    public static void main(String[] args) throws Exception {
        String text = "Woda, szlugi, grube baby";

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        SecretKey key = keyGen.generateKey();

        String encrypted = encrypt(text, key);
        System.out.println("Encrypted (Base64): " + encrypted);

        String decrypted = decrypt(encrypted, key);
        System.out.println("Decrypted: " + decrypted);
    }

}
