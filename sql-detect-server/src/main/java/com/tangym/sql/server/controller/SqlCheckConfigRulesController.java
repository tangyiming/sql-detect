package com.tangym.sql.server.controller;

import com.tangym.sql.server.config.AfterServiceStarted;
import com.tangym.sql.server.dto.response.ApiResponse;
import com.tangym.sql.server.entity.SqlExplainConfigRules;
import com.tangym.sql.server.mapper.SqlExplainConfigRulesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sql/config/rules")
public class SqlCheckConfigRulesController {
    @Resource
    private SqlExplainConfigRulesMapper sqlExplainConfigRulesMapper;

    @PostMapping("create")
    public ApiResponse<?> create(@RequestBody SqlExplainConfigRules sqlExplainConfigRules) {
        int i = sqlExplainConfigRulesMapper.insert(sqlExplainConfigRules);
        if (i > 0) {
            return ApiResponse.succResponse("创建成功");
        }
        return ApiResponse.failResponse("创建失败");
    }

    @PostMapping("update")
    public ApiResponse<?> update(@RequestBody SqlExplainConfigRules sqlExplainConfigRules) {
        int i = sqlExplainConfigRulesMapper.updateByPrimaryKey(sqlExplainConfigRules);
        if (i > 0) {
            if (sqlExplainConfigRules.getRuleKey().equals("criteria")) {
                AfterServiceStarted.SyncSqlExplainRules(sqlExplainConfigRules, new ArrayList<>());
            }
            return ApiResponse.succResponse("更新成功");
        }
        return ApiResponse.failResponse("更新失败");
    }

    @GetMapping("list")
    public ApiResponse<?> list() {
        List<SqlExplainConfigRules> sqlExplainConfigRules = sqlExplainConfigRulesMapper.selectAll();
        return ApiResponse.succResponse(sqlExplainConfigRules);
    }

    @GetMapping("capture")
    public String getCapture() {
        SqlExplainConfigRules sqlExplainConfigRules = sqlExplainConfigRulesMapper.selectByRuleKey("capture");
        return sqlExplainConfigRules.getRuleDetail();
    }

    @GetMapping("delete")
    public ApiResponse<?> delete(@RequestParam int id) {
        int i = sqlExplainConfigRulesMapper.deleteByPrimaryKey(id);
        if (i > 0) {
            return ApiResponse.succResponse("删除成功");
        }

        return ApiResponse.failResponse("删除失败");
    }
}
