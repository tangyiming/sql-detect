package com.tangym.sql.server.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SqlExplainConfigRules implements Serializable {
    private static final long serialVersionUID = -6631333819177532067L;
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 规则内容
     */
    private String ruleDetail;
    /**
     * 唯一键，识别规则所属
     */
    private String ruleKey;
}
