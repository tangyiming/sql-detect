package com.tangym.sql.plugin.v5;

import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.PreparedStatement;
import com.tangym.sql.plugin.v5.model.SqlExplainInfo;
import com.tangym.sql.plugin.v5.util.DataPool;
import io.manbang.gravity.plugin.Advice;
import io.manbang.gravity.plugin.ExecuteContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Properties;

import static com.tangym.sql.plugin.v5.util.DataPool.linkedBlockingQueue;
import static com.tangym.sql.plugin.v5.util.SqlFormatUtil.formatSql;

/**
 * @author :       yiming.tang
 */
@Slf4j
public class PreparedStatement5xAdvice implements Advice {
    @SneakyThrows
    @Override
    public void enterMethod(ExecuteContext context) {
        Object target = context.getTarget();
        if (target instanceof PreparedStatement) {
            String sqlId = DataPool.sqlIdThreadLocal.get();
            PreparedStatement preparedStatement = (PreparedStatement) target;
            String ORIGINALSQL = formatSql(preparedStatement.getPreparedSql());
            String SQL = formatSql(preparedStatement.asSql());
            String start = ORIGINALSQL.split(" ")[0];
            if (start.startsWith("/*")) {
                start = start.split("\\/")[2];
                ORIGINALSQL = ORIGINALSQL.split("\\/")[2];
                SQL = SQL.split("\\/")[2];
            }
            if (!DataPool.captureRules.contains(start.toLowerCase())) return;
            MySQLConnection connection = context.getFieldValue("connection");
            Optional<Properties> optional = Optional.ofNullable(connection).map(MySQLConnection::getProperties);
            if (!optional.isPresent()) {
                return;
            }
            Properties props = optional.get();
            SqlExplainInfo sqlExplainInfo = SqlExplainInfo.builder().dbHost(String.valueOf(props.get("HOST"))).dbPort(String.valueOf(props.get("PORT"))).dbName(String.valueOf(props.get("DBNAME"))).dbUser(String.valueOf(props.get("user"))).dbPwd(String.valueOf(props.get("password"))).originalSql(ORIGINALSQL).parameterizedSql(SQL).sqlId(sqlId).serverVersion(5).serviceName(DataPool.appName).build();
            if (!linkedBlockingQueue.offer(sqlExplainInfo)) log.info("sqls queue is full");
        }
    }
}
