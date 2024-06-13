package com.linqi.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DetectionExportInfo {
    private Date  callTime;
    private String callPermission;
    private String method;
    private String stacksShow;
}
