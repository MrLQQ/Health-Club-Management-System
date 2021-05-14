package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.CourseMapper;
import com.example.mapper.EmpMapper;
import com.example.pojo.CourseEmpVO;
import com.example.pojo.Emp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController {

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    EmpMapper empMapper;

    // 课程管理页面
    @RequestMapping("{pageNum}")
    public ModelAndView courseManager(@PathVariable("pageNum") Integer pageNum, ModelAndView modelAndView){

        Page<CourseEmpVO> page =new Page<>(pageNum,10);
        // 使用自定的多表联合查询
        List<CourseEmpVO> courseInfo = courseMapper.getCourseInfo(page);
        if (page.getTotal() > 0) {
            modelAndView.setViewName("courseManager");
            modelAndView.addObject("courseInfo", courseInfo);
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

    // 课程条件管理页面
    @RequestMapping("/conditional")
    public ModelAndView courseManagerByConditional(ModelAndView modelAndView,
                                                   @RequestParam(name = "pageNum") Integer pageNum,
                                                   @RequestParam(name = "courseID", defaultValue = "") String courseID,
                                                   @RequestParam(name = "courseName", defaultValue = "") String courseName,
                                                   @RequestParam(name = "empName", defaultValue = "") String empName,
                                                   RedirectAttributes attr){

        Page<CourseEmpVO> page = new Page<>(pageNum,10);

        QueryWrapper<CourseEmpVO> courseEmpVOQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Emp> empQueryWrapper = new QueryWrapper<>();

        if (!courseID.equals(""))
            courseEmpVOQueryWrapper.eq("courseID",courseID);
        if (!courseName.equals(""))
            courseEmpVOQueryWrapper.eq("courseName",courseName);
        if (!empName.equals("")){
            empQueryWrapper.select("empID").eq("empName",empName);
            Emp emp = empMapper.selectOne(empQueryWrapper);
            courseEmpVOQueryWrapper.eq("empID",emp.getEmpID());
        }

        List<CourseEmpVO> courseInfo = courseMapper.getCourseInfoByConditional(page, courseEmpVOQueryWrapper);

        if (page.getTotal()>0){
            modelAndView.setViewName("courseManager");
            modelAndView.addObject("courseInfo", courseInfo);
            modelAndView.addObject("pageCurrent", Math.toIntExact(page.getCurrent()));
            modelAndView.addObject("pages", Math.toIntExact(page.getPages()));
            modelAndView.addObject("Total", Math.toIntExact(page.getTotal()));
            modelAndView.addObject("courseID",courseID);
            modelAndView.addObject("courseName",courseName);
            modelAndView.addObject("empName",empName);

            // 是否为条件查询的flag
            modelAndView.addObject("flag","conditional");
            System.out.println("当前页"+page.getCurrent());
        } else {
            // 没有查询到数据 或者查询错误
            // 重定向到默认查询页
            System.out.println("储物柜条件查找触发错误");
            modelAndView.setViewName("redirect:/course/1");
            attr.addFlashAttribute("msg","未查找到有效数据，请修改条件后重新查询");
        }

        return modelAndView;
    }
}
