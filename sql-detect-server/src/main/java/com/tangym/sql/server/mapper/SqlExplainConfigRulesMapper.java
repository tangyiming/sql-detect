package com.tangym.sql.server.mapper;


import com.tangym.sql.server.entity.SqlExplainConfigRules;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SqlExplainConfigRulesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SqlExplainConfigRules record);

    int insertSelective(SqlExplainConfigRules record);

    SqlExplainConfigRules selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SqlExplainConfigRules record);

    int updateByPrimaryKey(SqlExplainConfigRules record);

    @Select({"select * from sql_explain_config_rules"})
    List<SqlExplainConfigRules> selectAll();

    @Select({"select * from sql_explain_config_rules where rule_key=#{ruleKey}"})
    SqlExplainConfigRules selectByRuleKey(String ruleKey);
}
