package com.linqi.utils;

import com.linqi.entity.ProcessInfo;
import com.linqi.handler.IProcessHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CommandUtil {

    /**
     * 执行command命令，返回结果
     *
     * @param commands       需要执行的命令行字符
     * @param noReturnResult 是否需要返回结果
     * @param startReturnRow 从哪行开始返回，默认调用应该从0行，如果使用了windows批处理，从第2行开始返回
     * @return 返回字符串集合
     * @throws IOException
     */
    public static List<String> executeByProcessBuilder(List<String> commands,
                                                       Boolean noReturnResult, int startReturnRow,
                                                       IProcessHandler handler, ProcessInfo processInfo, String charset) throws Exception{
        Runtime runtime = Runtime.getRuntime();
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commands);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        if (processInfo != null) {
            processInfo.setProcess(process);
        }
        if (noReturnResult) {
            return new ArrayList<>();
        }
        BufferedReader reader = null;
        List<String> result = new ArrayList<>();
        try {
            int index = 0;
            String line = null;
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName(charset)));
            if (processInfo != null) {
                processInfo.setReader(reader);
            }
            while ((line = reader.readLine()) != null) {
                System.out.println("line============================" + line    );
                // 只从指定的索引行开始
                if (index >= startReturnRow) {
                    result.add(line);
                    if (handler != null){
                        handler.handle(line);
                    }
                }
                index++;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            process.destroy();

            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ex) {

                }
            }
        }
        return result;
    }
}
