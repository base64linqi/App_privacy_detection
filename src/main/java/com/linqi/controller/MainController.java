package com.linqi.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.linqi.constant.CommonName;
import com.linqi.constant.SearchCategories;
import com.linqi.dao.ApplicationInfoDao;
import com.linqi.dao.DatabaseHelper;
import com.linqi.entity.*;
import com.linqi.handler.MainProcessHandler;
import com.linqi.handler.XposedProcessHandler;
import com.linqi.utils.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.poi.util.StringUtil;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class MainController implements Initializable {

    @FXML
    public AnchorPane paneXposedButton;
    @FXML
    public TextField tbxAppName;
    @FXML
    private ContextMenu ctxMenuTable;
    @FXML
    private Label lblProject;
    @FXML
    private AnchorPane root;

    @FXML
    private MenuBar menuBar;

    @FXML
    private ComboBox cbxAppList;

    //颜色
    @FXML
    private void setPinkTheme(ActionEvent event) {
        root.setStyle("-fx-background-color: pink;");
        menuBar.setStyle("-fx-background-color: pink;");
    }

    @FXML
    private TextField tbxAppFilter;

    @FXML
    private AnchorPane paneButtons;

    @FXML
    private TextField tbxSearch;

    /**
     * 用于实时处理命令行任务
     */
    private MainProcessHandler processHandler = null;

    /**
     * 退出进程通知
     */
    private ProcessInfo processInfo;


    private ObservableList<ApplicationInfo> applicationInfos;

    @FXML
    private FlowPane paneCalls;

    @FXML
    private Label lblAppName;


    private ObservableList<DetectionInfo> detectionInfos;

    private ObservableList<DetectionInfo> detailDetectionInfos;

    @FXML
    private TableView tblList;

    @FXML
    private TableView tblDetail;

    @FXML
    private TextArea txaStackInfo;

    @FXML
    private Label lblCallTimes;

    @FXML
    private DataListController dataListController;

    /**
     * 当前选中的标签
     */
    private String selectedLabel;

    @FXML
    private HBox boxPages;

    @FXML
    private ComboBox cbxSearchCategory;

    /**
     *
     */
    private ProjectInfo projectInfo;

    /**
     * 当前选中的页面按钮
     */
    private Button selectedBtnPage;

    /**
     * 数据库操作
     */
    private ApplicationInfoDao applicationInfoDao = new ApplicationInfoDao();

    /**
     * 默认页面名称
     */
    private String[] defaultPageNames = new String[]{"隐私前", "隐私后", "测试", "生产"};

    private Stage mainStage;

    /**
     * 正在检测中
     */
    private boolean isDetecting = false;

    public void resetPages() {
        boxPages.getChildren().clear();
        if (projectInfo.getApplicationInfoList() != null) {
            for (int i = 0; i < projectInfo.getApplicationInfoList().size(); i++) {
                ApplicationInfo app = projectInfo.getApplicationInfoList().get(i);
                Button btn = addPageBtn(app.getShowName());
                if (i == 0) {
                    selectedBtnPage = btn;
                    selectPageBtn(btn);
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        String iniPath = null;
        try {
            iniPath = PathUtil.getIniFilePath();
            if (!FileUtil.exist(iniPath)) {
                // 不存在ini文件
                File file = new File(iniPath);
                try {
                    file.createNewFile();
                } catch (Exception ex) {
                    AlertUtil.showErrAlert(String.format("创建配置文件失败%s", ex.getMessage()));
                    return;
                }
                Ini ini = new Ini();
                ini.load(file);

                Profile.Section section = ini.add("js");
                section.add("yinsi.js", "1");
                section.add("other.js", "0");

                ini.store(file);

            }
        } catch (Exception ex) {
            AlertUtil.showErrAlert(ex.getMessage());
            return;
        }
        JavaFxUtil.addMenu(menuBar, "新建测试方案", event -> {
            addUpdateProject("a");
        });
        JavaFxUtil.addMenu(menuBar, "历史数据", event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/datalist.fxml"));
                AnchorPane pane = loader.load();
                DataListController dataListController = loader.getController();
                dataListController.setMainController(this);
                Stage stage = new Stage();
                stage.setTitle("历史数据");
                Scene scene = new Scene(pane, 800, 500);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        JavaFxUtil.addMenu(menuBar, "导出结果", event -> {
            System.out.println("导出结果");
            exportResult();
        });

        Menu menu = JavaFxUtil.addMenu(menuBar, "选择js文件", event -> {


        });

        // 为搜索类添加项
        ObservableList<String> searchCategories = FXCollections.observableArrayList(
                SearchCategories.CLASS, SearchCategories.METHOD, SearchCategories.STACK
        );
        cbxSearchCategory.setItems(searchCategories);


        // 读取ini文件
        try {
            iniPath = PathUtil.getIniFilePath();
            if (FileUtil.exist(iniPath)) {
                // 不存在ini文件
                File file = new File(iniPath);
                Ini ini = new Ini();
                ini.load(file);
                Profile.Section section = ini.get("js");
                for (String key : section.keySet()) {
                    MenuItem mi = new MenuItem();
                    String text = key;
                    if (section.get(key).equals("1")) {
                        text += "  √";
                    }
                    mi.setText(text);
                    mi.setId(key);
                    mi.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            for (String k : section.keySet()) {
                                section.put(k, "0");
                                mi.setText(mi.getId());
                                if (k.equals(mi.getId())) {
                                    section.put(k, "1");
                                }
                            }
                            for (MenuItem item : menu.getItems()) {
                                item.setText(item.getId());
                            }
                            mi.setText(mi.getId() + "  √");
                            try {
                                ini.store(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    menu.getItems().add(mi);
                }

            }
        } catch (Exception ex) {
            AlertUtil.showWarningAlert(String.format("读取配置文件出错%s", ex.getMessage()));
        }
        // 添加菜单
        JavaFxUtil.addMenu(menuBar, "About", event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/about.fxml"));
                Stage stage = new Stage();
                stage.setTitle("About");
                Scene scene = new Scene(root, 390, 350);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // 应用列表发生选择改变的事件
        /*
        cbxAppList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Integer index = (int) newValue;
                if (index > -1) {
                    ApplicationInfo applicationInfo = applicationInfos.get(index);
                    tbxAppId.setText(applicationInfo.getIdentifier());
                }
            }
        });
         */
        try {
            DatabaseHelper.createTables();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        paneButtons.setDisable(true);
        for (int i = 0; i < defaultPageNames.length; i++) {
            Button btn = addPageBtn(defaultPageNames[i]);
            if (i == 0) {
                selectPageBtn(btn);
            }
        }

    }

    /**
     * 导出检测结果
     */
    private void exportResult() {
        if (this.projectInfo == null) {
            AlertUtil.showErrAlert("没有可以导出的数据");
            return;
        }
        List<ApplicationInfo> appList = projectInfo.getApplicationInfoList();
        if (appList == null || appList.size() == 0) {
            AlertUtil.showErrAlert("没有检测信息");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Excel", "*.xls");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(mainStage);

        ExcelWriter excelWriter = ExcelUtil.getWriter(file);
        excelWriter.addHeaderAlias("callTime", "时间点");
        excelWriter.addHeaderAlias("callPermission", "操作行为");
        excelWriter.addHeaderAlias("method", "行为描述");
        excelWriter.addHeaderAlias("stacksShow", "调用堆栈");

        for (int i = 0; i < appList.size(); i++) {
            ApplicationInfo app = appList.get(i);
            excelWriter.setSheet(app.getShowName());
            List<DetectionExportInfo> exportList = new ArrayList<>();
            for (DetectionInfo detectionInfo : app.getAllDetectionInfos()) {
                DetectionExportInfo ex = new DetectionExportInfo();
                BeanUtil.copyProperties(detectionInfo, ex);
                exportList.add(ex);
            }
            ArrayList<DetectionExportInfo> arrayList = CollUtil.newArrayList(exportList);
            excelWriter.write(arrayList, true);
            excelWriter.flush();

        }

        excelWriter.close();
    }

    /**
     * 清除搜索内容
     */
    public void onBtnClearSearch() {
        System.out.println("do clear search");
        tbxSearch.setText("");
        ApplicationInfo applicationInfo = projectInfo.getCurrentApplicationInfo();
        refreshDatas(applicationInfo);
    }

    public void onBtnDoSearch() {
        System.out.println("do search");
        System.out.println(tbxSearch.getText());
        // 如果正在检测中，不允许搜索
        if (isDetecting) {
            AlertUtil.showWarningAlert("正在检测中，不能执行搜索");
            return;
        }
        ApplicationInfo applicationInfo = projectInfo.getCurrentApplicationInfo();
        refreshDatas(applicationInfo);
    }

    /**
     * 点击了获取应用列表的按钮
     *
     * @param actionEvent
     */
    public void onBtnGetAppListAction(ActionEvent actionEvent) {
        applicationInfos = null;
        List<String> commands = new ArrayList<>();
        commands.add("frida-ps");
        commands.add("-Uai");
        new Thread(() -> {
            try {
                processInfo = new ProcessInfo();
                List<String> results = CommandUtil.executeByProcessBuilder(commands, false, 0, null, processInfo, "GBK");
                // 正确的结果是第一行以PID开头
                if (results.size() == 0) {
                    return;
                }
                // 如果有结果，但不以PID开头，则是异常信息
                String firstRow = results.get(0).trim();
                if (!firstRow.startsWith("PID")) {
                    AlertUtil.showWarningAlert(String.join(" ", results));
                    return;
                }
                // 返回的数据格式
            /*
            PID  Name                  Identifier
            ----  --------------------  ---------------------------
            2289  设置                    com.android.settings
            -  Amaze                 com.amaze.filemanager
            -  EncrptionTool         com.example.encrptiontool
            -  HBuilder              io.dcloud.HBuilder
            -  Lathe Simulator Lite  com.virtlab.lathesim_lite
            -  empmanagement         uni.UNIDCE331A
             */
                List<ApplicationInfo> applicationInfoList = new ArrayList<>();
                for (int i = 2; i < results.size(); i++) {
                    String[] arr = results.get(i).trim().split("\\s+");
                    ApplicationInfo applicationInfo = new ApplicationInfo();
                    applicationInfo.setAppName(arr[1]);
                    applicationInfo.setIdentifier(arr[2]);
                    String uuid = UUID.randomUUID().toString();
                    applicationInfoList.add(applicationInfo);
                }
                Platform.runLater(() -> {

                    applicationInfos = FXCollections.observableList(applicationInfoList);
                    cbxAppList.setItems(applicationInfos);
                    if (applicationInfos != null && applicationInfos.size() > 0) {
                        cbxAppList.getSelectionModel().select(0);
                    }
                });
            } catch (Exception ex) {
                AlertUtil.showErrAlert(ex.getMessage());
            } finally {
                Platform.runLater(() -> {
                    paneButtons.setDisable(false);

                });
            }

        }).start();
        paneButtons.setDisable(true);

    }

    /**
     * 点击了Spawing
     *
     * @param actionEvent
     */
    public void onBtnStartSpawingAction(ActionEvent actionEvent) {
        doDetection(EInjectMode.SPAWING);
    }

    /**
     * 点击了Attach
     *
     * @param actionEvent
     */
    public void onBtnStartAttachAction(ActionEvent actionEvent) {
        doDetection(EInjectMode.ATTACH);
    }


    /**
     * 执行xposed检测
     */
    private void doXposedDetection() {
        if (this.mainStage == null) {
            mainStage = (Stage) root.getScene().getWindow();

            mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    MainController.this.closeProcessAction();
                }
            });
        }
        // 设置检测标志
        isDetecting = true;
        String commandStr = null;
        String appName = tbxAppName.getText();
        commandStr = "adb logcat -s LSPosed-Bridge";
        // 先在解决方案的appList下找，如果没有则新建并加入列表
        Optional<ApplicationInfo> optApp = projectInfo.getApplicationInfoList().stream().filter(
                ap -> ap.getShowName().equals(selectedBtnPage.getText())).findFirst();
        if (optApp.isPresent()) {
            projectInfo.setCurrentApplicationInfo(optApp.get());
        } else {
            // 新建一个检测信息
            ApplicationInfo app = new ApplicationInfo();
            app.setProjectId(projectInfo.getId());
            app.setShowName(selectedBtnPage.getText());
            projectInfo.getApplicationInfoList().add(app);
            projectInfo.setCurrentApplicationInfo(app);
        }

        List<String> commands = new ArrayList<>(Arrays.asList(commandStr.split(" ")));
        final String appNameFinal = appName;
        new Thread(() -> {
            executeJsFile(commands, appNameFinal, CommonName.MACHINE_TYPE_ANDROID_XPOSED);
        }).start();
        paneButtons.setDisable(true);
        menuBar.setDisable(true);
    }


    /**
     * 执行检测
     *
     * @param injectMode 注入模式，spawing，attach
     */
    private void doDetection(EInjectMode injectMode) {

        if (this.mainStage == null) {
            mainStage = (Stage) root.getScene().getWindow();

            mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    MainController.this.closeProcessAction();
                }
            });
        }

        String jsPath = null;
        try {
            String iniPath = null;
            iniPath = PathUtil.getIniFilePath();
            // 从配置文件中读取
            Ini ini = new Ini();
            File file = new File(iniPath);
            ini.load(file);
            Profile.Section section = ini.get("js");
            String js = null;
            for (String key : section.keySet()) {
                if (section.get(key).equals("1")) {
                    js = key;
                    break;
                }
            }
            if (js == null) {
                AlertUtil.showErrAlert("配置文件中没有找到有效的js文件");
                return;
            }
            jsPath = PathUtil.getFridaJsPath(js);
            if (!FileUtil.exist(jsPath)) {
                AlertUtil.showErrAlert(String.format("没有找到文件%s", jsPath));
                return;
            }
        } catch (Exception ex) {
            AlertUtil.showErrAlert(ex.getMessage());
            return;
        }
        // 设置检测标志
        isDetecting = true;
        String commandStr = null;
        String appName = null;
        if (injectMode == EInjectMode.ATTACH) {
            commandStr = String.format("frida -UF -l %s --no-pause", jsPath);
        } else {
            ApplicationInfo app = ((ApplicationInfo) cbxAppList.getSelectionModel().getSelectedItem());
            if (app == null) {
                AlertUtil.showErrAlert("没有选择应用");
                return;
            }
            String appId = app.getIdentifier();
            if (appId == null || appId.trim() == "") {
                AlertUtil.showWarningAlert("没有选择应用或输入应用Identified");
                return;
            }
            commandStr = String.format("Frida -U -f %s -l %s --no-pause", appId, jsPath);
            appName = app.getAppName();
        }
        // 先在解决方案的appList下找，如果没有则新建并加入列表
        Optional<ApplicationInfo> optApp = projectInfo.getApplicationInfoList().stream().filter(
                ap -> ap.getShowName().equals(selectedBtnPage.getText())).findFirst();
        if (optApp.isPresent()) {
            projectInfo.setCurrentApplicationInfo(optApp.get());
        } else {
            // 新建一个检测信息
            ApplicationInfo app = new ApplicationInfo();
            app.setProjectId(projectInfo.getId());
            app.setShowName(selectedBtnPage.getText());
            projectInfo.getApplicationInfoList().add(app);
            projectInfo.setCurrentApplicationInfo(app);
        }

        List<String> commands = new ArrayList<>(Arrays.asList(commandStr.split(" ")));
        final String appNameFinal = appName;
        new Thread(() -> {
            executeJsFile(commands, appNameFinal, CommonName.MACHINE_TYPE_ANDROID_FRIDA);
        }).start();
        paneButtons.setDisable(true);
        menuBar.setDisable(true);
    }

    /***
     * 执行frida命令
     * @param commands
     */
    private void executeJsFile(List<String> commands, String appName, String deleteType) {
        String charset = "GBK";
        if (deleteType.equals(CommonName.MACHINE_TYPE_ANDROID_XPOSED)) {
            charset = "UTF8";
        }
        try {


            ApplicationInfo applicationInfo = projectInfo.getCurrentApplicationInfo();

            applicationInfo.setAppName(appName);
            Platform.runLater(() -> {
                refreshDatas(applicationInfo);
            });
            processHandler.setApplicationInfo(applicationInfo);
            processHandler.setMainController(this);
            processInfo = new ProcessInfo();
            List<String> results = CommandUtil.executeByProcessBuilder(commands, false, 0, processHandler, processInfo, charset);
            if (!processHandler.getFoundApp()) {
                Platform.runLater(() -> {

                    AlertUtil.showErrAlert("出错，没有找到应用信息，错误信息如下:\r\n" +
                            String.join("", results));
                });
                return;
            }
            System.out.printf("输出完了");
            // 检测结束 ，保存结束 信息
            processHandler.saveEndDetection();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Platform.runLater(() -> {
                paneButtons.setDisable(false);
                menuBar.setDisable(false);
            });
        }
    }

    /**
     * 中止正在进行的检测,实际上就是把一个标志设置为false
     *
     * @param actionEvent
     */
    public void onExitFirdaProcessAction(ActionEvent actionEvent) {
        closeProcessAction();
    }

    public void closeProcessAction() {
        isDetecting = false;
        if (processInfo.getProcess() == null) {
            AlertUtil.showErrAlert("没有获取到命令行进程");
            return;
        }
        OSUtil.killProcessByPid(processInfo.getProcess());
        try {
            processInfo.getReader().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新界面数据
     */
    public void refreshDatas(ApplicationInfo applicationInfo) {

        Platform.runLater(() -> {
            if (applicationInfo == null) {
                tblList.setItems(null);
                paneCalls.getChildren().clear();
                lblAppName.setText("");
                lblCallTimes.setText("调用次数0");
                return;
            }
            // 数据过滤
            // 如果选择了标签，进行一次过滤
            List<DetectionInfo> list = applicationInfo.getAllDetectionInfos();
            if (this.selectedLabel != null) {
                list = list
                        .stream()
                        .filter(di -> selectedLabel.equals(di.getCallPermission()))
                        .collect(Collectors.toList()
                        );

            }
            // 如果输入了搜索内容，进行一次过滤
            String searchContent = tbxSearch.getText();

            if (searchContent != null && !"".equals(searchContent)) {
                searchContent = searchContent.toLowerCase();
                String finalSearchContent = searchContent;
                // 没有选择搜索种类
                if (cbxSearchCategory.getValue() == null || cbxSearchCategory.getValue().equals("")) {
                    list = list.stream().filter(d -> (d.getCallClass() != null && d.getCallClass().toLowerCase().contains(finalSearchContent))
                            || (d.getMethod() != null && d.getMethod().toLowerCase().contains(finalSearchContent))
                            || (d.getStacksShow() != null && d.getStacksShow().toLowerCase().contains(finalSearchContent))
                    ).collect(Collectors.toList());
                } else {
                    // 只查找类
                    if (cbxSearchCategory.getValue().equals(SearchCategories.CLASS)) {
                        list = list.stream().filter(d -> d.getCallClass() != null && d.getCallClass().toLowerCase().contains(finalSearchContent))
                                .collect(Collectors.toList());
                    } else if (cbxSearchCategory.getValue().equals(SearchCategories.METHOD)) {
                        // 只查找方法
                        list = list.stream().filter(d -> d.getMethod() != null && d.getMethod().toLowerCase().contains(finalSearchContent))
                                .collect(Collectors.toList());
                    } else if (cbxSearchCategory.getValue().equals(SearchCategories.STACK)) {
                        // 只查找堆栈
                        list = list.stream().filter(d -> d.getStacksShow() != null && d.getStacksShow().toLowerCase().contains(finalSearchContent))
                                .collect(Collectors.toList());
                    }
                }
            }

            applicationInfo.setFilteredDetectionInfos(list);
            for (int i = 0; i < applicationInfo.getFilteredDetectionInfos().size(); i++) {
                applicationInfo.getFilteredDetectionInfos().get(i).setOrderIndex(i + 1);
            }
            tblDetail.setItems(null);
            txaStackInfo.setText(null);
            lblAppName.setText(applicationInfo.getAppName());
            lblCallTimes.setText("调用次数：" + applicationInfo.getFilteredDetectionInfos().size());
            detectionInfos = FXCollections.observableList(applicationInfo.getFilteredDetectionInfos());
            tblList.setItems(detectionInfos);
            paneCalls.getChildren().clear();
            Map<String, Long> map = applicationInfo.getAllDetectionInfos().stream()
                    .collect(Collectors.groupingBy(DetectionInfo::getCallPermission, Collectors.counting()));
            Object objdd = map;

            map.forEach((name, count) -> {
                HBox box = new HBox();
                box.setPadding(new Insets(10));
                Label lbl = new Label();
                lbl.setTextFill(Color.web("#FFFFFF"));
                if (name.equals(selectedLabel)) {
                    lbl.setStyle("-fx-background-color: #FF0000");
                } else {
                    lbl.setStyle("-fx-background-color: #0000FF");
                }
                lbl.setAlignment(Pos.CENTER);
                lbl.setPrefWidth(100);

                lbl.setPrefHeight(30);
                lbl.setText(String.format("%s(%d)", name, count));
                lbl.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // 点击后的筛选数据
                        if (selectedLabel != null) {
                            // 如果是选中的按钮，取消即可
                            if (selectedLabel.equals(name)) {
                                selectedLabel = null;
                            } else {
                                // 否则 ，切换选择
                                selectedLabel = name;
                            }
                        } else {
                            selectedLabel = name;
                        }
                        refreshDatas(applicationInfo);
                    }
                });
                box.getChildren().add(lbl);
                paneCalls.getChildren().add(box);
            });
        });
    }

    /**
     * 单击左侧表格
     *
     * @param mouseEvent
     */
    public void onTblListClicked(MouseEvent mouseEvent) {
        DetectionInfo detectionInfo = (DetectionInfo) tblList.getSelectionModel().getSelectedItem();
        if (detectionInfo == null) {
            return;
        }
        List<DetectionInfo> list = new ArrayList<>();
        list.add(detectionInfo);
        Platform.runLater(() -> {

            detailDetectionInfos = FXCollections.observableList(list);
            tblDetail.setItems(detailDetectionInfos);
            txaStackInfo.setText(detectionInfo.getStacksShow());
        });

        // 如果是右键，弹出删除菜单
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            ctxMenuTable.show(tblList, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        } else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        }
    }

    /**
     * 安装应用
     *
     * @param actionEvent
     */
    public void onBtnInstallAppClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("apk文件", "*.apk"));
        File file = fileChooser.showOpenDialog(this.getCbxAppList().getScene().getWindow());
        if (file != null) {
            String command = String.format("adb install %s", file.toPath());
            List<String> commands = Arrays.asList(command.split(" "));
            try {
                List<String> results = CommandUtil.executeByProcessBuilder(commands, false, 0, null, null, "GBK");

                AlertUtil.showInfoAlert(String.join(" ", results));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 过滤应用列表
     *
     * @param actionEvent
     */
    public void onBtnFilterAppListClicked(ActionEvent actionEvent) {
        if (tbxAppFilter.getText().trim().equals("")) {
            cbxAppList.setItems(applicationInfos);
        } else {
            FilteredList<ApplicationInfo> filtered = applicationInfos.filtered(a -> a.getAppName()
                    .toLowerCase(Locale.ROOT).contains(tbxAppFilter.getText().toLowerCase(Locale.ROOT)));
            cbxAppList.setItems(filtered);
        }
    }

    /**
     * 添加页面
     *
     * @param actionEvent
     */
    public void onAddPageClick(ActionEvent actionEvent) {
        addPageBtn("新页面");
    }

    /**
     * 添加按钮到父容器
     *
     * @param name
     */
    private Button addPageBtn(String name) {
        Button btnPage = new Button();
        btnPage.setVisible(true);
        btnPage.setText(name);
        btnPage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectPageBtn(btnPage);
            }
        });
        btnPage.setAlignment(Pos.CENTER);
        boxPages.getChildren().add(btnPage);
        return btnPage;
    }

    /**
     * 添加解决方案
     *
     * @param actionEvent
     */
    public void onAddProject(ActionEvent actionEvent) {
        addUpdateProject("a");
    }

    /**
     * 根据检测类型初始化界面控件
     *
     * @param type
     */
    public void initByDetectType(String type) {

        if (type.equals(CommonName.MACHINE_TYPE_ANDROID_XPOSED)) {
            paneXposedButton.setVisible(true);
            paneButtons.setVisible(false);
            paneXposedButton.setDisable(false);
            processHandler = new XposedProcessHandler();
        } else if (type.equals(CommonName.MACHINE_TYPE_ANDROID_FRIDA)) {
            paneXposedButton.setVisible(false);
            paneButtons.setVisible(true);
            paneButtons.setDisable(false);
            processHandler = new MainProcessHandler();
        }
    }


    private void addUpdateProject(String act) {
        try {
            Stage mainStage = (Stage) root.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/project.fxml"));
            if (act.equals("a")) {
                projectInfo = new ProjectInfo();
                projectInfo.setStartTime(new Date());
            }
            ProjectController projectController = new ProjectController();
            loader.setControllerFactory(param -> {
                return projectController;
            });
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();
            stage.setTitle(act == "a" ? "新建测试方案" : "修改测试方案");
            Scene scene = new Scene(root, 390, 200);
            stage.setScene(scene);
            stage.initOwner(mainStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

            if (projectController.getConfirm()) {
                projectInfo.setProjectName(projectController.getProjectName());
                lblProject.setText(projectInfo.getProjectName());
                projectInfo.setType(projectController.getTestType());
                // 根据检测类型初始化界面
                initByDetectType(projectController.getTestType());
                if (act.equals("a")) {
                    try {
                        applicationInfoDao.insertProject(projectInfo);
                        AlertUtil.showInfoAlert("添加成功");
                    } catch (Exception ex) {
                        AlertUtil.showErrAlert(ex.getMessage());
                    }
                } else {
                    try {
                        applicationInfoDao.updateProject(projectInfo);
                        AlertUtil.showInfoAlert("修改成功");
                    } catch (Exception ex) {
                        AlertUtil.showErrAlert(ex.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 打开解决方案
     *
     * @param actionEvent
     */
    public void onOpenProject(ActionEvent actionEvent) {
        addUpdateProject("u");
    }

    /**
     * 点击了切换页面按钮
     *
     * @param btnPage
     */
    private void selectPageBtn(Button btnPage) {
        for (Button btn : getAllPageBtns()) {
            btn.setTextFill(Color.BLACK);
            btn.setBackground(new Background(new BackgroundFill(Color.web("#DDDDDD"), null, null)));
        }
        selectedBtnPage = btnPage;
        btnPage.setTextFill(Color.BLUE);
        btnPage.setBackground(new Background(new BackgroundFill(Color.web("#AAAAAA"), null, null)));
        if (projectInfo != null) {
            // 如果点击后，该页面对应的app存在，则询问是否覆盖旧的数据
            Optional<ApplicationInfo> optApp = projectInfo.getApplicationInfoList().stream().filter(app -> app.getShowName().equals(selectedBtnPage.getText()))
                    .findFirst();
            if (optApp.isPresent()) {
                projectInfo.setCurrentApplicationInfo(optApp.get());
            } else {
                if (isDetecting) {
                    // 正在检测中
                    ApplicationInfo app = new ApplicationInfo();
                    projectInfo.getApplicationInfoList().add(app);
                    app.setShowName(btnPage.getText());
                    app.setProjectId(projectInfo.getId());
                    app.setStartTime(new Date());
                    if (projectInfo.getCurrentApplicationInfo() != null) {
                        app.setAppName(projectInfo.getCurrentApplicationInfo().getAppName());
                    } else {
                        app.setAppName(cbxAppList.getSelectionModel().getSelectedItem().toString());
                    }
                    projectInfo.setCurrentApplicationInfo(app);
                    try {
                        applicationInfoDao.insert(app);
                        processHandler.setApplicationInfo(app);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        AlertUtil.showErrAlert(ex.getMessage());
                    }
                } else {
                    projectInfo.setCurrentApplicationInfo(null);
                }
            }
            refreshDatas(projectInfo.getCurrentApplicationInfo());
        }
    }

    /**
     * 获取所有的页面按钮
     *
     * @return
     */
    private List<Button> getAllPageBtns() {
        List<Button> btns = new ArrayList<>();
        ObservableList<Node> children = boxPages.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Button btn = (Button) children.get(i);
            btns.add(btn);
        }
        return btns;
    }

    /**
     * 修改页面按钮名称
     *
     * @param actionEvent
     */
    public void onRename(ActionEvent actionEvent) {
        if (selectedBtnPage == null) {
            AlertUtil.showErrAlert("请点击选择页面");
            return;
        }

        try {
            Stage mainStage = (Stage) root.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/page.fxml"));
            PageController pageController = new PageController();
            loader.setControllerFactory(param -> {
                return pageController;
            });
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();
            stage.setTitle("修改页面名称");
            Scene scene = new Scene(root, 390, 200);
            stage.setScene(scene);
            stage.initOwner(mainStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

            if (pageController.getConfirm()) {
                selectedBtnPage.setText(pageController.getPageName());
                if (projectInfo.getCurrentApplicationInfo() != null) {
                    applicationInfoDao.reNameShow(projectInfo.getCurrentApplicationInfo(),
                            pageController.getPageName());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 删除选中的检测信息
     *
     * @param actionEvent
     */
    public void onDeleteDetectInfo(ActionEvent actionEvent) {
        DetectionInfo detectionInfo = (DetectionInfo) tblList.getSelectionModel().getSelectedItem();
        if (detectionInfo == null) {
            AlertUtil.showErrAlert("没有选中行");
            return;
        }
        try {
            applicationInfoDao.deleteDetection(detectionInfo.getId());
            detectionInfos.remove(detectionInfo);
            projectInfo.getCurrentApplicationInfo().getAllDetectionInfos().remove(detectionInfo);

        } catch (Exception ex) {
            AlertUtil.showErrAlert(ex.getMessage());
        }
        refreshDatas(projectInfo.getCurrentApplicationInfo());
    }

    /**
     * 点击 xposed检测开始 按钮
     *
     * @param actionEvent
     */
    public void onBtnStartXposedClick(ActionEvent actionEvent) {
        doXposedDetection();
    }

    /**
     * 清空当前解决方案
     *
     * @param actionEvent
     */
    public void onBtnClearProjectAction(ActionEvent actionEvent) {
        if (projectInfo != null && projectInfo.getId() != null) {
            Optional<ButtonType> result = AlertUtil.showConfirm("确定要清空當前解決方案?");
            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return;
            }
            try {
                applicationInfoDao.clearProject(projectInfo);
                projectInfo.setApplicationInfoList(new ArrayList<>());
                refreshDatas(null);
            } catch (Exception ex) {
                AlertUtil.showErrAlert(ex.getMessage());
            }
        }
    }
}
