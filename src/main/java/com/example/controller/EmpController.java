package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.EmpMapper;
import com.example.pojo.Emp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/emp")
public class EmpController {

    @Autowired
    EmpMapper empMapper;


    // 员工管理页面
    @RequestMapping("/{pageNum}")
    public ModelAndView empManager(@PathVariable("pageNum") Integer pageNum,ModelAndView modelAndView){

        Page<Emp> page =new Page<>(pageNum,10);
        empMapper.selectPage(page,null);
        if (page.getRecords().size() > 0) {
            modelAndView.setViewName("empManager");
            modelAndView.addObject("empIPage", page);
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

    // 员工条件管理页面
    @RequestMapping("/conditional")
    public ModelAndView empManagerByConditional(ModelAndView modelAndView,
                                                    @RequestParam(name = "pageNum") Integer pageNum,
                                                    @RequestParam(name = "empID", defaultValue = "") String empID,
                                                    @RequestParam(name = "empName", defaultValue = "") String empName,
                                                    @RequestParam(name = "sex", defaultValue = "") String sex,
                                                    @RequestParam(name = "job", defaultValue = "") String job,
                                                    RedirectAttributes attr){
        Page<Emp> page =new Page<>(pageNum,10);
        QueryWrapper<Emp> empQueryWrapper = new QueryWrapper<>();
        if (!empID.equals(""))
            empQueryWrapper.eq("empID",empID);
        if (!empName.equals(""))
            empQueryWrapper.eq("empName",empName);
        if (!sex.equals(""))
            empQueryWrapper.eq("sex",sex);
        if (!job.equals(""))
            empQueryWrapper.eq("job",job);

        empMapper.selectPage(page,empQueryWrapper);

        if (page.getRecords().size() > 0) {
            modelAndView.setViewName("empManager");
            modelAndView.addObject("empIPage", page);
            modelAndView.addObject("pageCurrent", Math.toIntExact(page.getCurrent()));
            modelAndView.addObject("pages", Math.toIntExact(page.getPages()));
            modelAndView.addObject("Total", Math.toIntExact(page.getTotal()));
            modelAndView.addObject("empID", empID);
            modelAndView.addObject("empName", empName);
            modelAndView.addObject("sex", sex);
            modelAndView.addObject("job", job);
            // 是否为条件查询的flag
            modelAndView.addObject("flag","conditional");
            System.out.println("当前页"+page.getCurrent());
        } else {
            // 没有查询到数据 或者查询错误
            // 重定向到默认查询页
            System.out.println("员工条件查找触发错误");
            modelAndView.setViewName("redirect:/emp/1");
            attr.addFlashAttribute("msg","未查找到有效数据，请修改条件后重新查询");
        }
        return modelAndView;
    }
}
