package com.tangym.sql.server.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tangym.sql.server.constant.SqlExplainRules;
import com.tangym.sql.server.dto.response.ExplainResult;
import com.tangym.sql.server.entity.SqlExplainInfo;
import com.tangym.sql.server.entity.SqlExplainTableFingerprint;
import com.tangym.sql.server.mapper.SqlExplainInfoMapper;
import com.tangym.sql.server.mapper.SqlExplainTableFingerprintMapper;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 慢查分析公共方法 核心代码
 */
@Component
@Log
public class SqlExplainTaskCommon {
    @Resource
    private SqlExplainInfoMapper sqlExplainInfoMapper;
    @Resource
    private SqlExplainTableFingerprintMapper sqlExplainTableFingerprintMapper;

    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName("groovy");
    Bindings bindings = engine.createBindings();

    public void pullData(Task task) {
        switch (task) {
            case NORMAL:
                List<SqlExplainInfo> nomalInfos = sqlExplainInfoMapper.selectForExplain();
                if (null != nomalInfos && nomalInfos.size() > 0) {
                    for (SqlExplainInfo info : nomalInfos) {
                        execExplain(info);
                    }
                }
                break;
            case FAIL:
                List<SqlExplainInfo> failedInfos = sqlExplainInfoMapper.selectFailedForExplain();
                if (null != failedInfos && failedInfos.size() > 0) {
                    for (SqlExplainInfo info : failedInfos) {
                        execExplain(info);
                    }
                }
                break;
            default:
                break;
        }

    }

    private void execExplain(SqlExplainInfo info) {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = String.format("jdbc:mysql://%s:%s/%s?characterEncoding=utf8&useSSL=false", info.getDbHost(), info.getDbPort(), info.getDbName());
            connection = DriverManager.getConnection(url, info.getDbUser(), info.getDbPwd());
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("EXPLAIN %s", info.getParameterizedSql()));
            List<ExplainResult> explainResultList = new ArrayList<>();
            while (resultSet.next()) {
                ExplainResult explainResult = new ExplainResult();
                explainResult.setSelectType(resultSet.getString("select_type"));
                explainResult.setTable(resultSet.getString("table"));
                explainResult.setPartitions(resultSet.getString("partitions"));
                explainResult.setType(resultSet.getString("type"));
                explainResult.setPossibleKeys(resultSet.getString("possible_keys"));
                if (resultSet.findColumn("key") > 0 && null == resultSet.getString("key")) {
                    explainResult.setKey("NULL");
                } else {
                    explainResult.setKey(resultSet.getString("key"));
                }
                explainResult.setKeyLen(resultSet.getInt("key_len"));
                explainResult.setRef(resultSet.getString("ref"));
                explainResult.setRows(resultSet.getInt("rows"));
                explainResult.setFiltered(resultSet.getInt("filtered"));
                explainResult.setExtra(resultSet.getString("Extra"));
                explainResultList.add(explainResult);
            }
            info.setExplainRes(JSON.toJSONString(explainResultList));
            Boolean analysis = analysis(info, explainResultList);
            info.setIsSlow(analysis);
            info.setUpdateTime(LocalDateTime.now());
            sqlExplainInfoMapper.updateByPrimaryKey(info);
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            info.setIsFailed(1);
            info.setUpdateTime(LocalDateTime.now());
            sqlExplainInfoMapper.updateByPrimaryKey(info);
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

    @SneakyThrows
    private Boolean analysis(SqlExplainInfo info, List<ExplainResult> explainResultList) {
        for (ExplainResult explainResult : explainResultList) {
            bindings.put("info", info);
            bindings.put("explainResult", explainResult);
            boolean flag = false;
            for (String script : SqlExplainRules.SCRIPTSET) {
                if ((boolean) engine.eval(script, bindings)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                continue;
            }
            /**
             * 慢查询判断规则 {"keySet":["NULL"],"typeSet":["index","ALL"]}
             */
            if (SqlExplainRules.KEYSET.contains(explainResult.getKey()) || SqlExplainRules.TYPESET.contains(explainResult.getType())) {
                SqlExplainInfo secondRecordForCompare = sqlExplainInfoMapper.select2ndRecordCompare(info.getOriginalSql());
                if (null != secondRecordForCompare && !secondRecordForCompare.getIsSlow()) {
                    String tbFingerprints = secondRecordForCompare.getTbFingerprints();
                    LocalDateTime createTime = secondRecordForCompare.getCreateTime();
                    String table = explainResult.getTable();
                    List<Integer> hashIdlist = (List<Integer>) JSONArray.parse(tbFingerprints);
                    for (Integer id : hashIdlist) {
                        SqlExplainTableFingerprint sqlExplainTableFingerprint = sqlExplainTableFingerprintMapper.selectByPrimaryKey(id);
                        LocalDateTime updateTime = sqlExplainTableFingerprint.getUpdateTime();
                        if (sqlExplainTableFingerprint.getTbName().equalsIgnoreCase(table)) {
                            if (updateTime.isAfter(createTime)) {
                                return true;
                            }
                        }
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public enum Task {
        NORMAL, FAIL
    }

}
