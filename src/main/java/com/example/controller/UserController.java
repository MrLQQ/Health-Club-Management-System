package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
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
            mv.setViewName("userIndex");
            mv.addObject("userName",userName);
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

    @RequestMapping("/info")
    public String userInfo(@RequestParam(name="userName",required = true) String userName,ModelMap map){

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userName",userName);
        List<User> users = userMapper.selectList(userQueryWrapper);
        for (User user : users) {
            map.addAttribute("user",user);
        }

        return "user";
    }

    @PostMapping("/update")
    public String userUpdate(@RequestParam(name="userID",required = true) String userID,
                             @RequestParam(name="userName",required = true) String userName,
                             @RequestParam(name="password",required = true) String password,
                             @RequestParam(name="sex",required = true) String sex,
                             @RequestParam(name="phone",required = true) String phone,
                             ModelMap map
                             ){
        // 在数据库中查找出目标的user对象
        User user = userMapper.selectById(userID);
        user.setUserName(userName);
        user.setPassword(password);
        user.setSex(sex);
        user.setPhone(phone);

        // 查找目标要修改的userName在数据库中是否存在
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userName",userName);
        List<User> users = userMapper.selectList(userQueryWrapper);

        // 如果目标的userName在数据中存在实体对象
        if (users!=null){
            for (User entity : users) {
                // 判断查询到的用户ID与当前请求修改的用户ID一样，说明该userName在数据库中不存在重复项。
                if (entity.getUserID().toString().equals(userID)){
                    System.out.println("id相同:"+entity.getUserID()+"=="+userID);
                    userMapper.updateById(user);
                }
                else {
                    System.out.println("id不相同:"+entity.getUserID()+"=="+userID);
                    // 如果两个ID不相同，说明如果修改userName则会在数据库中出现重复项，所以禁止修改。
                    map.addAttribute("msg","用户名已存在，请重新修改");
                    map.addAttribute("user",userMapper.selectById(userID));
                    return "user";
                }
            }
        }
        // 如果目标的userName在数据中不存在实体对象，也同样说明该userName在数据库中不存在重复项。
        userMapper.updateById(user);
        map.addAttribute("msg","修改成功");
        map.addAttribute("user",user);
        return "user";
    }

    // 用户管理页面
    @RequestMapping("/{pageNum}")
    public ModelAndView userManager(@PathVariable("pageNum") Integer pageNum,ModelAndView modelAndView){

        Page<User> page =new Page<>(pageNum,10);
        userMapper.selectPage(page,null);
        if (page.getRecords().size() > 0) {
            modelAndView.setViewName("userManager");
            modelAndView.addObject("userIPage", page);
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

    // 用户条件管理页面
    @RequestMapping("/conditional")
    public ModelAndView workersManagerByConditional(ModelAndView modelAndView,
                                                    @RequestParam(name = "pageNum") Integer pageNum,
                                                    @RequestParam(name = "userID", defaultValue = "") String userID,
                                                    @RequestParam(name = "userName", defaultValue = "") String userName,
                                                    @RequestParam(name = "realName", defaultValue = "") String realName,
                                                    @RequestParam(name = "userIdent", defaultValue = "") String userIdent,
                                                    @RequestParam(name = "sex", defaultValue = "") String sex,
                                                    RedirectAttributes attr){
        Page<User> page =new Page<>(pageNum,10);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (!userID.equals(""))
            userQueryWrapper.eq("userID",userID);
        if (!userName.equals(""))
            userQueryWrapper.eq("userName",userName);
        if (!realName.equals(""))
            userQueryWrapper.eq("realName",realName);
        if (!userIdent.equals(""))
            userQueryWrapper.eq("userIdent",userIdent);
        if (!sex.equals(""))
            userQueryWrapper.eq("sex",sex);

        userMapper.selectPage(page,userQueryWrapper);

        if (page.getRecords().size() > 0) {
            modelAndView.setViewName("userManager");
            modelAndView.addObject("userIPage", page);
            modelAndView.addObject("pageCurrent", Math.toIntExact(page.getCurrent()));
            modelAndView.addObject("pages", Math.toIntExact(page.getPages()));
            modelAndView.addObject("Total", Math.toIntExact(page.getTotal()));
            modelAndView.addObject("userID", userID);
            modelAndView.addObject("userName", userName);
            modelAndView.addObject("realName", realName);
            modelAndView.addObject("userIdent", userIdent);
            modelAndView.addObject("sex", sex);
            // 是否为条件查询的flag
            modelAndView.addObject("flag","conditional");
            System.out.println("当前页"+page.getCurrent());
        } else {
            // 没有查询到数据 或者查询错误
            // 重定向到默认查询页
            System.out.println("用户条件查找触发错误");
            modelAndView.setViewName("redirect:/user/1");
            attr.addFlashAttribute("msg","未查找到有效数据，请修改条件后重新查询");
        }
        return modelAndView;
    }
}
