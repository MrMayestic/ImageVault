package com.imagevault.model;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class StegoDecoderTest {

    @Test
    void testDecodeReturnsBase64() throws Exception {
        File input = new File("src/main/resources/UserFiles/test.jpg");

        String data = EncryptData.encrypt("secret", EncryptData.generateKey("pass"));

        File encoded = StegoEncoder.encode(input, data, "target/encoded");

        String decoded = StegoDecoder.decode(encoded.getAbsolutePath(), "");

        assertNotNull(decoded);
        assertFalse(decoded.isEmpty());
    }
}