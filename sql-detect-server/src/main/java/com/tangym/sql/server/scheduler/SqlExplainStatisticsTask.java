package com.tangym.sql.server.scheduler;

import com.tangym.sql.server.entity.SqlExplainStatistics;
import com.tangym.sql.server.mapper.SqlAppConfigsMapper;
import com.tangym.sql.server.mapper.SqlExplainInfoMapper;
import com.tangym.sql.server.mapper.SqlExplainStatisticsMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 统计信息汇总任务
 */
@Slf4j
@DisallowConcurrentExecution
public class SqlExplainStatisticsTask extends QuartzJobBean {
    @Resource
    private SqlAppConfigsMapper sqlAppConfigsMapper;
    @Resource
    private SqlExplainInfoMapper sqlExplainInfoMapper;
    @Resource
    private SqlExplainStatisticsMapper sqlExplainStatisticsMapper;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        List<String> apps = sqlAppConfigsMapper.selectServices();
        apps.forEach(app -> {
            LocalDateTime now = LocalDateTime.now();
            SqlExplainStatistics statistics = sqlExplainStatisticsMapper.selectByServiceName(app);
            LocalDateTime lastCalcTime = statistics.getCalcTime();
            Integer explainTotal = statistics.getExplainTotal();
            if (null == explainTotal) {
                explainTotal = 0;
            }
            int checkedCount = sqlExplainInfoMapper.countCheckedByService(app, lastCalcTime);
            int checkedTotal = explainTotal + checkedCount;
            String health = "excellent";
            int slowTotal = 0;
            String percent = "0.00";
            if (checkedTotal != 0) {
                Integer slow = statistics.getSlowTotal();
                if (null == slow) {
                    slow = 0;
                }
                int slowCount = sqlExplainInfoMapper.countSlowSqlByService(app, lastCalcTime);
                slowTotal = slow + slowCount;
                DecimalFormat df = new DecimalFormat("0.00");
                percent = df.format(((double) slowTotal / (checkedTotal)) * 100);
                if (Double.parseDouble(percent) <= 0.1) {
                    health = "excellent";
                } else if (Double.parseDouble(percent) <= 1.0) {
                    health = "good";
                } else if (Double.parseDouble(percent) <= 2.0) {
                    health = "average";
                } else {
                    health = "poor";
                }
            }
            // 为了这里的数据准确，数据清理时请保留至少一周的数据
            int increaseSlowCountSevenDays = sqlExplainInfoMapper.countSlowSqlIncreaseSevenDaysByService(app);
            SqlExplainStatistics sqlExplainStatistics = new SqlExplainStatistics();
            sqlExplainStatistics.setServiceName(app);
            sqlExplainStatistics.setHealth(health);
            sqlExplainStatistics.setSlowPercent(percent);
            sqlExplainStatistics.setSlowTotal(slowTotal);
            sqlExplainStatistics.setExplainTotal(checkedTotal);
            sqlExplainStatistics.setLatestSlowInSeven(increaseSlowCountSevenDays);
            sqlExplainStatistics.setCalcTime(now);
            int i = sqlExplainStatisticsMapper.selectCountByServiceName(app);
            if (i == 0) {
                sqlExplainStatisticsMapper.insert(sqlExplainStatistics);
            }
            sqlExplainStatisticsMapper.updateByServiceName(sqlExplainStatistics);
        });

    }
}
