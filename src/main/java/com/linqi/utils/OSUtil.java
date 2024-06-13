package com.linqi.utils;

import com.linqi.handler.IKernel32;

import java.io.IOException;
import java.lang.reflect.Field;

public class OSUtil {
    public static void killProcess(Process process) {
        process.destroyForcibly();
    }

    public static  void killProcessByPid(Process process){
        Long pid = getPid(process);
        killProcess(pid);
    }

    public static void killProcess(Long pid) {

        try {
            String cmd = "taskkill /F /T /pid " + pid;
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Long getPid(Process process) {
        Long pid = null;
        Field field = null;
        try {
            field = process.getClass().getDeclaredField("handle");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        field.setAccessible(true);
        try {
            pid = IKernel32.INSTANCE.GetProcessId((Long) field.get(process));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            return pid;
        }

    }
}
