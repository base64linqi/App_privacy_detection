package com.linqi.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProjectInfo {
    private Integer id;
    private String projectName;
    private Date startTime;
    private Integer index;
    private String type;
    /**
     * 当前选中的application
     */
    private ApplicationInfo currentApplicationInfo;

    public ProjectInfo(){
        applicationInfoList = new ArrayList<>();
    }

    private List<ApplicationInfo> applicationInfoList;
}
