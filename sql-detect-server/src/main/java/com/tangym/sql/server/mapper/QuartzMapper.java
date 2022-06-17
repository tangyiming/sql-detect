package com.tangym.sql.server.mapper;

import com.tangym.sql.server.entity.Quartz;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface QuartzMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Quartz record);

    int insertSelective(Quartz record);

    Quartz selectByPrimaryKey(Integer id);

    @Select({"select * from quartz where job_class=#{jobClass}"})
    Quartz selectByKey(String jobClass);

    int updateByPrimaryKeySelective(Quartz record);

    int updateByPrimaryKey(Quartz record);

    @Select({"select * from quartz"})
    List<Quartz> selectAll();
}
