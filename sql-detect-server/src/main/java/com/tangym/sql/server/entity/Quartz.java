package com.tangym.sql.server.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Quartz implements Serializable {
    private static final long serialVersionUID = 4115138464191541188L;
    private Integer id;
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务执行类
     */
    private String jobClass;
    /**
     * 任务运行时间表达式
     */
    private String cronExpression;
    /**
     * 是否在应用启动后运行 1是 0否
     */
    private Boolean runAfterStart;
}
