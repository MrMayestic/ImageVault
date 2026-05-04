package com.imagevault.controller;

import com.imagevault.model.EncryptData;
import com.imagevault.model.StegoDecoder;
import com.imagevault.model.StegoEncoder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class TaskController {

    @FXML private Label imagePath;
    @FXML private Label textPath;
    @FXML private TextField passwordField;
    @FXML private TextArea outputArea;

    private File selectedImage;
    private File selectedText;

    @FXML
    private void handleLoadImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Wybierz obraz");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Obrazy", "*.jpg", "*.png")
        );
        selectedImage = fc.showOpenDialog(new Stage());
        if (selectedImage != null) {
            imagePath.setText(selectedImage.getName());
        }
    }

    @FXML
    private void handleLoadText() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Wybierz plik tekstowy");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Tekst", "*.txt")
        );
        selectedText = fc.showOpenDialog(new Stage());
        if (selectedText != null) {
            textPath.setText(selectedText.getName());
        }
    }

    @FXML
    private void handleEncode() {
        if (selectedImage == null || selectedText == null) {
            outputArea.setText("Błąd: wybierz obraz i plik tekstowy.");
            return;
        }
        if (passwordField.getText().isEmpty()) {
            outputArea.setText("Błąd: podaj hasło.");
            return;
        }
        try {
            String text = Files.readString(Path.of(selectedText.getAbsolutePath()));
            String encrypted = EncryptData.encrypt(text, EncryptData.generateKey(passwordField.getText()));
            String outputPath = selectedImage.getParent() + "/result";
            StegoEncoder.encode(selectedImage.getAbsolutePath(), encrypted, outputPath);
            outputArea.setText("Zakodowano pomyślnie!\nWynik: " + outputPath + ".png");
        } catch (Exception e) {
            outputArea.setText("Błąd: " + e.getMessage());
        }
    }

    @FXML
    private void handleDecode() {
        if (selectedImage == null) {
            outputArea.setText("Błąd: wybierz obraz do odkodowania.");
            return;
        }
        if (passwordField.getText().isEmpty()) {
            outputArea.setText("Błąd: podaj hasło.");
            return;
        }
        try {
            String decodedPath = selectedImage.getParent() + "/decoded.txt";
            String decoded = StegoDecoder.decode(selectedImage.getAbsolutePath(), decodedPath);
            String decrypted = EncryptData.decrypt(decoded, EncryptData.generateKey(passwordField.getText()));
            outputArea.setText("Odkodowano:\n" + decrypted);
        } catch (Exception e) {
            outputArea.setText("Błąd: " + e.getMessage());
        }
    }
}