package com.tangym.sql.server.mapper;


import com.tangym.sql.server.entity.SqlExplainStatistics;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SqlExplainStatisticsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SqlExplainStatistics record);

    int insertSelective(SqlExplainStatistics record);

    SqlExplainStatistics selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SqlExplainStatistics record);

    int updateByPrimaryKey(SqlExplainStatistics record);

    @Select({"select count(id) from sql_explain_statistics where service_name = #{serviceName}"})
    int selectCountByServiceName(String serviceName);

    int updateByServiceName(SqlExplainStatistics sqlExplainStatistics);

    List<SqlExplainStatistics> selectByServiceNames(@Param("apps") List<String> apps);

    @Select({"select * from sql_explain_statistics"})
    List<SqlExplainStatistics> selectAll();

    @Select({"select * from sql_explain_statistics where service_name = #{serviceName}"})
    SqlExplainStatistics selectByServiceName(String serviceName);
}
