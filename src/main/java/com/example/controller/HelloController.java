package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelloController {

    @RequestMapping("/hello")
    public String helloController(){
        return "hello";
    }

    @RequestMapping("/")
    public ModelAndView indexController(ModelAndView mv){
        mv.setViewName("index");
        return mv;
    }
}
