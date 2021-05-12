package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.AdminMapper;
import com.example.pojo.Admin;
import com.example.pojo.Emp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
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

    // 管理员登录处理
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

    // 管理员默认页
    @RequestMapping("/defaultIndex")
    public String defaultIndex(){
        return "defaultIndex";
    }


    // 用户管理页面
    @RequestMapping("/userManager")
    public String userManager(){
        return "userManager";
    }

    // 课程管理页面
    @RequestMapping("/courseManager")
    public String courseManager(){
        return "courseManager";
    }

    // 储物箱管理
    @RequestMapping("/boxManager")
    public String boxManager(){
        return "boxManager";
    }
}
