package com.tangym.sql.server.dto.response;

import lombok.Builder;
import lombok.Data;
import org.quartz.Trigger;

import java.io.Serializable;

@Builder
@Data
public class QuartzResponse implements Serializable {
    private static final long serialVersionUID = -3551571484983058969L;

    private Integer id;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务执行类
     */
    private String jobClass;

    /**
     * 任务运行时间表达式
     */
    private String cronExpression;

    /**
     * 是否在应用启动后运行 1是 0否
     */
    private Boolean runAfterStart;

    /**
     * 任务运行状态
     */
    private Trigger.TriggerState status;
}
