
import EncryptModule.*;
import StegoModule.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class TaskController {

    public static void main(String[] args) {
        String testImgSrc = "test.jpg";
        String testTextSrc = "textToEncode.txt";
        String EnceyptedTextSrc = "encrypted.txt";
        String testResultPath = "result";
        String testImageToDecodePath = "result.png";
        String testDecodedTextPath = "decoded.txt";
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
