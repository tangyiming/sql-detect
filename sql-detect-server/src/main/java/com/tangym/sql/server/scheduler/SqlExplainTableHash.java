package com.tangym.sql.server.scheduler;

import com.tangym.sql.server.entity.SqlExplainTableFingerprint;
import com.tangym.sql.server.mapper.SqlExplainTableFingerprintMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 表Hash计算任务
 */
@Slf4j
@DisallowConcurrentExecution
public class SqlExplainTableHash extends QuartzJobBean {

    @Resource
    private SqlExplainTableFingerprintMapper sqlExplainTableFingerprintMapper;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        List<SqlExplainTableFingerprint> sqlExplainTableFingerprints = sqlExplainTableFingerprintMapper.selectForCalcFingerPrints();

        if (sqlExplainTableFingerprints.size() > 0) {
            sqlExplainTableFingerprints.forEach(sqlExplainTableFingerprint -> execDescribe(sqlExplainTableFingerprint));
        }

    }

    private void execDescribe(SqlExplainTableFingerprint info) {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = String.format("jdbc:mysql://%s:%s/%s?characterEncoding=utf8&useSSL=false", info.getDbHost(), info.getDbPort(), info.getDbName());
            connection = DriverManager.getConnection(url, info.getDbUser(), info.getDbPwd());
            statement = connection.createStatement();
            ResultSet resultSet = null;
            try {
                resultSet = statement.executeQuery(String.format("SHOW CREATE TABLE %s", info.getTbName()));
            } catch (Exception e) {
                // 如果发生异常，可能是因为是分表，DB中只存了去除_数字的部分，所有尝试再试一次获取分表_0的
                resultSet = statement.executeQuery(String.format("SHOW CREATE TABLE %s_0", info.getTbName()));
            }
            String ddl = null;
            while (resultSet.next()) {
                ddl = resultSet.getString("Create Table");
            }
            if (null != ddl) {
                String md5 = getMD5(ddl);
                if (!md5.equals(info.getFingerprint())) {
                    info.setFingerprint(md5);
                    info.setUpdateTime(LocalDateTime.now());
                    sqlExplainTableFingerprintMapper.updateByPrimaryKeySelective(info);
                }
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            log.warn("获取表结构hash签名异常");
            log.warn(info.toString());
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException se2) {
                statement = null;
            }
            try {
                if (connection != null) connection.close();
            } catch (SQLException se) {
                connection = null;

            }
        }
    }

    private String getMD5(String str) {
        return DigestUtils.md5Hex(str).toUpperCase();
    }
}
