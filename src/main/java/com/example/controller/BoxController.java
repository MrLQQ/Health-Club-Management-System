package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.BoxMapper;
import com.example.mapper.UserMapper;
import com.example.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("box")
public class BoxController {

    @Autowired
    BoxMapper boxMapper;

    @Autowired
    UserMapper userMapper;


    /**
     * 储物柜管理页面,分页显示
     * @param pageNum 分页页码
     * @param modelAndView modelAndView
     * @return modelAndView
     */
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

    /**
     * 储物柜条件管理页面，分页显示
     * @param modelAndView modelAndView
     * @param pageNum 分页页码
     * @param boxID 储物柜ID
     * @param userName
     * @param idle
     * @param attr
     * @return
     */
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
        if (!boxID.equals("")) {
            boxUserVOQueryWrapper.eq("boxID",boxID);
        }
        if (!userName.equals("")){

            // 在User表中找到userName对应的userID传给Box表中的UserID
            userQueryWrapper.select("UserID").eq("userName",userName);
            User user = userMapper.selectOne(userQueryWrapper);
            boxUserVOQueryWrapper.eq("userID",user.getUserID());
        }
        if (idle.equals("free")) {
            boxUserVOQueryWrapper.isNull("userID");
        } else if (idle.equals("rented")) {
            boxUserVOQueryWrapper.isNotNull("userID");
        }

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

    @RequestMapping("/edit/{boxID}")
    public ModelAndView boxEdit(@PathVariable("boxID") String boxID,ModelAndView modelAndView){
        modelAndView.setViewName("boxUpdate");
        Box box = boxMapper.selectById(boxID);

        QueryWrapper<User> userQueryWrapper =new QueryWrapper<>();
        userQueryWrapper.select("userName")
                .eq("userID",box.getUserID());
        User user = userMapper.selectOne(userQueryWrapper);

        modelAndView.addObject("box",box);
        modelAndView.addObject("userName",user.getUserName());

        return modelAndView;
    }

    @RequestMapping("/update")
    public ModelAndView userUpdate(ModelAndView modelAndView,
                                   @RequestParam(name = "boxID",required = true) String boxID,
                                   @RequestParam(name = "userName", required = true) String userName,
                                   @RequestParam(name = "startTime", required = true) String startTime,
                                   @RequestParam(name = "endTime", required = true) String endTime){

        Box box = new Box();
        box.setBoxID(boxID);
        try {
            box.setStartTime(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").parse(startTime));
            box.setEndTime(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").parse(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userName",userName);
        User user = userMapper.selectOne(userQueryWrapper);
        if (user!=null){
            box.setUserID(user.getUserID());
            int update = boxMapper.updateById(box);
            if (update>=1){
                modelAndView.setViewName("redirect:/box/1");
                return modelAndView;
            }
        }
        modelAndView.setViewName("boxUpdate");
        modelAndView.addObject("userName",userName);
        modelAndView.addObject("box",box);
        modelAndView.addObject("msg","修改失败,当前教练不存在！");
        return modelAndView;
    }

    @RequestMapping("/delete/{boxID}")
    public ModelAndView userDelete(ModelAndView modelAndView,
                                   @PathVariable("boxID") String boxID,
                                   RedirectAttributes attr){

        // 由于选课表或者租赁表的其中的外键都为userID
        // 所以在删除时候可能出现违反唯一约束的异常
        try{
            boxMapper.deleteById(boxID);
        }catch (Exception e){
            Throwable cause = e.getCause();
            // 判断是否为’违反唯一约束‘异常
            if (cause instanceof SQLIntegrityConstraintViolationException){
                attr.addFlashAttribute("msg","删除失败，可能该用户的服务还未结束，可能已选课或租赁储物柜！");
                modelAndView.setViewName("redirect:/box/1");
                return modelAndView;
            }
        }
        modelAndView.setViewName("redirect:/box/1");
        return modelAndView;
    }

}
