package com.tangym.sql.server.mapper;


import com.tangym.sql.server.entity.SqlProcessLog;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SqlProcessLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SqlProcessLog record);

    int insertSelective(SqlProcessLog record);

    SqlProcessLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SqlProcessLog record);

    int updateByPrimaryKey(SqlProcessLog record);

    @Select({"select * from sql_process_log where explain_info_id = #{infoId} order by id desc limit 5"})
    List<SqlProcessLog> selectByInfoId(Integer infoId);
}
