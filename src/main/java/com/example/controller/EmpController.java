package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.CourseMapper;
import com.example.mapper.EmpMapper;
import com.example.mapper.PickMapper;
import com.example.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * 员工Controller，拦截并处理关于员工的请求
 * @Author LQQ
 */
@Controller
@RequestMapping("/emp")
public class EmpController {

    @Autowired
    EmpMapper empMapper;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    PickMapper pickMapper;

    /**
     * 所有员工分页查询
     * @param pageNum 分页页码
     * @param modelAndView modelAndView
     * @return empManager.html视图
     */
    @RequestMapping("/{pageNum}")
    public ModelAndView empManager(@PathVariable("pageNum") Integer pageNum,ModelAndView modelAndView){

        Page<Emp> page =new Page<>(pageNum,10);
        empMapper.selectPage(page,null);
        if (page.getRecords().size() > 0) {
            modelAndView.setViewName("empManager");
            modelAndView.addObject("empIPage", page);
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
     * 员工附带条件的多表分页查询
     * @param modelAndView modelAndView
     * @param pageNum 分页页码
     * @param empID 员工ID
     * @param empName 员工姓名
     * @param sex 性别
     * @param job 职位
     * @param attr 用户重定向后页面，存储数据
     * @return 分页查询成功：empManager.html视图
     *             查询失败：重定向到redirect:/emp/1
     */
    @RequestMapping("/conditional")
    public ModelAndView empManagerByConditional(ModelAndView modelAndView,
                                                    @RequestParam(name = "pageNum") Integer pageNum,
                                                    @RequestParam(name = "empID", defaultValue = "") String empID,
                                                    @RequestParam(name = "empName", defaultValue = "") String empName,
                                                    @RequestParam(name = "sex", defaultValue = "") String sex,
                                                    @RequestParam(name = "job", defaultValue = "") String job,
                                                    RedirectAttributes attr){
        Page<Emp> page =new Page<>(pageNum,10);
        QueryWrapper<Emp> empQueryWrapper = new QueryWrapper<>();

        // 如果附带条件查询，则把条件放入QueryWrapper<Emp> 条件构造器中
        if (!"".equals(empID)) {
            empQueryWrapper.eq("empID",empID);
        }
        if (!"".equals(empName)) {
            empQueryWrapper.eq("empName",empName);
        }
        if (!"".equals(sex)) {
            empQueryWrapper.eq("sex",sex);
        }
        if (!"".equals(job)) {
            empQueryWrapper.eq("job",job);
        }

        // 分页查询
        empMapper.selectPage(page,empQueryWrapper);

        // 判断是否查询成功
        if (page.getRecords().size() > 0) {
            modelAndView.setViewName("empManager");
            modelAndView.addObject("empIPage", page);
            modelAndView.addObject("pageCurrent", Math.toIntExact(page.getCurrent()));
            modelAndView.addObject("pages", Math.toIntExact(page.getPages()));
            modelAndView.addObject("Total", Math.toIntExact(page.getTotal()));
            modelAndView.addObject("empID", empID);
            modelAndView.addObject("empName", empName);
            modelAndView.addObject("sex", sex);
            modelAndView.addObject("job", job);
            // 是否为条件查询的flag
            modelAndView.addObject("flag","conditional");
            System.out.println("当前页"+page.getCurrent());
        } else {
            // 没有查询到数据 或者查询错误
            // 重定向到默认查询页
            System.out.println("员工条件查找触发错误");
            modelAndView.setViewName("redirect:/emp/1");
            attr.addFlashAttribute("msg","未查找到有效数据，请修改条件后重新查询");
        }
        return modelAndView;
    }

    /**
     * 添加新员工视图映射
     * @return empAdd.html视图
     */
    @RequestMapping("/empAdd")
    public String empAdd(){
        return "empAdd";
    }

    /**
     * 添加新员工业务
     * @param modelAndView modelAndView
     * @param empName 员工用户名
     * @param realName 真实姓名
     * @param password 密码
     * @param sex 性别
     * @param job 职位
     * @param address 地址
     * @param phone 联系方式
     * @return 如果要添加的用户名存在，处理"/emoAdd"请求，跳转到empAdd.html,重新修改
     *         如果要添加的用户名不存在，且添加成功：重定向到redirect:/emp/1
     *                               添加不成功：处理"/emoAdd"请求，跳转到empAdd.html,重新修改
     */
    @RequestMapping("/save")
    public ModelAndView empSave(ModelAndView modelAndView,
                                @RequestParam(name = "empName") String empName,
                                @RequestParam(name = "realName") String realName,
                                @RequestParam(name = "password") String password,
                                @RequestParam(name = "sex") String sex,
                                @RequestParam(name = "job") String job,
                                @RequestParam(name = "address") String address,
                                @RequestParam(name = "phone") String phone){

        QueryWrapper<Emp> empQueryWrapper = new QueryWrapper<>();
        empQueryWrapper.eq("empName",empName);
        Emp emp = empMapper.selectOne(empQueryWrapper);

        // 判断目标的empName在数据库中是否存在
        if (emp!=null){
            // 如果存在，则不允许使用当前用户名
            modelAndView.setViewName("/empAdd");
            modelAndView.addObject("msg","用户名已存在,请重试");
            modelAndView.addObject("empName",empName);
            modelAndView.addObject("realName",realName);
            modelAndView.addObject("password",password);
            modelAndView.addObject("sex",sex);
            modelAndView.addObject("job",job);
            modelAndView.addObject("address",address);
            modelAndView.addObject("phone",phone);
            return modelAndView;
        }

        // 用户名不存在，继续进行下一步操作
        Emp addEmp = new Emp();
        addEmp.setEmpName(empName);
        addEmp.setRealName(realName);
        addEmp.setPassword(password);
        addEmp.setSex(sex);
        addEmp.setJob(job);
        addEmp.setAddress(address);
        addEmp.setPhone(phone);

        int insert = empMapper.insert(addEmp);

        // 是否修改成功
        if (insert>0){
            // 成功
            modelAndView.setViewName("redirect:/emp/1");
            modelAndView.addObject("msg","添加成功！");
            return modelAndView;
        }
        // 失败
        modelAndView.setViewName("/empAdd");
        modelAndView.addObject("msg","添加失败");
        return modelAndView;

    }

    /**
     * 员工修改跳转
     * @param empID 员工ID
     * @param modelAndView modelAndView
     * @return empUpdate.html视图
     */
    @RequestMapping("/edit/{empID}")
    public ModelAndView empEdit(@PathVariable("empID") Long empID,ModelAndView modelAndView){
        modelAndView.setViewName("empUpdate");
        Emp emp = empMapper.selectById(empID);
        modelAndView.addObject("emp",emp);
        return modelAndView;
    }

    /**
     * 员工更新API
     * @param modelAndView modelAndView
     * @param empID 员工ID
     * @param realName 这是姓名
     * @param sex 性别
     * @param job 职位
     * @param address 地址
     * @param phone 联系方式
     * @param behavior 发送当前请求的来源，是否为员工自己
     *                 "yourself":员工自己的请求
     *                         "":来自管理的请求
     * @return 如果修改成功，请求来自员工本身：defaultIndex.html
     *                     请求来自管理员：重定向到redirect:/emp/1
     *
     *         如果修改失败，请求来自员工本身：重定向到 redirect:/emp/center"+empID
     *                     请求来自管理员：empUpdate.html
     */
    @RequestMapping("/update")
    public ModelAndView empUpdate(ModelAndView modelAndView,
                                  @RequestParam(name = "empID",required = true) Long empID,
                                  @RequestParam(name = "realName", required = true) String realName,
                                  @RequestParam(name = "sex", required = true) String sex,
                                  @RequestParam(name = "job", required = true) String job,
                                  @RequestParam(name = "address", required = true) String address,
                                  @RequestParam(name = "phone", required = true) String phone,
                                  @RequestParam(name= "behavior",defaultValue = "")String behavior){
        Emp emp = new Emp();
        emp.setEmpID(empID);
        emp.setRealName(realName);
        emp.setSex(sex);
        emp.setAddress(address);
        emp.setPhone(phone);
        emp.setJob(job);
        int update = empMapper.updateById(emp);

        // 如果修改成功
        if (update>=1){
            if ("yourself".equals(behavior)){
                // 请求来用户本身
                // 跳转回默认页面
                modelAndView.setViewName("defaultIndex");
                return modelAndView;
            }
            // 请求来自管理员
            // 重定向回所有员工分页显示页面
            modelAndView.setViewName("redirect:/emp/1");
            return modelAndView;
        }

        // 如果修改失败
        if ("yourself".equals(behavior)){
            // 请求来自用户本身
            // 重定向回员工个人中心
            String url = "redirect:/emp/center"+empID;
            modelAndView.setViewName(url);
        }
        else {
            // 请求来自管理员
            // 重定向回管理员修改员工信息界面
            modelAndView.setViewName("empUpdate");
        }
        modelAndView.addObject("emp",empMapper.selectById(empID));
        modelAndView.addObject("msg","修改失败！");
        return modelAndView;
    }

    /**
     * 员工删除API
     * @param modelAndView modelAndView
     * @param empID 员工ID
     * @param attr 用户重定向后，数据传递
     * @return 重定向到redirect:/emp/1
     */
    @RequestMapping("/delete/{empID}")
    public ModelAndView empDelete(ModelAndView modelAndView,
                                  @PathVariable("empID") Long empID,
                                  RedirectAttributes attr){

        // 由于选课表或者租赁表的其中的外键都为userID
        // 所以在删除时候可能出现违反唯一约束的异常
        try{
            empMapper.deleteById(empID);
        }catch (Exception e){
            Throwable cause = e.getCause();
            // 判断是否为’违反唯一约束‘异常
            if (cause instanceof SQLIntegrityConstraintViolationException){
                attr.addFlashAttribute("msg","删除失败，可能该用户的服务还未结束，可能已选课或租赁储物柜！");
                modelAndView.setViewName("redirect:/emp/1");
                return modelAndView;
            }
        }

        modelAndView.setViewName("redirect:/emp/1");
        return modelAndView;
    }

    /**
     * 员工个人中心跳转
     * @param empID 用户ID
     * @param modelAndView modelAndView
     * @return empCenter.html视图
     */
    @RequestMapping("/center/{empID}")
    public ModelAndView empCenter(@PathVariable("empID") Long empID, ModelAndView modelAndView){

        Emp emp = empMapper.selectById(empID);
        modelAndView.setViewName("empCenter");
        modelAndView.addObject("emp",emp);
        return modelAndView;
    }

    /**
     * 员工管理自己的课程,显示自己教授的所有课程
     * @param modelAndView modelAndView
     * @param empID 用户ID
     * @param pageNum 分页页码
     * @return empCourseManager.html
     */
    @RequestMapping("/course/{empID}/{pageNum}")
    public ModelAndView empCourse(ModelAndView modelAndView,
                                  @PathVariable("empID") String empID,
                                  @PathVariable("pageNum") Integer pageNum){

        Page<CourseEmpVO> page = new Page<>(pageNum,10);

        QueryWrapper<CourseEmpVO> courseEmpVOQueryWrapper = new QueryWrapper<>();


        courseEmpVOQueryWrapper.eq("empID",empID);

        List<CourseEmpVO> courseInfo = courseMapper.getCourseInfoByConditional(page, courseEmpVOQueryWrapper);

        if (page.getTotal()>0){
            modelAndView.setViewName("empCourseManager");
            for (CourseEmpVO courseEmpVO : courseInfo) {
                courseEmpVO.setCourseIDtoString(courseEmpVO.getCourseID().toString());
            }
            modelAndView.addObject("courseInfo", courseInfo);
            modelAndView.addObject("pageCurrent", Math.toIntExact(page.getCurrent()));
            modelAndView.addObject("pages", Math.toIntExact(page.getPages()));
            modelAndView.addObject("Total", Math.toIntExact(page.getTotal()));
            modelAndView.addObject("empID", empID);

            // 是否为条件查询的flag
            modelAndView.addObject("flag","conditional");
            System.out.println("当前页"+page.getCurrent());
        } else {
            // 没有查询到数据 或者查询错误
            modelAndView.setViewName("empCourseManager");
        }

        return modelAndView;
    }

}
