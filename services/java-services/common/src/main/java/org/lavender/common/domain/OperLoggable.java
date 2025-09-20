package org.lavender.common.domain;

import lombok.Data;
import org.lavender.common.enums.OperatorType;

import java.util.Map;

@Data
public class OperLoggable {
    private String title;
    private boolean success;
    private String exception;
    private int operatorType;
    private String ip;
    private String url;
    private String httpMethod;
    private Map<String,String[]> param;
}
