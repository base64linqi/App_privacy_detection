package com.linqi.utils;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;

public class JavaFxUtil {
    /**
     * 添加一个菜单到菜单bar
     * @param menuBar 传入的menubar
     * @param text 菜单的文本
     * @param handler 点击后的处理
     */
    public static Menu addMenu(MenuBar menuBar, String text, EventHandler<MouseEvent> handler){
        Menu menu = new Menu();
        Label label = new Label(text);
        label.setOnMouseClicked(handler);
        menu.setGraphic(label);
        menuBar.getMenus().add(menu);
        return menu;
    }

    public static void setControlText(StringProperty property, String text){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                property.set(text);
            }
        });
    }
}
