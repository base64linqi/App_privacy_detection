package com.linqi.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationInfo {
    /**
     * 用于显示在分页按钮上的名称
     */
    private String showName;
    private  Integer id;
    private String appName;
    private String identifier;
    private Date startTime;
    private String stopMode;
    private LocalDateTime stopTime;
    private Integer index;
    private Integer projectId;

    /**
     * 全部检测信息
     */
    private List<DetectionInfo> allDetectionInfos;

    /**
     * 筛选过的检测信息
     */
    private List<DetectionInfo> filteredDetectionInfos;


    public ApplicationInfo(){
        allDetectionInfos = new ArrayList<>();
    }

    @Override
    public String toString() {
        return this.getAppName();
    }


}
