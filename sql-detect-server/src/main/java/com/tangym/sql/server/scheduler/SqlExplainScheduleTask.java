package com.tangym.sql.server.scheduler;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;


/**
 * 获取新捕获的sql进行分析任务
 */
@DisallowConcurrentExecution
public class SqlExplainScheduleTask extends QuartzJobBean {
    @Resource
    private SqlExplainTaskCommon sqlExplainTaskCommon;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        sqlExplainTaskCommon.pullData(SqlExplainTaskCommon.Task.NORMAL);
    }
}
