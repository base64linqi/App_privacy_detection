package com.linqi.handler;

import com.linqi.constant.CommandFlag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XposedProcessHandler extends MainProcessHandler{


    Pattern pattern = Pattern.compile("LSPosed-Bridge:\\s+(.*)");
    @Override
    public String preprocessing(String str) {
        Matcher m = pattern.matcher(str);
        while (m.find()) {
            return m.group(1).trim();
        }
        return str;
    }


    /**
     * 获取应用名称
     * @return
     */
    @Override
    public String getAppName(String str) {
        if(str.startsWith("已加载应用:")) {
            return str.substring(6).trim();
        }
        return null;
    }
}
