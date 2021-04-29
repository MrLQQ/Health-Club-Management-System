package com.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pojo.Emp;
import com.example.pojo.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface EmpMapper extends BaseMapper<Emp> {
    List<Map<String, Object>> selectMaps(QueryWrapper<User> userQueryWrapper);
}
