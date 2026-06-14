package com.imagevault.controller;
import com.imagevault.model.*;
import com.imagevault.util.*;

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
    private static File selectedImageToDecode;
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

    @FXML private Label decodeImagePath;
    @FXML private PasswordField decodedPasswordField;
    @FXML private TextArea outputArea;

    @FXML private Label encodeErrorLabel;
    @FXML private Label decodeErrorLabel;

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
        File resourcesDir = new File(System.getProperty("user.dir") + "/src/main/resources/UserFiles");
        if (resourcesDir.exists()) {
            fileChooser.setInitialDirectory(resourcesDir);
        }
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
        File resourcesDir = new File(System.getProperty("user.dir") + "/src/main/resources/UserFiles");
        if (resourcesDir.exists()) {
            fileChooser.setInitialDirectory(resourcesDir);
        }
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
        errorHandler.clear(encodeErrorLabel);

        String password = passwordField.getText();
        String textToEncode;
        String encryptedData;

        if (selectedImage == null) {
            errorHandler.error(encodeErrorLabel, "No image selected.");
            return;
        }

        //isFile tells which type of text input is selected
        if (isFile) {
            if (selectedText == null) {
                errorHandler.error(encodeErrorLabel, "No text file selected.");
                return;
            }
            try {
                textToEncode = Files.readString(Path.of(selectedText.getAbsolutePath()));
                if (textToEncode.isEmpty()) {
                    errorHandler.error(encodeErrorLabel, "Text file is empty.");
                    return;
                }
            } catch (Exception e) {
                errorHandler.error(encodeErrorLabel, "Failed to read text file: " + e.getMessage());
                return;
            }
        } else {
            textToEncode = inputArea.getText();
        }

        //encrypt data
        try {
            encryptedData = EncryptData.encrypt(textToEncode, EncryptData.generateKey(password));
        } catch (Exception e) {
        errorHandler.error(encodeErrorLabel, "Encryption failed: " + e.getMessage());
        return;
    }

        File encodedImage = null;

        //encode() returns reference to new file
        try {
            encodedImage = StegoEncoder.encode(selectedImage, encryptedData, System.getProperty("user.dir") + "/src/main/resources/UserFiles/result");
        } catch (Exception e) {
                errorHandler.error(encodeErrorLabel, e.getMessage());
            return;
        }

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
        File resourcesDir = new File(System.getProperty("user.dir") + "/src/main/resources/UserFiles");
        if (resourcesDir.exists()) {
            fileChooser.setInitialDirectory(resourcesDir);
        }
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
        errorHandler.clear(decodeErrorLabel);
        if (selectedImageToDecode == null) {
            errorHandler.error(decodeErrorLabel, "No image selected.");
            return;
        }

        String base64Data;
        String password = decodedPasswordField.getText();
        String decryptedText;

        try {
            base64Data = StegoDecoder.decode(selectedImageToDecode.getAbsolutePath(), "");
            
            if (base64Data == null || base64Data.isEmpty()) {
                errorHandler.error(decodeErrorLabel, "No hidden data found in this image.");
                return;
            }
        } catch (Exception e) {
            errorHandler.error(decodeErrorLabel, e.getMessage());
            return;
        }

        try {
            decryptedText = EncryptData.decrypt(base64Data, EncryptData.generateKey(password));
        } catch (Exception e) {
            errorHandler.error(decodeErrorLabel, e.getMessage());
            return;
        }

        outputArea.setText(decryptedText);
    }

    @FXML
    private void handleSaveResult() {
        if (resultPreview.getImage() == null) return;

        FileChooser fc = new FileChooser();
        fc.setTitle("Save image");
        fc.setInitialFileName("result.png");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File dest = fc.showSaveDialog(new Stage());
        if (dest != null) {
            try {
                String url = resultPreview.getImage().getUrl();
                File source = new File(new java.net.URI(url));
                Files.copy(source.toPath(), dest.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                errorHandler.error(encodeErrorLabel, e.getMessage());
            }
        }
    }

    //temporary decoding test
    public static void main(String[] args) {
    }
}