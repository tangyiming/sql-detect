package com.tangym.sql.server.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangym.sql.server.dto.request.SqlTableHashPageRequest;
import com.tangym.sql.server.dto.response.ApiResponse;
import com.tangym.sql.server.entity.SqlExplainTableFingerprint;
import com.tangym.sql.server.mapper.SqlExplainTableFingerprintMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sql/tables")
public class SqlTableFingerPrintController {
    @Resource
    private SqlExplainTableFingerprintMapper sqlExplainTableFingerprintMapper;

    @PostMapping("/query")
    public ApiResponse<?> queryByPage(@RequestBody SqlTableHashPageRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<SqlExplainTableFingerprint> tables = sqlExplainTableFingerprintMapper.selectBySelective(request.getSqlExplainTableFingerprint());
        PageInfo<SqlExplainTableFingerprint> infos = new PageInfo<>(tables);
        return ApiResponse.succResponse(infos);
    }
}
