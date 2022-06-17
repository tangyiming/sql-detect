package com.tangym.sql.plugin.v5.util;

import com.tangym.sql.plugin.v5.model.SqlExplainInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author :       yiming.tang
 */
@Slf4j
public class SqlPostTask {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public static void start() {
        SqlPostTask task = new SqlPostTask();
        task.scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                if (!DataPool.linkedBlockingQueue.isEmpty()) {
                    List<SqlExplainInfo> infoList = new ArrayList<>();
                    DataPool.linkedBlockingQueue.drainTo(infoList);
                    if (infoList.size() > 0) {
                        infoList.forEach(PostUtil::process);
                    }
                }
            } catch (Exception e) {
                log.info("sql post failed");
            }
        }, 60, 1, TimeUnit.SECONDS);

    }
}
