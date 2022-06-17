package com.tangym.sql.server.scheduler;

import com.tangym.sql.server.mapper.SqlExplainInfoMapper;
import lombok.extern.java.Log;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;

/**
 * 数据清理任务
 */
@Log
@DisallowConcurrentExecution
public class SqlExplainClearDataTask extends QuartzJobBean {

    @Resource
    private SqlExplainInfoMapper sqlExplainInfoMapper;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        sqlExplainInfoMapper.deleteBeforeHalfMonth();
    }
}
