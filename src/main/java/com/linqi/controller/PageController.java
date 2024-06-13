package com.linqi.controller;

import com.alibaba.druid.util.StringUtils;
import com.linqi.utils.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
public class PageController implements Initializable {

    @FXML
    private TextField tbxName;

    @FXML
    private AnchorPane root;


    private Stage stage;

    private Boolean confirm;

    private String pageName;


    /**
     * 确定添加或修改
     * @param actionEvent
     */
    public void onConfirm(ActionEvent actionEvent) {
        if (StringUtils.isEmpty( tbxName.getText())){
            AlertUtil.showErrAlert("必须输入页面名称");
            return;
        }
        pageName = tbxName.getText();
        stage  = getStage();
        confirm = true;
        stage.close();
    }

    /**
     * 取消
     * @param actionEvent
     */
    @FXML
    public void onCancel(ActionEvent actionEvent) {
        confirm = false;
        this.stage = getStage();
        this.stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setConfirm(false);
    }


    private Stage getStage() {
        if (stage == null) {
            stage = (Stage) root.getScene().getWindow();
        }
        return stage;
    }
}
