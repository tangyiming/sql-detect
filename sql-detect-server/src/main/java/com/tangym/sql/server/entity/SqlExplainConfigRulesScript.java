package com.tangym.sql.server.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SqlExplainConfigRulesScript implements Serializable {
    private static final long serialVersionUID = 9028680727012191304L;
    private Integer id;
    /**
     * 规则脚本
     */
    private String script;
    /**
     * 规则备注信息
     */
    private String remark;
}
