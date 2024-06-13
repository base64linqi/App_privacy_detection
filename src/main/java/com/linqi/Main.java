package com.linqi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        stage.setTitle("隐私检测工具");
        Scene scene = new Scene(root, 1000, 700);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/icon.jpeg")));
        stage.setScene(scene);
        stage.show();
    }
}
