package com.imagevault.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Base64;
import javax.imageio.*;

public class StegoDecoder {

    private static int addBit(int currentByte, int bit) {
        return (currentByte << 1) | (bit & 1);
    }

    public static String decode(String srcImagePath, String resultPath)  throws Exception  {
        BufferedImage img;

        try {
            File mainImg = new File(srcImagePath);
            if (!mainImg.exists()) {
                throw new Exception("Failed to read image: File not found.");
            }
            img = ImageIO.read(mainImg);
            if (img == null) {
                throw new Exception("Failed to read image: Unsupported format.");
            }
        } catch (IOException e) {
            throw new Exception("Failed to read image: " + e.getMessage());
        }

        int bitIndex = 0;
        int length = 0;

        for (int x = 0; x < 11; x++) {
            Color pixel = new Color(img.getRGB(x, 0));
            int r = pixel.getRed();
            int g = pixel.getGreen();
            int b = pixel.getBlue();

            length = (length << 1) | (r & 1);
            bitIndex++;

            if (bitIndex >= 32) {
                break;
            }

            length = (length << 1) | (g & 1);
            bitIndex++;

            if (bitIndex >= 32) {
                break;
            }

            length = (length << 1) | (b & 1);
            bitIndex++;

        }

        byte[] message = new byte[length];

        int globalBit = 0;
        int currentByte = 0;
        int bitCount = 0;
        int totalBits = length * 8;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (bitCount >= totalBits) {
                    break;
                }

                Color pixel = new Color(img.getRGB(x, y));
                int[] channels = {pixel.getRed(), pixel.getGreen(), pixel.getBlue()};

                for (int channel : channels) {
                    if (globalBit < 32) {
                        globalBit++;
                        continue;
                    }
                    if (bitCount >= totalBits) {
                        break;
                    }

                    currentByte = addBit(currentByte, channel);
                    bitCount++;
                    if (bitCount % 8 == 0) {
                        message[bitCount / 8 - 1] = (byte) currentByte;
                    }
                }
            }
            if (bitCount >= totalBits) {
                break;
            }
        }

        return Base64.getEncoder().encodeToString(message);
    }
}
