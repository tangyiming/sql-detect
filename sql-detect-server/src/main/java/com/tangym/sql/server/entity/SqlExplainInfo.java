package com.tangym.sql.server.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SqlExplainInfo implements Serializable {
    private static final long serialVersionUID = -62833220359613523L;

    private Integer id;

    /**
     * 关联hash签名表id列表
     */
    private String tbFingerprints;


    /**
     * 服务名称/应用名称
     */
    private String serviceName;

    /**
     * 原始不填参数的sql语句
     */
    private String originalSql;

    /**
     * 原始语句的sql hash值
     */
    private String originalSqlHash;

    /**
     * 最终可以执行的参数化后的sql
     */
    private String parameterizedSql;

    /**
     * sql mybatis id 全路径
     */
    private String sqlId;

    /**
     * host
     */
    private String dbHost;

    /**
     * 端口号
     */
    private String dbPort;

    /**
     * 数据库名
     */
    private String dbName;

    /**
     * 用户名
     */
    private String dbUser;

    /**
     * 密码
     */
    private String dbPwd;

    /**
     * mysql server 版本 5 or 8
     */
    private Integer serverVersion;

    /**
     * explain结果
     */
    private String explainRes;

    /**
     * 执行explain是否失败 0否 1是
     */
    private Integer isFailed;

    /**
     * 是否是慢查sql
     */
    private Boolean isSlow;

    /**
     * 是否已告警
     */
    private Boolean isAlert;

    /**
     * 线上是否出慢查故障
     */
    private Boolean isProdFault;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
