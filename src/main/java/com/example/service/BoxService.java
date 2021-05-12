package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.Box;
import com.example.pojo.BoxUserVO;

public interface  BoxService extends IService<Box> {
    Page<BoxUserVO> getBoxInfo(Page<BoxUserVO> page);
}
