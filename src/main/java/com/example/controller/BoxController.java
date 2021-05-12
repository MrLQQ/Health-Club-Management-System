package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.BoxMapper;
import com.example.pojo.BoxUserVO;
import com.example.service.BoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("box")
public class BoxController {

    @Autowired
    BoxMapper boxMapper;

    BoxService boxService;

    // 储物柜管理页面
    @RequestMapping("{pageNum}")
    public ModelAndView boxManager(@PathVariable("pageNum") Integer pageNum, ModelAndView modelAndView){

        Page<BoxUserVO> page =new Page<>(pageNum,10);
        // 使用自定的多表联合查询
        List<BoxUserVO> boxInfo = boxMapper.getBoxInfo(page);

        if (page.getTotal() > 0) {
            modelAndView.setViewName("boxManager");
            modelAndView.addObject("boxInfo", boxInfo);
            modelAndView.addObject("pageCurrent", Math.toIntExact(page.getCurrent()));
            modelAndView.addObject("pages", Math.toIntExact(page.getPages()));
            modelAndView.addObject("Total", Math.toIntExact(page.getTotal()));
            // 是否为条件查询的flag
            modelAndView.addObject("flag","normal");
            System.out.println("当前页"+page.getCurrent());
        } else {
            modelAndView.addObject("msg", "查询失败");
        }
        return modelAndView;
    }

    // 储物柜条件管理页面
    @RequestMapping("/conditional")
    public ModelAndView boxpManagerByConditional(@PathVariable("pageNum") Integer pageNum, ModelAndView modelAndView){


        return modelAndView;
    }

}
