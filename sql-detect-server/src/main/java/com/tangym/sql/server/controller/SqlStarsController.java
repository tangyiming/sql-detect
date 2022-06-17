package com.tangym.sql.server.controller;

import com.tangym.sql.server.dto.response.ApiResponse;
import com.tangym.sql.server.entity.SqlExplainStars;
import com.tangym.sql.server.mapper.SqlExplainStarsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/sql/stars")
public class SqlStarsController {
    @Resource
    private SqlExplainStarsMapper sqlExplainStarsMapper;

    @PostMapping("/add")
    public ApiResponse<?> add(@RequestBody SqlExplainStars sqlExplainStars) {
        Integer i = sqlExplainStarsMapper.selectByStarInfo(sqlExplainStars);
        if (null != i && i > 0) {
            return ApiResponse.succResponse("已添加关注");
        }
        int res = sqlExplainStarsMapper.insert(sqlExplainStars);
        if (res > 0) {
            return ApiResponse.succResponse("已添加关注");
        }
        return ApiResponse.failResponse("关注失败，请重试");
    }

    @PostMapping("/delete")
    public ApiResponse<?> delete(@RequestBody SqlExplainStars sqlExplainStars) {
        int i = sqlExplainStarsMapper.deleteByStarInfo(sqlExplainStars);
        if (i > 0) {
            return ApiResponse.succResponse("已取消关注");
        }
        return ApiResponse.failResponse("取消关注失败，请重试");
    }
}
