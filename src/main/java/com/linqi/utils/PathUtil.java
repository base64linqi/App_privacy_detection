package com.linqi.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtil {
    /**
     * 获取app运行所在的目录
     * @return
     */
    public static String getAppPath() throws Exception {

        String path = PathUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        File f = new File(path);
        String dir = null;
        if (f.isDirectory()){
            dir = f.getPath();
        } else if (f.isFile()){
            dir = f.getParent();
        }
        if (dir == null){
            throw new Exception("没有获取到正确的应用路径，路径中不能包含中文或者没有权限");
        }
        return dir;
    }

    public static String getFridaJsPath(String jsFile) throws Exception {
        String appPath = getAppPath();
        Path path = Paths.get(appPath,  jsFile);
        return path.toString();
    }

    public static String getDbFilePath() throws Exception{
        String appPath = getAppPath();
        Path path = Paths.get(appPath, "db.db");
        return  path.toString();
    }

    public static String getIniFilePath() throws Exception{
        String appPath = getAppPath();
        Path path  = Paths.get(appPath, "config.ini");
        return path.toString();
    }

    public static String getFilePath(String fileName) throws Exception {
        String appPath = getAppPath();
        Path path = Paths.get(appPath, fileName);
        return path.toString();
    }

    public static  String getGeneralPath(String path){
        String result = path.replace("\\", "/");
        return  result;
    }
}
