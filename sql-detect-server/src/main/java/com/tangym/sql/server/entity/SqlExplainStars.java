package com.tangym.sql.server.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SqlExplainStars implements Serializable {
    private static final long serialVersionUID = -6237953424812137538L;
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 工号
     */
    private String jobNumber;
    /**
     * 关注应用
     */
    private String serviceName;
}
