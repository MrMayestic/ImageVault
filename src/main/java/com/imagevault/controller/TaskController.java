package com.imagevault.controller;

import com.imagevault.model.EncryptData;
import com.imagevault.model.StegoEncoder;
import com.imagevault.model.StegoDecoder;
import java.nio.file.Files;
import java.nio.file.Path;

public class TaskController {

    public static void main(String[] args) {
        String testImgSrc = "src/main/resources/test.jpg";
        String testTextSrc = "src/main/resources/textToEncode.txt";
        String testResultPath = "src/main/resources/tresult";
        String testImageToDecodePath = "src/main/resources/tresult.png";
        String testDecodedTextPath = "src/main/resources/tdecoded.txt";
        String testPassword = "parserZcepa";
        String encryptedData;

        String textToEncode;

        try {
            textToEncode = Files.readString(Path.of(testTextSrc));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            encryptedData = EncryptData.encrypt(textToEncode, EncryptData.generateKey(testPassword));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        StegoEncoder.encode(testImgSrc, encryptedData, testResultPath);
        String decodedText = StegoDecoder.decode(testImageToDecodePath, testDecodedTextPath);

        try {
            System.out.println(EncryptData.decrypt(decodedText, EncryptData.generateKey(testPassword)));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }
}
