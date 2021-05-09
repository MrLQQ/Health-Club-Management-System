package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class JumpController {

    @RequestMapping(value = {"/","/index"})
    public ModelAndView indexController(ModelAndView mv,
                                        @ModelAttribute("msg") String msg,
                                        HttpServletRequest request){
        mv.setViewName("index");
        if (msg.equals(""))
            msg = "null";
        System.out.println("msg==>"+msg);
        request.setAttribute("msg",msg);
        return mv;
    }

}
