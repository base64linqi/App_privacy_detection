package com.linqi.handler;

import com.linqi.constant.CommandFlag;
import com.linqi.constant.TimeConstant;
import com.linqi.controller.MainController;
import com.linqi.dao.ApplicationInfoDao;
import com.linqi.entity.ApplicationInfo;
import com.linqi.entity.DetectionInfo;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class MainProcessHandler implements IProcessHandler {
    private MainController mainController;
    private ApplicationInfo applicationInfo;
    private Boolean foundApp = false;

    private List<String> resultList = new ArrayList<>();

    Pattern pattern = Pattern.compile(CommandFlag.DEVICE_APPLICATION_NAME);
    private DetectionInfo detectionInfo = new DetectionInfo();

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TimeConstant.DATETIME_FORMATTER);
    private SimpleDateFormat sdf = new SimpleDateFormat(TimeConstant.DATETIME_FORMATTER_ALL);


    /**
     * 获取应用名称
     * @return
     */
    @Override
    public String getAppName(String str) {
        Matcher m = pattern.matcher(str);
        // 找到应用名称
        if (m.find()) {
            if (m.groupCount() > 3) {
                return (m.group(3));
            }
        }
        return null;
    }

    /**
     * 堆栈开始了
     */
    private Boolean stackStart = false;
    private static ApplicationInfoDao applicationInfoDao = new ApplicationInfoDao();

    /**
     * 添加app信息到数据库
     * @param appName
     */
    private void insertAppInfo(String appName) {
        if (applicationInfo.getAppName() == null || applicationInfo.getAppName().trim().equals("")) {
            applicationInfo.setAppName(appName);
        }
        applicationInfo.setStartTime(new Date());
        int id = -1;
        try {
            id = applicationInfoDao.insert(applicationInfo);
        }
        catch (Exception ex) {

        }
        applicationInfo.setId(id);
        mainController.refreshDatas(applicationInfo);
    }

    @Override
    public void handle(String str) throws Exception {
        if (str == null || str.isEmpty()) {
            return;
        }
        str = preprocessing(str);
        System.out.println("-------------------------------" + str);
        str = str.trim();
        Matcher m = pattern.matcher(str);
        // 找到应用名称
        String appName = getAppName(str);
        if (appName != null) {
                foundApp = true;
                insertAppInfo(appName);
        } else {
            // 找到一段开始
            if (str.startsWith(CommandFlag.COMMAND_START)) {
                // 如果还没有找到应用名称 ，则插入一个未知信息
                if (applicationInfo.getId() == null || applicationInfo.getId() == 0) {
                    insertAppInfo("未知应用");
                }
                System.out.println("获取开始信息");
                detectionInfo = new DetectionInfo();
                stackStart = false;
                // 时间
            } else if (str.startsWith(CommandFlag.TIME)) {
                System.out.println("获取时间信息");
                if (detectionInfo != null) {
                    detectionInfo.setCallTime(sdf.parse(str.split(CommandFlag.SEPARATOR)[1]));
                }
                // 方法
            } else if (str.startsWith(CommandFlag.METHOD)) {
                System.out.println("获取方法信息");
                if (detectionInfo != null) {
                    detectionInfo.setMethod(str.split(CommandFlag.SEPARATOR)[1]);
                }

            } else if (str.startsWith(CommandFlag.CALL_CLASS)){
                System.out.printf("获取类信息");
                if (detectionInfo != null) {
                    detectionInfo.setCallClass(str.split(CommandFlag.SEPARATOR)[1]);
                }
            }
            else if (str.startsWith(CommandFlag.DESCRIPTION)){
                System.out.println("获取概述信息");
                if (detectionInfo != null){
                    detectionInfo.setDescription(str.split(CommandFlag.SEPARATOR)[1]);
                }
            }
            else if (str.startsWith(CommandFlag.CALL_PERMISSION)) {
                System.out.printf("获取权限信息");
                if (detectionInfo != null) {
                    detectionInfo.setCallPermission(str.split(CommandFlag.SEPARATOR)[1]);
                }
            } else if (str.startsWith(CommandFlag.STACK)) {
                System.out.printf("开始堆栈信息");
                stackStart = true;
            } else if (str.startsWith(CommandFlag.COMMAND_END)) {
                System.out.println("获取结束信息");
                // 结束了一次检测片段，要进行一次插入
                if (applicationInfo.getId() == null || applicationInfo.getId() == 0){
                    System.out.println("有检测信息，但没有获取到应用信息");
                    // throw new Exception("获取到一次检测信息，但没有获取到应用信息");
                    return;
                }
                if (detectionInfo != null && detectionInfo.getCallPermission() != null) {
                    detectionInfo.setOrderIndex(applicationInfo.getAllDetectionInfos().size() + 1);
                    applicationInfoDao.insertDetection(applicationInfo.getId(), detectionInfo);
                    applicationInfo.getAllDetectionInfos().add(detectionInfo);
                    stackStart = false;
                    detectionInfo = null;
                    mainController.refreshDatas(applicationInfo);
                }
            } else {
                System.out.println("没有标记");
                if (stackStart) {
                    if (detectionInfo != null) {
                        detectionInfo.getStack().add(str);
                    }
                }
            }
        }
    }

    @Override
    public String preprocessing(String str) {
        return str;
    }


    public void saveEndDetection(){
        // applicationInfo.saveEndInfo(applicationInfo);
    }
}
