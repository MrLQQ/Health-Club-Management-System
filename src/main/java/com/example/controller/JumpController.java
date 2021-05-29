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

@Controller
public class JumpController {

    @Autowired
    CourseMapper courseMapper;

    @RequestMapping(value = {"/","/index"})
    public ModelAndView indexController(ModelAndView mv,
                                        @ModelAttribute("msg") String msg,
                                        HttpServletRequest request){

        List<Course> courses = courseMapper.selectList(null);
        mv.addObject("courses",courses);
        mv.setViewName("index");
        if (msg.equals("")) {
            msg = "null";
        }
        System.out.println("msg==>"+msg);
        request.setAttribute("msg",msg);
        return mv;
    }

    @RequestMapping(value = {"/test"})
    public ModelAndView testController(ModelAndView mv,
                                        @ModelAttribute("msg") String msg,
                                        HttpServletRequest request){
        mv.setViewName("test");
        if (msg.equals("")) {
            msg = "null";
        }
        System.out.println("msg==>"+msg);
        request.setAttribute("msg",msg);
        return mv;
    }

}
