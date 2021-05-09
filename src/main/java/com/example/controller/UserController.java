package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public ModelAndView login(ModelAndView mv,
                              @RequestParam(name = "userName") String userName,
                              @RequestParam(name = "password") String password, RedirectAttributes attr){
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("password")
                .eq("userName",userName);
        List<Map<String, Object>> maps =userMapper.selectMaps(userQueryWrapper);
        String SelectPassword="";
        for (Map<String, Object> map : maps) {
            SelectPassword= (String) map.get("password");
        }
        if (password.equals(SelectPassword)){
            mv.setViewName("defaultIndex");
            return mv;
        }
        else{
            mv.setViewName("redirect:/index");
            attr.addFlashAttribute("msg","登录失败,用户名或密码错误");
            return mv;
        }
    }

    @PostMapping("/registered")
    public ModelAndView registered(ModelAndView mv,
                                   @RequestParam(name = "userName") String userName,
                                   @RequestParam(name = "realName") String realName,
                                   @RequestParam(name = "passwordOne") String password,
                                   @RequestParam(name = "sex") String sex,
                                   @RequestParam(name = "phone") String phone,
                                   RedirectAttributes attr
                             ){
        User user = new User();
        user.setUserName(userName);
        user.setRealName(realName);
        user.setPassword(password);
        user.setSex(sex);
        user.setPhone(phone);
        user.setUserIdent("非会员");

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("userName")
                .eq("userName",userName);
        if (userMapper.selectCount(userQueryWrapper)>=1){
            mv.setViewName("redirect:/index");
            attr.addFlashAttribute("msg","注册失败，用户名已存在");
            return mv;
        }
        userMapper.insert(user);
        mv.setViewName("redirect:/index");
        attr.addFlashAttribute("msg","注册成功，请登录");
        return mv;
    }
}
