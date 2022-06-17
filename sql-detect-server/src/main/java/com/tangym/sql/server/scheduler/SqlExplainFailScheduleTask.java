package com.tangym.sql.server.scheduler;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;

/**
 * 获取分析失败的sql再次进行分析任务
 */
@DisallowConcurrentExecution
public class SqlExplainFailScheduleTask extends QuartzJobBean {
    @Resource
    private SqlExplainTaskCommon sqlExplainTaskCommon;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        sqlExplainTaskCommon.pullData(SqlExplainTaskCommon.Task.FAIL);
    }
}
