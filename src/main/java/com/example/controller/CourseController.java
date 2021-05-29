package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.CourseMapper;
import com.example.mapper.EmpMapper;
import com.example.mapper.UserMapper;
import com.example.pojo.Course;
import com.example.pojo.CourseEmpVO;
import com.example.pojo.Emp;
import com.example.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController {

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    EmpMapper empMapper;

    @Autowired
    UserMapper userMapper;


    // 课程管理页面
    @RequestMapping("{pageNum}")
    public ModelAndView courseManager(@PathVariable("pageNum") Integer pageNum, ModelAndView modelAndView){

        Page<CourseEmpVO> page =new Page<>(pageNum,10);
        // 使用自定的多表联合查询
        List<CourseEmpVO> courseInfo = courseMapper.getCourseInfo(page);
        if (page.getTotal() > 0) {
            modelAndView.setViewName("courseManager");
            modelAndView.addObject("courseInfo", courseInfo);
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

    // 课程条件管理页面
    @RequestMapping("/conditional")
    public ModelAndView courseManagerByConditional(ModelAndView modelAndView,
                                                   @RequestParam(name = "pageNum") Integer pageNum,
                                                   @RequestParam(name = "courseID", defaultValue = "") String courseID,
                                                   @RequestParam(name = "courseName", defaultValue = "") String courseName,
                                                   @RequestParam(name = "empName", defaultValue = "") String empName,
                                                   RedirectAttributes attr){

        Page<CourseEmpVO> page = new Page<>(pageNum,10);

        QueryWrapper<CourseEmpVO> courseEmpVOQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Emp> empQueryWrapper = new QueryWrapper<>();

        if (!courseID.equals("")) {
            courseEmpVOQueryWrapper.eq("courseID",courseID);
        }
        if (!courseName.equals("")) {
            courseEmpVOQueryWrapper.eq("courseName",courseName);
        }
        if (!empName.equals("")){
            empQueryWrapper.select("empID").eq("empName",empName);
            Emp emp = empMapper.selectOne(empQueryWrapper);
            courseEmpVOQueryWrapper.eq("empID",emp.getEmpID());
        }

        List<CourseEmpVO> courseInfo = courseMapper.getCourseInfoByConditional(page, courseEmpVOQueryWrapper);

        if (page.getTotal()>0){
            modelAndView.setViewName("courseManager");
            modelAndView.addObject("courseInfo", courseInfo);
            modelAndView.addObject("pageCurrent", Math.toIntExact(page.getCurrent()));
            modelAndView.addObject("pages", Math.toIntExact(page.getPages()));
            modelAndView.addObject("Total", Math.toIntExact(page.getTotal()));
            modelAndView.addObject("courseID",courseID);
            modelAndView.addObject("courseName",courseName);
            modelAndView.addObject("empName",empName);

            // 是否为条件查询的flag
            modelAndView.addObject("flag","conditional");
            System.out.println("当前页"+page.getCurrent());
        } else {
            // 没有查询到数据 或者查询错误
            // 重定向到默认查询页
            System.out.println("课程条件查找触发错误");
            modelAndView.setViewName("redirect:/course/1");
            attr.addFlashAttribute("msg","未查找到有效数据，请修改条件后重新查询");
        }

        return modelAndView;
    }

    @RequestMapping("/edit/{courseID}")
    public ModelAndView courseEdit(@PathVariable("courseID") Long courseID,@RequestParam("behavior") String behavior,ModelAndView modelAndView){
        modelAndView.setViewName("courseUpdate");
        Course course = courseMapper.selectById(courseID);
        QueryWrapper<Emp> empQueryWrapper =new QueryWrapper<>();
        empQueryWrapper.select("empName")
                .eq("empID",course.getEmpID());
        Emp emp = empMapper.selectOne(empQueryWrapper);

        if (behavior.equals("empself")){
            modelAndView.addObject("behavior",behavior);
        }
        modelAndView.addObject("course",course);
        modelAndView.addObject("empName",emp.getEmpName());

        return modelAndView;
    }

    @RequestMapping("/update")
    public ModelAndView userUpdate(ModelAndView modelAndView,
                                   @RequestParam(name = "courseID",required = true) Long courseID,
                                   @RequestParam(name = "courseName", required = true) String courseName,
                                   @RequestParam(name = "empName", required = true) String empName,
                                   @RequestParam(name = "time", required = true) String time,
                                   @RequestParam(name = "residue", required = true) Integer residue,
                                   @RequestParam("behavior") String behavior){

        Course course = new Course();
        course.setCourseID(courseID);
        course.setCourseName(courseName);
        course.setTime(time);
        course.setResidue(residue);

        QueryWrapper<Emp> empQueryWrapper = new QueryWrapper<>();
        empQueryWrapper.eq("empName",empName);
        Emp emp = empMapper.selectOne(empQueryWrapper);
        if (emp!=null){
            course.setEmpID(emp.getEmpID());
            int update = courseMapper.updateById(course);
            if (update>=1){
                // 判断是否是emp自己的修改请求
                if (behavior.equals("empself")){
                    modelAndView.setViewName("redirect:/emp/course/"+emp.getEmpID()+"/1");
                    return modelAndView;
                }
                // 不是来自emp的请求
                modelAndView.setViewName("redirect:/course/1");
                return modelAndView;
            }
        }
        modelAndView.setViewName("courseUpdate");
        modelAndView.addObject("empName",empName);
        modelAndView.addObject("course",course);
        if (behavior.equals("empself")){
            modelAndView.addObject("behavior",behavior);
        }
        modelAndView.addObject("msg","修改失败,当前教练不存在！");
        return modelAndView;
    }

    @RequestMapping("/delete/{courseID}")
    public ModelAndView userDelete(ModelAndView modelAndView,
                                   @PathVariable("courseID") Long courseID,
                                   @RequestParam("behavior") String behavior,
                                   RedirectAttributes attr){

        Course course = courseMapper.selectById(courseID);
        // 由于选课表或者租赁表的其中的外键都为userID
        // 所以在删除时候可能出现违反唯一约束的异常
        try{
            courseMapper.deleteById(courseID);
        }catch (Exception e){
            Throwable cause = e.getCause();
            // 判断是否为’违反唯一约束‘异常
            if (cause instanceof SQLIntegrityConstraintViolationException){
                attr.addFlashAttribute("msg","删除失败，可能该用户的服务还未结束，可能已选课或租赁储物柜！");
                modelAndView.setViewName("redirect:/course/1");
                return modelAndView;
            }
        }

        if (behavior.equals("empself")){
            modelAndView.setViewName("redirect:/emp/course/"+course.getEmpID()+"/1");
        }else {
            modelAndView.setViewName("redirect:/course/1");
        }
        return modelAndView;
    }

    @RequestMapping("/courseAdd/{empID}")
    public ModelAndView courseAdd(@PathVariable("empID") Long empID,ModelAndView modelAndView){
        Emp emp = empMapper.selectById(empID);
        modelAndView.addObject("emp",emp);
        modelAndView.setViewName("courseAdd");
        return modelAndView;
    }

    @RequestMapping("/save")
    public ModelAndView saveCourse(ModelAndView modelAndView,
                                   @RequestParam("courseName") String courseName,
                                   @RequestParam("empID") Long empID,
                                   @RequestParam("time") String time,
                                   @RequestParam("residue") Integer residue){

        Course course = new Course();
        course.setCourseName(courseName);
        course.setEmpID(empID);
        course.setTime(time);
        course.setResidue(residue);

        int insert = courseMapper.insert(course);
        modelAndView.addObject("msg","添加成功");
        modelAndView.setViewName("redirect:/emp/course/"+empID+"/1");
        return modelAndView;
    }

    @RequestMapping("/emp/showAll")
    public ModelAndView showAll(ModelAndView modelAndView,
                                @RequestParam("userID") Long userID){

        List<CourseEmpVO> courseInfo = courseMapper.getCourseInfo();
        modelAndView.setViewName("userCourse");
        modelAndView.addObject("courseInfo",courseInfo);
        User user = userMapper.selectById(userID);
        modelAndView.addObject("user",user);

        return modelAndView;
    }

}
