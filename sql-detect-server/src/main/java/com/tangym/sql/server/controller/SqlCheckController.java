package com.tangym.sql.server.controller;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSON;
import com.tangym.sql.server.dto.response.ApiResponse;
import com.tangym.sql.server.entity.SqlAppConfigs;
import com.tangym.sql.server.entity.SqlExplainInfo;
import com.tangym.sql.server.entity.SqlExplainTableFingerprint;
import com.tangym.sql.server.mapper.SqlAppConfigsMapper;
import com.tangym.sql.server.mapper.SqlExplainInfoMapper;
import com.tangym.sql.server.mapper.SqlExplainTableFingerprintMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/sql")
public class SqlCheckController {
    @Resource
    private SqlExplainInfoMapper sqlExplainInfoMapper;

    @Resource
    private SqlExplainTableFingerprintMapper sqlExplainTableFingerprintMapper;

    @Resource
    private SqlAppConfigsMapper sqlAppConfigsMapper;

    /**
     * sql上报
     *
     * @param explainInfo
     * @return
     */
    @PostMapping("/add")
    public ApiResponse<?> addSql(@RequestBody SqlExplainInfo explainInfo) {
        try {
            // parameterizedSql为空的话也没有存储的必要
            if (ObjectUtils.isEmpty(explainInfo.getParameterizedSql())) return ApiResponse.succResponse("sql为空，跳过保存");

            // 如果为本应用开启了慢sql检测差距，需要过滤掉sql检测服务相关表的sql语句，否则会陷入无限循环检测
            if ((explainInfo.getParameterizedSql().contains("sql_explain_info")
                    || explainInfo.getParameterizedSql().contains("sql_explain_table_fingerprint")
                    || explainInfo.getParameterizedSql().contains("sql_process_log")
                    || explainInfo.getParameterizedSql().contains("quartz")
                    || explainInfo.getParameterizedSql().contains("sql_explain_config_rules")
                    || explainInfo.getParameterizedSql().contains("sql_explain_config_rules_script")
                    || explainInfo.getParameterizedSql().contains("sql_app_configs")
                    || explainInfo.getParameterizedSql().contains("sql_explain_stars")
                    || explainInfo.getParameterizedSql().contains("sql_explain_statistics"))
                    && explainInfo.getServiceName().equals("sql-detect-server")) {
                return ApiResponse.succResponse("sql分析后台服务查询语句，过滤退出保存");
            }

            // DB相关的操作逻辑都要放上面这段代码下面
            // 查看开关进行上报控制
            SqlAppConfigs appConfig = sqlAppConfigsMapper.selectByService(explainInfo.getServiceName());
            if (!appConfig.getExplainSwitch()) {
                //"SQL上报开关关闭，不上报"
                return ApiResponse.succResponse();
            }

            // pageHelper 分页查询语句过滤 SELECT count(0)
            if (explainInfo.getOriginalSql().contains("SELECT count(0) FROM")) {
                //"PageHelper分页查询语句，过滤退出保存"
                return ApiResponse.succResponse();
            }

            // 计算original sql hash
            String sqlHash = DigestUtils.md5Hex(explainInfo.getOriginalSql()).toUpperCase();


            // 查询24小时内有无相同的OriginalSql，如果有，就不存了
            // 未来考虑时间段可以根据应用可配置化
            int count = sqlExplainInfoMapper.filterByOriginalSqlHash(sqlHash);
            if (count > 0) {
                //"本应用24时内已存在相同sql记录，跳过保存与分析"
                return ApiResponse.succResponse();
            }
            explainInfo.setOriginalSqlHash(sqlHash);

            // 解析sql包含表名
            List<SQLStatement> statementList = SQLUtils.parseStatements(explainInfo.getParameterizedSql(), JdbcConstants.MYSQL);
            SQLStatement sqlStatement = statementList.get(0);
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            sqlStatement.accept(visitor);
            Set<TableStat.Name> tableNames = visitor.getTables().keySet();
            LocalDateTime now = LocalDateTime.now();
            List<Integer> fingerprintsTableIds = new ArrayList<>();
            tableNames.forEach(tb -> {
                // 处理分表 xxx_sss_1 xxx_sss_2 这种，最后统一存 xxx_sss
                String tbName = tb.getName();
                // 如果表名被``包裹，还要去除``
                if (tbName.startsWith("`") || tbName.endsWith("`")) tbName = tbName.replace("`", "");

                Pattern pattern = Pattern.compile("_\\d+$");
                Matcher matcher = pattern.matcher(tbName);
                if (matcher.find()) {
                    tbName = tbName.replace(matcher.group(), "");
                }
                SqlExplainTableFingerprint res = sqlExplainTableFingerprintMapper.selectByDbAndTableName(explainInfo.getDbName(), tbName);
                if (null == res) {
                    SqlExplainTableFingerprint sqlExplainTableFingerprint = new SqlExplainTableFingerprint();
                    sqlExplainTableFingerprint.setDbHost(explainInfo.getDbHost());
                    sqlExplainTableFingerprint.setDbPort(explainInfo.getDbPort());
                    sqlExplainTableFingerprint.setDbUser(explainInfo.getDbUser());
                    sqlExplainTableFingerprint.setDbPwd(explainInfo.getDbPwd());
                    sqlExplainTableFingerprint.setDbName(explainInfo.getDbName());
                    sqlExplainTableFingerprint.setTbName(tbName);
                    sqlExplainTableFingerprint.setCreateTime(now);
                    sqlExplainTableFingerprint.setUpdateTime(now);
                    sqlExplainTableFingerprintMapper.insert(sqlExplainTableFingerprint);
                    fingerprintsTableIds.add(sqlExplainTableFingerprint.getId());
                } else {
                    fingerprintsTableIds.add(res.getId());
                    // 避免连接信息过期 判断并更新
                    if (!res.getDbHost().equals(explainInfo.getDbHost()) || !res.getDbPort().equals(explainInfo.getDbPort()) || !res.getDbUser().equals(explainInfo.getDbUser()) || !res.getDbPwd().equals(explainInfo.getDbPwd())) {
                        res.setDbHost(explainInfo.getDbHost());
                        res.setDbPort(explainInfo.getDbPort());
                        res.setDbUser(explainInfo.getDbUser());
                        res.setDbPwd(explainInfo.getDbPwd());
                        sqlExplainTableFingerprintMapper.updateByPrimaryKeySelective(res);
                    }
                }
            });
            explainInfo.setCreateTime(now);
            explainInfo.setUpdateTime(now);
            explainInfo.setTbFingerprints(JSON.toJSONString(fingerprintsTableIds));
            int res = sqlExplainInfoMapper.insert(explainInfo);
            if (res > 0) {
                return ApiResponse.succResponse();
            }
            // "sql信息插入失败"
            return ApiResponse.failResponse("0");
        } catch (Exception e) {
            log.warn("addSql error:" + e.getMessage());
        }
        return ApiResponse.failResponse("0");
    }
}
