package com.tangym.sql.server.controller;

import com.tangym.sql.server.config.AfterServiceStarted;
import com.tangym.sql.server.dto.response.ApiResponse;
import com.tangym.sql.server.entity.SqlExplainConfigRulesScript;
import com.tangym.sql.server.mapper.SqlExplainConfigRulesScriptMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sql/config/rules/script")
public class SqlCheckConfigRuleScriptController {
    @Resource
    private SqlExplainConfigRulesScriptMapper scriptMapper;

    @PostMapping("create")
    public ApiResponse<?> create(@RequestBody SqlExplainConfigRulesScript script) {
        int i = scriptMapper.insert(script);
        if (i > 0) {
            return ApiResponse.succResponse("创建成功");
        }
        return ApiResponse.failResponse("创建失败");
    }

    @PostMapping("update")
    public ApiResponse<?> update(@RequestBody SqlExplainConfigRulesScript script) {
        int i = scriptMapper.updateByPrimaryKey(script);
        List<String> allScripts = scriptMapper.selectAllScripts();
        if (i > 0) {
            AfterServiceStarted.SyncSqlExplainRules(null, allScripts);
            return ApiResponse.succResponse("更新成功");
        }
        return ApiResponse.failResponse("更新失败");
    }

    @GetMapping("list")
    public ApiResponse<?> list() {
        List<SqlExplainConfigRulesScript> rulesScripts = scriptMapper.selectAll();
        return ApiResponse.succResponse(rulesScripts);
    }

    @GetMapping("delete")
    public ApiResponse<?> delete(@RequestParam int id) {
        int i = scriptMapper.deleteByPrimaryKey(id);
        if (i > 0) {
            return ApiResponse.succResponse("删除成功");
        }

        return ApiResponse.failResponse("删除失败");
    }
}
