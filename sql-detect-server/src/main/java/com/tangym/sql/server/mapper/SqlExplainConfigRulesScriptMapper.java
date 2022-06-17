package com.tangym.sql.server.mapper;

import com.tangym.sql.server.entity.SqlExplainConfigRulesScript;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SqlExplainConfigRulesScriptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SqlExplainConfigRulesScript record);

    int insertSelective(SqlExplainConfigRulesScript record);

    SqlExplainConfigRulesScript selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SqlExplainConfigRulesScript record);

    int updateByPrimaryKey(SqlExplainConfigRulesScript record);

    @Select({"select script from sql_explain_config_rules_script"})
    List<String> selectAllScripts();

    @Select({"select * from sql_explain_config_rules_script"})
    List<SqlExplainConfigRulesScript> selectAll();
}
