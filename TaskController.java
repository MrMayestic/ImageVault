import StegoModule.*;

public class TaskController {
    public static void main(String[] args) {
        String testImgSrc = "test.jpg";
        String testTextSrc = "textToEncode.txt";
        String testResultPath = "result";
        String testImageToDecodePath = "result.png";
        String testDecodedTextPath = "decoded.txt";

        StegoEncoder.encode(testImgSrc, testTextSrc, testResultPath);
        StegoDecoder.decode(testImageToDecodePath, testDecodedTextPath);
    }
}