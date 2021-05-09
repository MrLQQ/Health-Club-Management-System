package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.AdminMapper;
import com.example.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminMapper adminMapper;

    @RequestMapping("")
    public String LoginPage(@ModelAttribute("msg") String msg, HttpServletRequest request){
        if (msg.equals(""))
            msg = "null";
        System.out.println("msg==>"+msg);
        request.setAttribute("msg",msg);
        return "login";
    }
    @PostMapping("/login")
    public String LoginVerification(@RequestParam(name = "adminName") String adminName,
                                    @RequestParam(name = "adminPassword") String adminPassword,
                                    RedirectAttributes attr,
                                    ModelMap map){

        System.out.println("adminName=>"+adminName);
        System.out.println("adminPassword=>"+adminPassword);

        QueryWrapper<Admin> adminQueryWrapper = new QueryWrapper<>();
        adminQueryWrapper.select("adminPassword")
                .eq("adminName",adminName)
                .eq("adminPassword",adminPassword);
        if (adminMapper.selectCount(adminQueryWrapper)<1){
            attr.addFlashAttribute("msg","管理员账户或密码错误！");
            return "redirect:/admin";
        }
        map.addAttribute("ident","管理员");
        map.addAttribute("adminName",adminName);
        return "indexManager";
    }

    @RequestMapping("/defaultIndex")
    public String defaultIndex(){
        return "defaultIndex";
    }

    @RequestMapping("/workersManager")
    public String workersManager(){
        return "workersManager";
    }

    @RequestMapping("/userManager")
    public String userManager(){
        return "userManager";
    }

    @RequestMapping("/courseManager")
    public String courseManager(){
        return "courseManager";
    }

    @RequestMapping("/boxManager")
    public String boxManager(){
        return "boxManager";
    }
}
