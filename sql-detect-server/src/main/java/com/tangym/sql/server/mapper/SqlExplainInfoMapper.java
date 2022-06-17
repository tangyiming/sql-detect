package com.tangym.sql.server.mapper;

import com.tangym.sql.server.dto.request.SqlDetailListPageRequest;
import com.tangym.sql.server.entity.SqlExplainInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface SqlExplainInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SqlExplainInfo record);

    int insertSelective(SqlExplainInfo record);

    SqlExplainInfo selectByPrimaryKey(Integer id);

    @Select({"select * from sql_explain_info where explain_res is null limit 100"})
    List<SqlExplainInfo> selectForExplain();

    @Select({"select * from sql_explain_info where is_failed=1 and create_time > date_sub(current_timestamp(),INTERVAL 1 DAY ) limit 100"})
    List<SqlExplainInfo> selectFailedForExplain();

    @Select({"select * from sql_explain_info where is_slow=true and is_alert is null"})
    List<SqlExplainInfo> selectFailedForAlert();

    int updateByPrimaryKeySelective(SqlExplainInfo record);

    int updateByPrimaryKey(SqlExplainInfo record);

    @Select({"select count(*) from sql_explain_info where original_sql_hash = #{hash} and create_time > date_sub(current_timestamp(),INTERVAL 1 DAY )"})
    int filterByOriginalSqlHash(String hash);

    @Select({"select * from sql_explain_info where original_sql = #{originalSql} order by id desc limit 2,1 "})
    SqlExplainInfo select2ndRecordCompare(String originalSql);

    int countCheckedByService(@Param("app") String app, @Param("lastCalcTime") LocalDateTime lastCalcTime);

    int countSlowSqlByService(@Param("app") String app, @Param("lastCalcTime") LocalDateTime lastCalcTime);

    @Select({"select count(id) from sql_explain_info where service_name= #{app} and (is_slow=true or remark is not null) and create_time > date_sub(current_timestamp(),INTERVAL 7 DAY )"})
    int countSlowSqlIncreaseSevenDaysByService(String app);

    List<SqlExplainInfo> queryByConditions(SqlDetailListPageRequest request);

    @Delete({"delete from sql_explain_info where create_time <= DATE (DATE_SUB(NOW(),INTERVAL 15 DAY))"})
    int deleteBeforeHalfMonth();
}
