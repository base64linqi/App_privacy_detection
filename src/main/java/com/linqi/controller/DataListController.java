package com.linqi.controller;

import com.linqi.dao.ApplicationInfoDao;
import com.linqi.entity.ProjectInfo;
import com.linqi.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DataListController implements Initializable {
    @FXML
    private TableView tblList;
    private ApplicationInfoDao applicationInfoDao = new ApplicationInfoDao();
    @FXML
    private TableColumn tcDelete;
    @FXML
    private TableColumn tcOpen;

    @Setter
    private MainController mainController;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Object obj = mainController;
        refresh();
        tcDelete.setCellFactory(col ->{
            Button btnDelete = new Button("删除");
            TableCell<ProjectInfo, ProjectInfo> cell = new TableCell<ProjectInfo, ProjectInfo>(){
                @Override
                protected void updateItem(ProjectInfo item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty){
                        setGraphic(null);
                    }else {
                        setGraphic(btnDelete);
                    }
                }
            };
            btnDelete.setOnAction(e ->{
                ProjectInfo projectInfo = (ProjectInfo) cell.getTableRow().getItem();
                if (projectInfo == null){
                    AlertUtil.showErrAlert("请重试，数据错误");
                    return;
                }
                Optional<ButtonType> result = AlertUtil.showConfirm(String.format("确定要删除%s吗？", projectInfo.getProjectName()));
                if (!result.isPresent() || result.get() != ButtonType.OK){
                    return;
                }
                try {
                    applicationInfoDao.deleteProject(projectInfo.getId());
                    refresh();
                } catch (Exception exception) {
                    AlertUtil.showErrAlert("删除出错:" + exception.getMessage());
                    exception.printStackTrace();
                }
            });
            return cell;
        });
        tcOpen.setCellFactory(col ->{
            Button btnOpen = new Button("打开");
            TableCell<ProjectInfo, ProjectInfo> cell = new TableCell<ProjectInfo, ProjectInfo>(){
                @Override
                protected void updateItem(ProjectInfo item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty){
                        setGraphic(null);
                    }else {
                        setGraphic(btnOpen);
                    }
                }
            };
            btnOpen.setOnAction(e ->{
                ProjectInfo projectInfo = (ProjectInfo) cell.getTableRow().getItem();
                if (projectInfo == null){
                    AlertUtil.showErrAlert("请重试，数据错误");
                    return;
                }
                try {
                    applicationInfoDao.setAllChildInfo(projectInfo);
                    if (projectInfo.getApplicationInfoList().size() > 0){
                        projectInfo.setCurrentApplicationInfo(projectInfo.getApplicationInfoList().get(0));
                    }
                    mainController.getPaneButtons().setDisable(false);
                    mainController.setProjectInfo(projectInfo);
                    mainController.getLblProject().setText("方案:" +projectInfo.getProjectName());
                    mainController.resetPages();
                    mainController.initByDetectType(projectInfo.getType());
                    mainController.refreshDatas(projectInfo.getCurrentApplicationInfo());
                    Stage stage = (Stage)btnOpen.getScene().getWindow();
                    stage.close();
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            });
            return cell;
        });
    }
    private void refresh(){

        try {
            List<ProjectInfo> list = applicationInfoDao.getAllProject();
            Object obj = list;
            ObservableList<ProjectInfo> obsList = FXCollections.observableList(list);
            tblList.setItems(obsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
