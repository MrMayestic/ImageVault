package com.imagevault;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import atlantafx.base.theme.NordDark;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/main-view.fxml")
        );
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        double width = screen.getWidth() * 0.5;
        double height = screen.getHeight() * 0.7;

        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setTitle("ImageVault");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}