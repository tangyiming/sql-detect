package com.tangym.sql.server.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangym.sql.server.dto.request.SqlAppConfigsPageRequest;
import com.tangym.sql.server.dto.response.ApiResponse;
import com.tangym.sql.server.entity.SqlAppConfigs;
import com.tangym.sql.server.mapper.SqlAppConfigsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sql/config/service")
public class SqlAppConfigsController {
    @Resource
    private SqlAppConfigsMapper sqlAppConfigsMapper;

    @PostMapping("/query")
    public ApiResponse<?> queryByPage(@RequestBody SqlAppConfigsPageRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<SqlAppConfigs> configs = sqlAppConfigsMapper.selectBySelective(request.getAppConfigs());
        PageInfo<SqlAppConfigs> sqlAppConfigsPageInfo = new PageInfo<>(configs);
        return ApiResponse.succResponse(sqlAppConfigsPageInfo);
    }

    @PostMapping("/add")
    public ApiResponse<?> add(@RequestBody SqlAppConfigs sqlAppConfigs) {
        int i = sqlAppConfigsMapper.insert(sqlAppConfigs);
        if (i > 0) {
            return ApiResponse.succResponse("创建成功");
        }
        return ApiResponse.failResponse("创建失败");
    }

    @PostMapping("/update")
    public ApiResponse<?> update(@RequestBody SqlAppConfigs sqlAppConfigs) {
        int i = sqlAppConfigsMapper.updateByPrimaryKeySelective(sqlAppConfigs);
        if (i > 0) {
            return ApiResponse.succResponse("更新成功");
        }
        return ApiResponse.failResponse("更新失败");
    }

    @GetMapping("/delete")
    public ApiResponse<?> delete(@RequestParam int id) {
        int i = sqlAppConfigsMapper.deleteByPrimaryKey(id);
        if (i > 0) {
            return ApiResponse.succResponse("删除成功");
        }
        return ApiResponse.failResponse("删除失败");
    }

}
