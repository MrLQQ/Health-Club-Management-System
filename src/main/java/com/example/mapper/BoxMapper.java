package com.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pojo.Box;
import com.example.pojo.BoxUserVO;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoxMapper extends BaseMapper<Box> {

    @Select("SELECT Box.*,(select userName from User where Box.userID=User.userID) as userName FROM Box")
    List<BoxUserVO> getBoxInfo(Page<BoxUserVO> page);
}
