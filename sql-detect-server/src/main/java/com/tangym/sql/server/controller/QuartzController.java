package com.tangym.sql.server.controller;

import com.tangym.sql.server.dto.response.ApiResponse;
import com.tangym.sql.server.dto.response.QuartzResponse;
import com.tangym.sql.server.entity.Quartz;
import com.tangym.sql.server.mapper.QuartzMapper;
import com.tangym.sql.server.util.QuartzUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/quartz")
public class QuartzController {

    @Resource
    private Scheduler scheduler;
    @Resource
    private QuartzMapper quartzMapper;


    @PostMapping("/create")
    public ApiResponse<?> createJob(@RequestBody Quartz quartz) {
        try {
            Quartz q = quartzMapper.selectByKey(quartz.getJobClass());
            if (ObjectUtils.isEmpty(q)) {
                quartz.setRunAfterStart(false);
                int i = quartzMapper.insert(quartz);
                if (i > 0) {
                    QuartzUtils.createScheduleJob(scheduler, quartz);
                }
            }
            QuartzUtils.createScheduleJob(scheduler, quartz);
        } catch (Exception e) {
            return ApiResponse.failResponse(e.getMessage());
        }
        return ApiResponse.succResponse();
    }

    @PostMapping("/pause")
    public ApiResponse<?> pauseJob(@RequestBody Quartz quartz) {
        try {
            QuartzUtils.pauseScheduleJob(scheduler, quartz.getJobName());
        } catch (Exception e) {
            return ApiResponse.failResponse(e.getMessage());
        }
        return ApiResponse.succResponse("暂停成功");
    }

    @PostMapping("/runonce")
    public ApiResponse<?> runOnce(@RequestBody Quartz quartz) {
        try {
            QuartzUtils.runOnce(scheduler, quartz.getJobName());
        } catch (Exception e) {
            return ApiResponse.failResponse(String.format("运行一次失败：%s", e.getMessage()));
        }
        return ApiResponse.succResponse("执行成功");
    }

    @PostMapping("/resume")
    public ApiResponse<?> resume(@RequestBody Quartz quartz) {
        try {
            QuartzUtils.resumeScheduleJob(scheduler, quartz.getJobName());
        } catch (Exception e) {
            return ApiResponse.failResponse(e.getMessage());
        }
        return ApiResponse.succResponse("成功恢复运行");
    }

    @PostMapping("/update")
    public ApiResponse<?> update(@RequestBody Quartz quartz) {
        try {
            int i = quartzMapper.updateByPrimaryKey(quartz);
            if (i > 0) {
                QuartzUtils.updateScheduleJob(scheduler, quartz);
            }
        } catch (Exception e) {
            return ApiResponse.failResponse(e.getMessage());
        }
        return ApiResponse.succResponse("更新成功");
    }

    @PostMapping("/delete")
    public ApiResponse<?> delete(@RequestBody Quartz quartz) {
        try {
            int i = quartzMapper.deleteByPrimaryKey(quartz.getId());
            if (i > 0) {
                QuartzUtils.deleteScheduleJob(scheduler, quartz.getJobName());
            }
        } catch (Exception e) {
            return ApiResponse.failResponse(e.getMessage());
        }
        return ApiResponse.succResponse("完成删除");
    }

    @GetMapping("/list")
    public ApiResponse<?> list() {
        List<Quartz> list = quartzMapper.selectAll();
        List<QuartzResponse> res = new ArrayList<>();
        list.forEach(e -> {
            Trigger.TriggerState state = QuartzUtils.getStatusOfScheduleJob(scheduler, e);
            QuartzResponse quartzResponse = QuartzResponse.builder().id(e.getId()).jobName(e.getJobName()).jobClass(e.getJobClass()).cronExpression(e.getCronExpression()).status(state).runAfterStart(e.getRunAfterStart()).build();
            res.add(quartzResponse);
        });
        return ApiResponse.succResponse(res);
    }

}
