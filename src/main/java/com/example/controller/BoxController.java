package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.BoxMapper;
import com.example.mapper.UserMapper;
import com.example.pojo.BoxUserVO;
import com.example.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("box")
public class BoxController {

    @Autowired
    BoxMapper boxMapper;

    @Autowired
    UserMapper userMapper;


    // 储物柜管理页面
    @RequestMapping("{pageNum}")
    public ModelAndView boxManager(@PathVariable("pageNum") Integer pageNum, ModelAndView modelAndView){

        Page<BoxUserVO> page =new Page<>(pageNum,10);
        // 使用自定的多表联合查询
        List<BoxUserVO> boxInfo = boxMapper.getBoxInfo(page);
        if (page.getTotal() > 0) {
            modelAndView.setViewName("boxManager");
            modelAndView.addObject("boxInfo", boxInfo);
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

    // 储物柜条件管理页面
    @RequestMapping("/conditional")
    public ModelAndView boxpManagerByConditional(ModelAndView modelAndView,
                                                 @RequestParam(name = "pageNum") Integer pageNum,
                                                 @RequestParam(name = "boxID", defaultValue = "") String boxID,
                                                 @RequestParam(name = "userName", defaultValue = "") String userName,
                                                 @RequestParam(name = "idle", defaultValue = "") String idle,
                                                 RedirectAttributes attr){


        Page<BoxUserVO> page= new Page<>(pageNum,10);
        QueryWrapper<BoxUserVO> boxUserVOQueryWrapper = new QueryWrapper<>();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (!boxID.equals(""))
            boxUserVOQueryWrapper.eq("boxID",boxID);
        if (!userName.equals("")){

            // 在User表中找到userName对应的userID传给Box表中的UserID
            userQueryWrapper.select("UserID").eq("userName",userName);
            User user = userMapper.selectOne(userQueryWrapper);
            boxUserVOQueryWrapper.eq("userID",user.getUserID());
        }
        if (idle.equals("free"))
            boxUserVOQueryWrapper.isNull("userID");
        else if (idle.equals("rented"))
            boxUserVOQueryWrapper.isNotNull("userID");

        List<BoxUserVO> boxInfo = boxMapper.getBoxInfoByconditional(page,boxUserVOQueryWrapper);

        if (page.getTotal() > 0) {
            modelAndView.setViewName("boxManager");
            modelAndView.addObject("boxInfo", boxInfo);
            modelAndView.addObject("pageCurrent", Math.toIntExact(page.getCurrent()));
            modelAndView.addObject("pages", Math.toIntExact(page.getPages()));
            modelAndView.addObject("Total", Math.toIntExact(page.getTotal()));
            modelAndView.addObject("boxID",boxID);
            modelAndView.addObject("userName",userName);
            modelAndView.addObject("idle",idle);
            // 是否为条件查询的flag
            modelAndView.addObject("flag","conditional");
            System.out.println("当前页"+page.getCurrent());
        } else {
            // 没有查询到数据 或者查询错误
            // 重定向到默认查询页
            System.out.println("储物柜条件查找触发错误");
            modelAndView.setViewName("redirect:/box/1");
            attr.addFlashAttribute("msg","未查找到有效数据，请修改条件后重新查询");
        }
        return modelAndView;
    }

}
