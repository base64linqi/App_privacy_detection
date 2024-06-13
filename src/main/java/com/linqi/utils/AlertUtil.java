package com.linqi.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtil {
    public static void showErrAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("");
        alert.setHeaderText("出错");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showWarningAlert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("");
        alert.setHeaderText("注意");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInfoAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText("信息");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Optional<ButtonType> showConfirm(String message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("请确认");
        alert.setHeaderText("确认框");
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return  result;
    }
}
