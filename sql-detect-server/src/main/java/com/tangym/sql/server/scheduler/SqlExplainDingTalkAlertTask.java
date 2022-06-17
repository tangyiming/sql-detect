package com.tangym.sql.server.scheduler;

import com.alibaba.fastjson.JSONArray;
import com.github.jaemon.dinger.DingerSender;
import com.github.jaemon.dinger.core.DingerHelper;
import com.github.jaemon.dinger.core.entity.DingerRequest;
import com.github.jaemon.dinger.core.entity.enums.DingerType;
import com.github.jaemon.dinger.core.entity.enums.MessageSubType;
import com.tangym.sql.server.entity.SqlAppConfigs;
import com.tangym.sql.server.entity.SqlExplainInfo;
import com.tangym.sql.server.mapper.SqlAppConfigsMapper;
import com.tangym.sql.server.mapper.SqlExplainInfoMapper;
import lombok.extern.java.Log;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 慢查告警任务
 */
@Log
@DisallowConcurrentExecution
public class SqlExplainDingTalkAlertTask extends QuartzJobBean {
    @Resource
    private SqlExplainInfoMapper sqlExplainInfoMapper;
    @Resource
    private SqlAppConfigsMapper sqlAppConfigsMapper;
    @Resource
    private DingerSender dingerSender;

    @Value("${spring.dinger.dingers.dingtalk.tokenId}")
    private String defaultDingToken;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        List<SqlExplainInfo> infos = sqlExplainInfoMapper.selectFailedForAlert();
        if (infos.size() == 0) {
            return;
        }
        Map<String, List<SqlExplainInfo>> stringListMap = infos.stream().collect(Collectors.groupingBy(SqlExplainInfo::getServiceName));
        stringListMap.forEach((k, v) -> {
            int count = v.size();
            SqlAppConfigs appConfigs = sqlAppConfigsMapper.selectByService(k);
            List<Map<String, String>> listObjectFir = (List<Map<String, String>>) JSONArray.parse(appConfigs.getUsers());
            List<String> userList = new ArrayList<>();
            String dingToken = appConfigs.getDingToken();
            listObjectFir.forEach(map -> {
                String tel = map.get("tel");
                userList.add(tel);
            });
            log.info(String.format("alert:%s", k));
            dingAlert(k, count, userList, dingToken);

            v.forEach(sqlinfo -> {
                sqlinfo.setIsAlert(true);
                sqlExplainInfoMapper.updateByPrimaryKey(sqlinfo);
            });
        });
    }

    private void dingAlert(String serviceName, int count, List<String> userList, String dingToken) {
        String template = "**应用：** <font color=#FF0000 size=4 face=\"黑体\"> %s </font>   \n   **负责人：**  %s   \n  **慢SQL：**  %s条  \n  [前往查看处理](https://firefly.amh-group.com/#/sqlananysis/sqls?name=%s)";
        StringBuilder users = new StringBuilder();
        userList.forEach(usr -> users.append(String.format("  @%s  ", usr)));
        String content = String.format(template, serviceName, users, count, serviceName);
        if (StringUtils.isEmpty(dingToken)) {
            DingerHelper.assignDinger(DingerType.DINGTALK, defaultDingToken, true);
        } else {
            DingerHelper.assignDinger(DingerType.DINGTALK, dingToken, true);
        }
        dingerSender.send(MessageSubType.MARKDOWN, DingerRequest.request(content, serviceName, userList));
    }
}
