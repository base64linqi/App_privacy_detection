package com.linqi.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 检测信息
 */
@Getter
@Setter
public class DetectionInfo {
    private Integer id;
    private Date callTime;
    private String method;
    private String callPermission;
    private String description;

    public String getCallPermissionDesc() {
        return String.format("获取%s信息", this.getCallPermission());
    }

    public String getStacksShow() {
        if (this.stack == null){
            return  "";
        }
        return String.join("\r\n", stack);
    }

    private String stacksShow;



    private String callPermissionDesc;

    private String callClass;
    private List<String> stack;
    private int orderIndex;
    private int callCount;



    public DetectionInfo(){
        callCount = 1;
        stack = new ArrayList<>();
    }
}
