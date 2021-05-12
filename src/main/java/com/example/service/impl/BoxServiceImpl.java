package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.BoxMapper;
import com.example.pojo.Box;
import com.example.pojo.BoxUserVO;
import com.example.service.BoxService;
import org.springframework.stereotype.Service;

@Service
public class BoxServiceImpl extends ServiceImpl<BoxMapper, Box> implements BoxService {
    @Override
    public Page<BoxUserVO> getBoxInfo(Page<BoxUserVO> page) {

        return page.setRecords(this.baseMapper.getBoxInfo(page));
    }
}
