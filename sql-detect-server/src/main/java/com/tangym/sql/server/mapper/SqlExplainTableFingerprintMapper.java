package com.tangym.sql.server.mapper;


import com.tangym.sql.server.entity.SqlExplainTableFingerprint;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SqlExplainTableFingerprintMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SqlExplainTableFingerprint record);

    int insertSelective(SqlExplainTableFingerprint record);

    SqlExplainTableFingerprint selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SqlExplainTableFingerprint record);

    int updateByPrimaryKey(SqlExplainTableFingerprint record);

    SqlExplainTableFingerprint selectByDbAndTableName(@Param("dbName") String dbName, @Param("tbName") String tbName);

    @Select({"select * from sql_explain_table_fingerprint where fingerprint is null or update_time > date_sub(current_timestamp(),INTERVAL 1 DAY ) limit 100"})
    List<SqlExplainTableFingerprint> selectForCalcFingerPrints();

    List<SqlExplainTableFingerprint> selectBySelective(SqlExplainTableFingerprint sqlExplainTableFingerprint);
}
