package StegoModule;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.imageio.*;
import java.util.Scanner; 
import java.nio.file.*;

//class do do steganography on given image
public class StegoEncoder {

    //static function to calculate current bit
    private static int calcBit(byte[] byteText, int bitIndex) {
        int byteIndex = bitIndex / 8;
        int bitPos = 7 - (bitIndex % 8);
        return (byteText[byteIndex] >> bitPos) & 1;
    }

    public static void encode(String srcImgPath, String srcTextPath, String resultPath) {
        BufferedImage img;

        try {
            File mainImg = new File(srcImgPath);
            img = ImageIO.read(mainImg);
        } catch (IOException e) {
            System.err.println("Failed to read image: " + e.getMessage());
            return;
        }

        String textToEncode;

        try {
            textToEncode = Files.readString(Path.of(srcTextPath));
        } catch (IOException e) {
            System.err.println("Failed to read text: " + e.getMessage());
            return;
        }

        byte[] byteText = textToEncode.getBytes(StandardCharsets.UTF_8);
        int length = byteText.length;

        byte[] header = new byte[4];
        header[0] = (byte) (length >> 24);
        header[1] = (byte) (length >> 16);
        header[2] = (byte) (length >> 8);
        header[3] = (byte) (length);

        byte[] payload = new byte[4 + byteText.length];
        System.arraycopy(header, 0, payload, 0, 4);
        System.arraycopy(byteText, 0, payload, 4, byteText.length);

        int bitIndex = 0;
        int totalBits = payload.length * 8;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (bitIndex >= totalBits) {
                    break;
                }
                Color pixel = new Color(img.getRGB(x, y));
                int r = pixel.getRed();
                int g = pixel.getGreen();
                int b = pixel.getBlue();

                if (bitIndex < totalBits) {
                    r = (r & 0xFE) | calcBit(payload, bitIndex++);
                }
                if (bitIndex < totalBits) {
                    g = (g & 0xFE) | calcBit(payload, bitIndex++);
                }
                if (bitIndex < totalBits) {
                    b = (b & 0xFE) | calcBit(payload, bitIndex++);
                }

                img.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        try {
            ImageIO.write(img, "png", new File(resultPath + ".png"));
            System.out.println("Zapisano wynik.png");
        } catch (IOException e) {
            System.err.println("Nie udało się zapisać: " + e.getMessage());
        }
    }

}
