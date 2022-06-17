package com.tangym.sql.server.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SqlExplainStatistics implements Serializable {
    private static final long serialVersionUID = -3360684907406927226L;
    private Integer id;
    /**
     * 应用名
     */
    private String serviceName;
    /**
     * 健康度
     */
    private String health;
    /**
     * 慢查占比
     */
    private String slowPercent;
    /**
     * 近七日新增慢查数
     */
    private Integer latestSlowInSeven;
    /**
     * 慢查总数
     */
    private Integer slowTotal;
    /**
     * 分析总数
     */
    private Integer explainTotal;
    /**
     * 汇总时间点，以后从此时间点进行数据统计，做累加
     */
    private LocalDateTime calcTime;
}
