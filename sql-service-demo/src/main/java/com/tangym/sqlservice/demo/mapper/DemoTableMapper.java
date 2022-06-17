package com.tangym.sqlservice.demo.mapper;

import com.tangym.sqlservice.demo.domain.DemoTable;

public interface DemoTableMapper {

    int deleteByPrimaryKey(Long id);

    int insert(DemoTable record);

    int insertSelective(DemoTable record);

    DemoTable selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DemoTable record);

    int updateByPrimaryKey(DemoTable record);

}
