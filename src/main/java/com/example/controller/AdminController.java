package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.AdminMapper;
import com.example.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 管理员Controller<br>
 * 负责管理员像管理业务
 * @author LQQ
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminMapper adminMapper;

    /**
     * 管理主页请求拦截器，进行管理员主页的视图映射
     * @param msg 前台页面的消息
     * @param request 获取请求
     * @return 视图
     */
    @RequestMapping("")
    public String loginPage(@ModelAttribute("msg") String msg, HttpServletRequest request){
        if ("".equals(msg)) {
            msg = "null";
        }
        System.out.println("msg==>"+msg);
        request.setAttribute("msg",msg);
        return "login";
    }

    /**
     * 管理员登录业务
     * @param adminName 用户名
     * @param adminPassword 密码
     * @param attr 全局RedirectAttributes，可以在重定向时，保留传递信息
     * @param map 存放传递数据
     * @return adminHomepage.html||"redirect:/admin"
     */
    @PostMapping("/login")
    public String loginVerification(@RequestParam(name = "adminName") String adminName,
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
        return "adminHomepage";
    }


    /**
     * 管理员默认 页视图映射
     * @return defaultIndex.html视图
     */
    @RequestMapping("/defaultIndex")
    public String defaultIndex(){
        return "defaultIndex";
    }


    /**
     * 用户管理页面
     * @return userManager.html视图
     */
    @RequestMapping("/userManager")
    public String userManager(){
        return "userManager";
    }

    /**
     * 课程管理页面
     * @return courseManager.html视图
     */
    @RequestMapping("/courseManager")
    public String courseManager(){
        return "courseManager";
    }

    /**
     * 储物箱管理
     * @return boxManager.html视图
     */
    @RequestMapping("/boxManager")
    public String boxManager(){
        return "boxManager";
    }
}
