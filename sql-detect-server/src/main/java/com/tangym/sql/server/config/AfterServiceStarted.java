package com.tangym.sql.server.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tangym.sql.server.constant.SqlExplainRules;
import com.tangym.sql.server.entity.Quartz;
import com.tangym.sql.server.entity.SqlExplainConfigRules;
import com.tangym.sql.server.mapper.QuartzMapper;
import com.tangym.sql.server.mapper.SqlExplainConfigRulesMapper;
import com.tangym.sql.server.mapper.SqlExplainConfigRulesScriptMapper;
import com.tangym.sql.server.util.QuartzUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
public class AfterServiceStarted implements ApplicationRunner {

    @Resource
    private QuartzMapper quartzMapper;
    @Resource
    private Scheduler scheduler;
    @Resource
    private SqlExplainConfigRulesMapper sqlExplainConfigRulesMapper;
    @Resource
    private SqlExplainConfigRulesScriptMapper sqlExplainConfigRulesScriptMapper;

    public static void SyncSqlExplainRules(SqlExplainConfigRules sqlExplainConfigRules, List<String> allScripts) {
        if (sqlExplainConfigRules != null) {
            String detail = sqlExplainConfigRules.getRuleDetail();
            if (null != detail) {
                JSONObject jsonObject = JSON.parseObject(detail);
                Object keySet = jsonObject.get("keySet");
                if (null != keySet) {
                    JSONArray ksa = JSONArray.parseArray(keySet.toString());
                    if (ksa.size() > 0) SqlExplainRules.KEYSET = new HashSet<>(ksa.toJavaList(String.class));
                }
                Object typeSet = jsonObject.get("typeSet");
                if (null != typeSet) {
                    JSONArray tsa = JSONArray.parseArray(typeSet.toString());
                    if (tsa.size() > 0) SqlExplainRules.TYPESET = new HashSet<>(tsa.toJavaList(String.class));
                }
            }
        }

        if (null != allScripts && allScripts.size() > 0) {
            SqlExplainRules.SCRIPTSET = new HashSet<>(allScripts);
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        SqlExplainConfigRules sqlExplainConfigRules = sqlExplainConfigRulesMapper.selectByRuleKey("criteria");
        List<String> allScripts = sqlExplainConfigRulesScriptMapper.selectAllScripts();
        if (null != sqlExplainConfigRules) SyncSqlExplainRules(sqlExplainConfigRules, allScripts);

        // todo 本地调试是否开启定时任务，以部署环境系统名为判断
        if ("Linux".equals(System.getProperty("os.name"))) {
            List<Quartz> quartzs = quartzMapper.selectAll();
            if (quartzs.size() > 0) {
                quartzs.forEach(quartz -> {
                    if (quartz.getRunAfterStart()) {
                        try {
                            QuartzUtils.createScheduleJob(scheduler, quartz);
                        } catch (SchedulerException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
