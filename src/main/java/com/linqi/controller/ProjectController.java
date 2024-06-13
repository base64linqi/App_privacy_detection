package com.linqi.controller;

import com.alibaba.druid.util.StringUtils;
import com.linqi.constant.CommonName;
import com.linqi.entity.ProjectInfo;
import com.linqi.utils.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
public class ProjectController implements Initializable {

    @FXML
    public RadioButton rbxAndroidFrida;
    @FXML
    public RadioButton rdoIos;
    @FXML
    public RadioButton rbxAndroidXposed;
    @FXML
    private TextField tbxName;

    @FXML
    private AnchorPane root;



    private Stage stage;

    private ProjectInfo projectInfo;

    private Boolean confirm;

    private String projectName;

    /**
     * 测试类型
     */
    private String testType;


    /**
     * 确定添加或修改
     * @param actionEvent
     */
    public void onConfirm(ActionEvent actionEvent) {
        if (StringUtils.isEmpty( tbxName.getText())){
            AlertUtil.showErrAlert("必须输入方案名称");
            return;
        }
        if (rbxAndroidFrida.isSelected()) {
            this.testType = CommonName.MACHINE_TYPE_ANDROID_FRIDA;
        } else if (rbxAndroidXposed.isSelected()){
            this.testType = CommonName.MACHINE_TYPE_ANDROID_XPOSED;
        } else {
            this.testType = CommonName.MACHINE_TYPE_IOS;
        }
        projectName = tbxName.getText();
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

