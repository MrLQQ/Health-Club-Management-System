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

/**
 * 课程Controller<br>
 * 用于处理所有关于课程的请求与业务。
 * @author LQQ
 */
@Controller
@RequestMapping("/course")
public class CourseController {

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    EmpMapper empMapper;

    @Autowired
    UserMapper userMapper;


    /**
     * 课程管理页面,分页显示
     * @param pageNum 分页页码
     * @param modelAndView modelAndView
     * @return courseManager.html视图
     */
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

    /**
     * 课程条件管理页面,多表联合查询、条件查询
     * @param modelAndView modelAndView
     * @param pageNum 分页页码
     * @param courseID 课程ID
     * @param courseName 课程名字
     * @param empName 课任教练
     * @param attr RedirectAttributes，用于视图重定向后，传递数据
     * @return 条件查询成功：courseManager.html视图<br/>
     *                失败：重定向到redirect:/course/1
     */
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

        //如果存在条件查询，讲条件放入QueryWrapper<CourseEmpVO>条件构造器中
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

        // 使用自定义的getCourseInfoByConditional方法，配置条件器，分页出查询数据
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

    /**
     * 课程修改页面跳转业务。
     * @param courseID 课程ID
     * @param behavior 用于判断发出当前请求人的身份，"empself"：员工本人发出的修改请求。"":管理员发出的请求
     * @param modelAndView modelAndView
     * @return courseUpdate.html视图
     */
    @RequestMapping("/edit/{courseID}")
    public ModelAndView courseEdit(@PathVariable("courseID") Long courseID,
                                   @RequestParam(value = "behavior",defaultValue = "") String behavior,
                                   ModelAndView modelAndView){
        // 设置视图
        modelAndView.setViewName("courseUpdate");
        Course course = courseMapper.selectById(courseID);
        QueryWrapper<Emp> empQueryWrapper =new QueryWrapper<>();
        empQueryWrapper.select("empName")
                .eq("empID",course.getEmpID());
        Emp emp = empMapper.selectOne(empQueryWrapper);

        // 判断当前请求的发出者，是否是员工
        if (behavior.equals("empself")){
            // 如果是员工发出的修改请求，则在跳转课程修改页面是，附带behavior变量。
            modelAndView.addObject("behavior",behavior);
        }
        modelAndView.addObject("course",course);
        modelAndView.addObject("empName",emp.getEmpName());

        return modelAndView;
    }

    /**
     * 课程修改请求
     * @param modelAndView modelAndView
     * @param courseID 课程ID
     * @param courseName 课程名字
     * @param empName 任课教练名字
     * @param time 上课时间
     * @param residue 课容量
     * @param behavior 请求行为的发出者
     * @return modelAndView
     *         如果要修改的emp不存在，则跳转到：courseUpdate.html
     *         如果要修改的emp存在，请求发出者是emp，则重定向到：redirect:/emp/course/"+emp.getEmpID()+"/1"
     *                           请求发出或者不是emp，则重定向到：redirect:/course/1
     */
    @RequestMapping("/update")
    public ModelAndView userUpdate(ModelAndView modelAndView,
                                   @RequestParam(name = "courseID",required = true) Long courseID,
                                   @RequestParam(name = "courseName", required = true) String courseName,
                                   @RequestParam(name = "empName", required = true) String empName,
                                   @RequestParam(name = "time", required = true) String time,
                                   @RequestParam(name = "residue", required = true) Integer residue,
                                   @RequestParam(value = "behavior", defaultValue = "") String behavior){

        Course course = new Course();
        course.setCourseID(courseID);
        course.setCourseName(courseName);
        course.setTime(time);
        course.setResidue(residue);

        QueryWrapper<Emp> empQueryWrapper = new QueryWrapper<>();
        empQueryWrapper.eq("empName",empName);
        Emp emp = empMapper.selectOne(empQueryWrapper);

        // 判断当前修改的empName在数据库中是否存在
        if (emp!=null){
            // 如果存在，则进行下一步修改。
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
        // 目标empName在数据库中不存在
        modelAndView.setViewName("courseUpdate");
        modelAndView.addObject("empName",empName);
        modelAndView.addObject("course",course);
        if (behavior.equals("empself")){
            modelAndView.addObject("behavior",behavior);
        }
        modelAndView.addObject("msg","修改失败,当前教练不存在！");
        return modelAndView;
    }

    /**
     * 课程删除业务
     * @param modelAndView modelAndView
     * @param courseID 目标课程ID
     * @param behavior 发出删除行为的身份
     * @param attr RedirectAttributes，页面重定向后，传递数据
     * @return ModelAndView
     *          删除失败：redirect:/course/1
     *          删除成功，行为发出者为emp：redirect:/emp/course/"+course.getEmpID()+"/1"
     *                  行为发出者部位emp：redirect:/course/1
     */
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
                // 删除失败
                attr.addFlashAttribute("msg","删除失败，可能该用户的服务还未结束，可能已选课或租赁储物柜！");
                modelAndView.setViewName("redirect:/course/1");
                return modelAndView;
            }
        }

        // 删除成功，判断行为发出者是否为emp
        if (behavior.equals("empself")){
            //emp发出的请求
            modelAndView.setViewName("redirect:/emp/course/"+course.getEmpID()+"/1");
        }else {
            // 管理员发出的请求
            modelAndView.setViewName("redirect:/course/1");
        }
        return modelAndView;
    }

    /**
     * 课程添加页面跳转
     * @param empID 员工ID
     * @param modelAndView modelAndView
     * @return courseAdd.html视图
     */
    @RequestMapping("/courseAdd/{empID}")
    public ModelAndView courseAdd(@PathVariable("empID") Long empID,ModelAndView modelAndView){
        Emp emp = empMapper.selectById(empID);
        modelAndView.addObject("emp",emp);
        modelAndView.setViewName("courseAdd");
        return modelAndView;
    }

    /**
     * 添加课程业务
     * @param modelAndView modelAndView
     * @param courseName 课程名字
     * @param empID 任课教练ID
     * @param time 上课时间
     * @param residue 课容量
     * @return redirect:/emp/course/"+empID+"/1"
     */
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

    /**
     * 显示所有课程,用于之后用户进行选课
     * @param modelAndView modelAndView
     * @param userID 用户ID
     * @return userCourse.html
     */
    @RequestMapping("/emp/showAll")
    public ModelAndView showAll(ModelAndView modelAndView,
                                @RequestParam("userID") Long userID){

        List<CourseEmpVO> courseInfo = courseMapper.getCourseInfo(null);
        modelAndView.setViewName("userCourse");
        modelAndView.addObject("courseInfo",courseInfo);
        User user = userMapper.selectById(userID);
        modelAndView.addObject("user",user);

        return modelAndView;
    }

}
