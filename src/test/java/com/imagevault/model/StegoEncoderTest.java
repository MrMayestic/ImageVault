package com.imagevault.model;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class StegoEncoderTest {

    @Test
    void testEncodeCreatesFile() throws Exception {
        File input = new File("src/main/resources/UserFiles/test.jpg");

        String data = EncryptData.encrypt("hello", EncryptData.generateKey("pass"));

        File output = StegoEncoder.encode(input, data, "target/test-output");

        assertTrue(output.exists());
        assertTrue(output.length() > 0);
    }
}