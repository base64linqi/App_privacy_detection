package com.linqi.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;

@Getter
@Setter

public class ProcessInfo {
    private Process process;
    private BufferedReader reader;
}
