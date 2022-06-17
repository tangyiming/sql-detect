package com.tangym.sqlservice.demo.controller;

import com.tangym.sqlservice.demo.domain.DemoTable;
import com.tangym.sqlservice.demo.mapper.DemoTableMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @Resource
    private DemoTableMapper demoTableMapper;

    @PostMapping("/add")
    public void add(@RequestBody DemoTable demoTable) {
        demoTableMapper.insert(demoTable);
    }

    @GetMapping("/get")
    public DemoTable get(long id) {
        return demoTableMapper.selectByPrimaryKey(id);
    }
}
