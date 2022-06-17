package com.tangym.sql.server.mapper;


import com.tangym.sql.server.entity.SqlExplainStars;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SqlExplainStarsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SqlExplainStars record);

    int insertSelective(SqlExplainStars record);

    SqlExplainStars selectByPrimaryKey(Integer id);

    @Select({"select service_name from sql_explain_stars where job_number = #{ jobNumber }"})
    List<String> selectByJobNum(String jobNumber);

    @Select({"select count(id) from sql_explain_stars where job_number = #{ jobNumber }"})
    int selectCountByJobNum(String jobNumber);

    int updateByPrimaryKeySelective(SqlExplainStars record);

    int updateByPrimaryKey(SqlExplainStars record);

    @Delete({"delete from sql_explain_stars where job_number=#{jobNumber} and service_name=#{serviceName}"})
    int deleteByStarInfo(SqlExplainStars sqlExplainStars);

    @Select({"select id from sql_explain_stars where job_number=#{jobNumber} and service_name=#{serviceName}"})
    Integer selectByStarInfo(SqlExplainStars sqlExplainStars);
}
