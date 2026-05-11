package com.imagevault.controller;
import com.imagevault.model.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.io.*;
import java.nio.file.*;
import javafx.stage.FileChooser.*;
import java.util.*;

public class TaskController {

    private static File selectedImage;
    private static File selectedText;
    private static boolean isFile = true;

    @FXML private TextField passwordField;
    @FXML private Label imagePath;
    @FXML private Label textPath;

    @FXML private HBox fileBox;
    @FXML private TextArea inputArea;
    @FXML private ToggleGroup textInputType;

    @FXML private Label resultImageInfo;
    @FXML private ImageView resultPreview;
    @FXML private Button downloadImage;

    private File imageToDecode;

    @FXML private Label decodeImagePath;
    @FXML private PasswordField decodedPasswordField;
    @FXML private TextArea outputArea;

    //handle to toggling text input method
    @FXML
    private void handleToggleInput() {
        RadioButton selected = (RadioButton) textInputType.getSelectedToggle();
        if (selected == null) return;
        isFile = selected.getText().equals("Select File");

        fileBox.setVisible(isFile);
        fileBox.setManaged(isFile);

        inputArea.setVisible(!isFile);
        inputArea.setManaged(!isFile);
    }

    //creates file chooser and sets source image
    @FXML
    private void handleLoadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pick image to encode");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            selectedImage = selectedFile;
            imagePath.setText(selectedImage.getName());
        }
    }

    //creates file chooser and sets text file
    @FXML
    private void handleLoadText() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pick text to encode");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            selectedText = selectedFile;
            textPath.setText(selectedText.getName());
        }
    }

    //handler when encode button is clicked
    @FXML
    private void encodeImage() {
        String password = passwordField.getText();
        String textToEncode;
        String encryptedData;

        //isFile tells which type of text input is selected
        if (isFile) {
            try {
                textToEncode = Files.readString(Path.of(selectedText.getAbsolutePath()));
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            textToEncode = inputArea.getText();
        }

        //encrypt data
        try {
            encryptedData = EncryptData.encrypt(textToEncode, EncryptData.generateKey(password));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        //encode() returns reference to new file
        File encodedImage = StegoEncoder.encode(selectedImage, encryptedData, "result");

        resultImageInfo.setVisible(true);
        resultImageInfo.setManaged(true);

        Image image = new Image(encodedImage.toURI().toString());
        resultPreview.setImage(image);

        resultPreview.setVisible(true);
        resultPreview.setManaged(true);

        downloadImage.setVisible(true);
        downloadImage.setManaged(true);
    }

    //
    @FXML
    private void handleLoadImageToDecode() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pick image to decode");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        
        if (selectedFile != null) {
            selectedImageToDecode = selectedFile;
            decodeImagePath.setText(selectedImageToDecode.getName());
        }
    }
    
    //decode handler
    @FXML
    private void decodeImage() {
        if (imageToDecode == null) {
            outputArea.setText("Error: No image selected!");
            return;
        }

        String base64Data;
        String password = decodedPasswordField.getText();
        String decryptedText;

        try {
            base64Data = StegoDecoder.decode(imageToDecode.getAbsolutePath(), "");
            
            if (base64Data == null || base64Data.isEmpty()) {
                outputArea.setText("Error: No hidden data found in this image.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            outputArea.setText("Error: Failed to extract data from image.");
            return;
        }

        try {
            decryptedText = EncryptData.decrypt(base64Data, EncryptData.generateKey(password));
        } catch (Exception e) {
            e.printStackTrace();
            outputArea.setText("Error: Decryption failed. Wrong password?");
            return;
        }

        outputArea.setText(decryptedText);
    }

    //temporary decoding test
    public static void main(String[] args) {
//        String testImgSrc = "test.jpg";
//        String testTextSrc = "textToEncode.txt";
//        String EnceyptedTextSrc = "encrypted.txt";
//        String testResultPath = "result";
//        String testImageToDecodePath = "result.png";
//        String testDecodedTextPath = "decoded.txt";
//        String testPassword = "parserZcepa";
//        String encryptedData;
//
//        String textToEncode;
//
//        try {
//            textToEncode = Files.readString(Path.of(testTextSrc));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//
//        try {
//            encryptedData = EncryptData.encrypt(textToEncode, EncryptData.generateKey(testPassword));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }

//        StegoEncoder.encode(testImgSrc, encryptedData, testResultPath);
        String decodedText = StegoDecoder.decode("result.png", "");

        try {
            System.out.println(EncryptData.decrypt(decodedText, EncryptData.generateKey("iLoveIO!")));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }
}