package com.tangym.sql.server.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
public class SqlExplainTableFingerprint implements Serializable {
    private static final long serialVersionUID = -6585305026361228847L;
    private Integer id;
    /**
     * 数据库名
     */
    private String dbName;
    /**
     * host
     */
    private String dbHost;
    /**
     * port
     */
    private String dbPort;
    /**
     * 用户名
     */
    private String dbUser;
    /**
     * 密码
     */
    private String dbPwd;
    /**
     * 表名
     */
    private String tbName;
    /**
     * md5 hash值
     */
    private String fingerprint;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
