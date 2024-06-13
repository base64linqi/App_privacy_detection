package com.linqi.handler;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface IKernel32 extends Library {
    public static IKernel32 INSTANCE = (IKernel32) Native.loadLibrary("kernel32", IKernel32.class);
    public Long GetProcessId(Long hProcess);
}
