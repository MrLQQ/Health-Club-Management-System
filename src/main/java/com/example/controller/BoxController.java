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

/**
 * 储物柜Controller<br>
 * 用于处理所有关于储物柜的请求与业务
 * @author LQQ
 */
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
     * 储物柜条件管理页面，分页显示，多表联合查询，条件查询
     * @param modelAndView modelAndView
     * @param pageNum 分页页码
     * @param boxID 储物柜ID
     * @param userName 用户名
     * @param idle 状态：free||rented<br/>
     *             储物柜的租用状态。
     * @param attr 全局RedirectAttributes
     * @return ModelAndView
     */
    @RequestMapping("/conditional")
    public ModelAndView boxManagerByConditional(ModelAndView modelAndView,
                                                 @RequestParam(name = "pageNum") Integer pageNum,
                                                 @RequestParam(name = "boxID", defaultValue = "") String boxID,
                                                 @RequestParam(name = "userName", defaultValue = "") String userName,
                                                 @RequestParam(name = "idle", defaultValue = "") String idle,
                                                 RedirectAttributes attr){


        Page<BoxUserVO> page= new Page<>(pageNum,10);
        QueryWrapper<BoxUserVO> boxUserVOQueryWrapper = new QueryWrapper<>();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();

        // 判断是否存在条件查询，如果存在把条件放入QueryWrapper<BoxUserVO>中
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

        // 使用自定义的getBoxInfoByConditional方法进行分页查询
        List<BoxUserVO> boxInfo = boxMapper.getBoxInfoByConditional(page,boxUserVOQueryWrapper);

        // 如果分页查询到了数据，把需要的数据保存传到前端。
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

    /**
     * 储物柜修改页面跳转<br>
     * 将查询到的目标箱子的信息传到前端
     * @param boxID 目标储物柜ID
     * @param modelAndView modelAndView
     * @return 修改成功返回modelAndView
     */
    @RequestMapping("/edit/{boxID}")
    public ModelAndView boxEdit(@PathVariable("boxID") String boxID,ModelAndView modelAndView){

        // 设置视图映射为boxUpdate.html页面
        modelAndView.setViewName("boxUpdate");

        // 根据目标ID查询box
        Box box = boxMapper.selectById(boxID);

        QueryWrapper<User> userQueryWrapper =new QueryWrapper<>();
        userQueryWrapper.select("userName")
                .eq("userID",box.getUserID());
        User user = userMapper.selectOne(userQueryWrapper);

        modelAndView.addObject("box",box);
        modelAndView.addObject("userName",user.getUserName());

        return modelAndView;
    }

    /**
     * 储物柜信息修改业务、租用业务
     * @param modelAndView modelAndView
     * @param boxID 目标储物柜ID
     * @param userName 盒子的租用人
     * @param startTime 租用开始时间
     * @param endTime 租用结束时间
     * @return 重定向到显示盒子界面=>redirect:/box/1
     */
    @RequestMapping("/update")
    public ModelAndView userUpdate(ModelAndView modelAndView,
                                   @RequestParam(name = "boxID",required = true) String boxID,
                                   @RequestParam(name = "userName", required = true) String userName,
                                   @RequestParam(name = "startTime", required = true) String startTime,
                                   @RequestParam(name = "endTime", required = true) String endTime){

        Box box = new Box();
        box.setBoxID(boxID);
        try {
            // 将前台的日期时间类型，进行格式化处理，然后存储日数据
            box.setStartTime(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").parse(startTime));
            box.setEndTime(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").parse(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userName",userName);
        User user = userMapper.selectOne(userQueryWrapper);

        // 判断租用人是否存在
        if (user!=null){

            // 如果存在，允许修改（租用）
            box.setUserID(user.getUserID());
            int update = boxMapper.updateById(box);
            if (update>=1){
                modelAndView.setViewName("redirect:/box/1");
                return modelAndView;
            }
        }

        // 不存在，视图跳转到boxUpdate.html页面重新修改
        modelAndView.setViewName("boxUpdate");
        modelAndView.addObject("userName",userName);
        modelAndView.addObject("box",box);
        modelAndView.addObject("msg","修改失败,当前教练不存在！");
        return modelAndView;
    }

    /**
     * 储物柜删除业务
     * @param modelAndView modelAndView
     * @param boxID 目标储物柜ID
     * @param attr 全局RedirectAttributes，用于重定向后传递数据
     * @return 重定向到显示所有储物柜页面=>redirect:/box/1
     */
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
