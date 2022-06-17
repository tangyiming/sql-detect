package com.tangym.sql.server.mapper;

import com.tangym.sql.server.entity.SqlAppConfigs;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SqlAppConfigsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SqlAppConfigs record);

    int insertSelective(SqlAppConfigs record);

    SqlAppConfigs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SqlAppConfigs record);

    int updateByPrimaryKey(SqlAppConfigs record);

    @Select({"select * from sql_app_configs where service = #{serviceName}"})
    SqlAppConfigs selectByService(String serviceName);

    @Select({"select * from sql_app_configs"})
    List<SqlAppConfigs> selectAll();

    List<SqlAppConfigs> selectBySelective(SqlAppConfigs record);

    @Select({"select distinct service from sql_app_configs"})
    List<String> selectServices();

    @Select({"select count(id) from sql_app_configs"})
    int selectCount();
}
