package com.tangym.sql.server.controller;

import com.tangym.sql.server.dto.response.ApiResponse;
import com.tangym.sql.server.entity.SqlExplainInfo;
import com.tangym.sql.server.entity.SqlProcessLog;
import com.tangym.sql.server.mapper.SqlExplainInfoMapper;
import com.tangym.sql.server.mapper.SqlProcessLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sql/process/log")
public class SqlProcessLogController {
    @Resource
    private SqlProcessLogMapper sqlProcessLogMapper;
    @Resource
    private SqlExplainInfoMapper sqlExplainInfoMapper;

    @PostMapping("/add")
    public ApiResponse<?> add(@RequestBody SqlProcessLog sqlProcessLog) {
        if (StringUtils.isEmpty(sqlProcessLog.getRemark())) {
            return ApiResponse.failResponse("remark处理说明参数必填");
        }
        SqlExplainInfo sqlExplainInfo = new SqlExplainInfo();
        sqlExplainInfo.setId(sqlProcessLog.getExplainInfoId());
        if (sqlProcessLog.getType().equals("ignore") || sqlProcessLog.getType().equals("slow-optimized")) {
            sqlExplainInfo.setIsSlow(false);
        }
        if (sqlProcessLog.getType().equals("slow-todo")) {
            sqlExplainInfo.setIsSlow(true);
            log.info("add setIsSlow true:{}", sqlExplainInfo.getId());
        }
        sqlExplainInfo.setRemark(sqlProcessLog.getRemark());
        sqlExplainInfo.setUpdateTime(LocalDateTime.now());
        int res = sqlExplainInfoMapper.updateByPrimaryKeySelective(sqlExplainInfo);
        if (res > 0) {
            sqlProcessLog.setCreateTime(LocalDateTime.now());
            int i = sqlProcessLogMapper.insert(sqlProcessLog);
            if (i > 0) {
                return ApiResponse.succResponse();
            } else {
                return ApiResponse.failResponse("数据插入失败");
            }
        }
        return ApiResponse.failResponse("数据更新失败");
    }

    @GetMapping("/query")
    public ApiResponse<?> query(@RequestParam Integer infoId) {
        List<SqlProcessLog> sqlProcessLogs = sqlProcessLogMapper.selectByInfoId(infoId);
        return ApiResponse.succResponse(sqlProcessLogs);
    }

}

