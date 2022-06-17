package com.tangym.sql.server.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SqlAppConfigs implements Serializable {
    private static final long serialVersionUID = -2089623614139774652L;
    private Integer id;
    /**
     * 应用名
     */
    private String service;
    /**
     * 应用级别explain检查开关 1开启 0关闭
     */
    private Boolean explainSwitch;
    /**
     * 应用负责人列表，通知到SQL分析群里哪些人
     */
    private String users;
    /**
     * 钉群机器人token
     */
    private String dingToken;
}
