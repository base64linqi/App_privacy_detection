package com.linqi.handler;

/**
 * 处理命令行的接口
 */
public interface IProcessHandler {
    Process process = null;
    void handle(String str) throws  Exception;

    /**
     * 对字符进行预处理
     * @param str
     * @return
     */
    String preprocessing(String str);

    /**
     * 获取应用名称
     * @return
     */
    String getAppName(String str);
}
