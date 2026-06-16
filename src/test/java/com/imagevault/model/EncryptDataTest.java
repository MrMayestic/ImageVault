package com.imagevault.model;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

class EncryptDataTest {

    @Test
    void testEncryptDecryptCycle() throws Exception {
        SecretKey key = EncryptData.generateKey("password123");

        String original = "Hello secret message";

        String encrypted = EncryptData.encrypt(original, key);
        String decrypted = EncryptData.decrypt(encrypted, key);

        assertEquals(original, decrypted);
    }

    @Test
    void testDifferentPasswordsProduceDifferentKeys() throws Exception {
        SecretKey k1 = EncryptData.generateKey("a");
        SecretKey k2 = EncryptData.generateKey("b");

        assertNotEquals(k1.getEncoded(), k2.getEncoded());
    }
}