package com.example.controller;

import com.example.mapper.CourseMapper;
import com.example.pojo.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 跳转Controller
 * @author LQQ
 */
@Controller
public class JumpController {

    @Autowired
    CourseMapper courseMapper;

    /**
     * 欢迎页视图映射
     * @param mv ModelAndView
     * @param msg 消息
     * @param request HttpServletRequest
     * @return index.html
     */
    @RequestMapping(value = {"/","/index"})
    public ModelAndView indexController(ModelAndView mv,
                                        @ModelAttribute("msg") String msg,
                                        HttpServletRequest request){

        // 查询课程数据库，获取课程表后，传递到前端进行展示
        List<Course> courses = courseMapper.selectList(null);
        mv.addObject("courses",courses);
        mv.setViewName("index");
        if ("".equals(msg)) {
            msg = "null";
        }
        System.out.println("msg==>"+msg);
        request.setAttribute("msg",msg);
        return mv;
    }

}
