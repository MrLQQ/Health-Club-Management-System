package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.CourseMapper;
import com.example.mapper.EmpMapper;
import com.example.mapper.UserMapper;
import com.example.pojo.Course;
import com.example.pojo.Emp;
import com.example.pojo.User;
import com.example.util.OSSClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * 用户Controller，拦截和处理关于用户的请求或业务
 * @author LQQ
 */
@Controller
@RequestMapping("/user")
public class UserController{

    @Autowired
    OSSClientUtil ossClientUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private CourseMapper courseMapper;

    /**
     * 用户的登录业务，可以处理用户身份登录，以及员工身份登录
     * @param mv ModelAndView
     * @param userName 用户名
     * @param password 密码
     * @param attr 重定向后传递数据
     * @return 用户登录：userIndex.html
     *         员工登录：empHomepage.html
     *         登录失败：重定向到"redirect:/index"
     */
    @PostMapping("/login")
    public ModelAndView login(ModelAndView mv,
                              @RequestParam(name = "userName") String userName,
                              @RequestParam(name = "password") String password, RedirectAttributes attr){

        // 先将当前userName在User表中进行查找
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userName",userName);
        User user = userMapper.selectOne(userQueryWrapper);

        // 如果User表中存在当前user并且密码相同
        if (user!=null && password.equals(user.getPassword())){
            // 获取头像url
            String image=user.getImage();
            mv.setViewName("userIndex");
            mv.addObject("user",user);
            mv.addObject("image",image);

            // 获取所有课程
            List<Course> courses = courseMapper.selectList(null);
            mv.addObject("courses",courses);

            return mv;
        }
        // 在User表中没有查询到目标用户后，则在Emp员工表中再次进行查找比对
        else{
            QueryWrapper<Emp> empQueryWrapper = new QueryWrapper<>();
            empQueryWrapper.eq("empName",userName);
            Emp emp = empMapper.selectOne(empQueryWrapper);

            // 如果在Emp表中查询到目标信息，并且密码也相同
            if (emp!=null && emp.getPassword().equals(password)){
                // 跳转到empHomepage.html
                mv.setViewName("empHomepage");
                mv.addObject("emp", emp);
                return mv;
            }
            // 在User表和Emp表中均为查询到目标用户，则用户不存在
            mv.setViewName("redirect:/index");
            attr.addFlashAttribute("msg","登录失败,用户名或密码错误");
            return mv;
        }
    }

    /**
     * 用户注册业务
     * @param mv ModelAndView
     * @param userName 用户名
     * @param realName 真实姓名
     * @param password 密码
     * @param sex 性别
     * @param phone 联系方式
     * @param attr RedirectAttributes
     * @return 重定向到redirect:/index
     */
    @PostMapping("/registered")
    public ModelAndView registered(ModelAndView mv,
                                   @RequestParam(name = "userName") String userName,
                                   @RequestParam(name = "realName") String realName,
                                   @RequestParam(name = "passwordOne") String password,
                                   @RequestParam(name = "sex") String sex,
                                   @RequestParam(name = "phone") String phone,
                                   RedirectAttributes attr){
        User user = new User();
        user.setUserName(userName);
        user.setRealName(realName);
        user.setPassword(password);
        user.setSex(sex);
        user.setPhone(phone);
        user.setUserIdent("非会员");
        // 设置默认头像
        user.setImage("https://health-club-management-system.oss-cn-beijing.aliyuncs.com/image/0000000000000.png");

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

    /**
     * 用户个人信息
     * @param userName 用户名
     * @param map ModelMap
     * @return userCenter.html
     */
    @RequestMapping("/info")
    public String userInfo(@RequestParam(name="userName") String userName, ModelMap map){

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userName",userName);
        User user = userMapper.selectOne(userQueryWrapper);
        map.addAttribute("user",user);

        return "userCenter";
    }

    /**
     * 用户个人信息修改
     * @param file 文件，上传的用户头像
     * @param userID 用户ID
     * @param userName 用户名
     * @param password 真实姓名
     * @param sex 性别
     * @param phone 联系方式
     * @param map ModelMap
     * @return userCenter.html
     */
    @PostMapping("/userUpdate")
    public String userUpdate(@RequestParam(value = "image") MultipartFile file,
                             @RequestParam(name="userID",required = true) String userID,
                             @RequestParam(name="userName",required = true) String userName,
                             @RequestParam(name="password",required = true) String password,
                             @RequestParam(name="sex",required = true) String sex,
                             @RequestParam(name="phone",required = true) String phone,
                             ModelMap map
                             ){
        // 在数据库中查找出目标的user对象
        String msg="";
        User user = userMapper.selectById(userID);
        user.setUserName(userName);
        user.setPassword(password);
        user.setSex(sex);
        user.setPhone(phone);
        if (file != null || file.getSize() > 0){
            System.out.println("file's sizeof==》"+file.getSize());
            if (file.getSize() > 1024 * 1024 * 10){
                msg+="图片不能超过10MB!";
            }
            else {
                try {
                    user.setImage(ossClientUtil.updateHomeImage(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

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
                    msg+=" 用户名已存在，请重新修改!";
                    map.addAttribute("msg",msg);
                    map.addAttribute("user",userMapper.selectById(userID));
                    return "userCenter";
                }
            }
        }
        // 如果目标的userName在数据中不存在实体对象，也同样说明该userName在数据库中不存在重复项。
        userMapper.updateById(user);
        map.addAttribute("msg","修改成功");
        map.addAttribute("user",user);
        return "userCenter";
    }


    /**
     * 管理用户界面
     * @param pageNum 分页页码
     * @param modelAndView modelAndView
     * @return userManager.html
     */
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

    /**
     * 用户条件管理页面
     * @param modelAndView modelAndView
     * @param pageNum 分页页码
     * @param userID 用户ID
     * @param userName 用户名
     * @param realName 真实姓名
     * @param userIdent 用户身份
     * @param sex 性别
     * @param attr RedirectAttributes
     * @return 查询成功：userManager.html
     *         查询失败：重定向到redirect:/user/1
     */
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
        if (!"".equals(userID)) {
            userQueryWrapper.eq("userID",userID);
        }
        if (!"".equals(userName)) {
            userQueryWrapper.eq("userName",userName);
        }
        if (!"".equals(realName)) {
            userQueryWrapper.eq("realName",realName);
        }
        if (!"".equals(userIdent)) {
            userQueryWrapper.eq("userIdent",userIdent);
        }
        if (!"".equals(sex)) {
            userQueryWrapper.eq("sex",sex);
        }

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

    /**
     * 用户修改页面
     * @param userID 用户ID
     * @param modelAndView modelAndView
     * @return userUpdate.html
     */
    @RequestMapping("/edit/{userID}")
    public ModelAndView userEdit(@PathVariable("userID") Long userID,ModelAndView modelAndView){
        modelAndView.setViewName("userUpdate");
        User user = userMapper.selectById(userID);
        modelAndView.addObject("user",user);
        return modelAndView;
    }

    /**
     * 用户修改业务
     * @param modelAndView modelAndView
     * @param userID 用户ID
     * @param sex 性别
     * @param userIdent 用户身份
     * @param phone 联系方式
     * @return 修改成功：重定向到redirect:/user/1
     *         修改失败：userUpdate.html
     */
    @RequestMapping("/update")
    public ModelAndView userUpdate(ModelAndView modelAndView,
                                  @RequestParam(name = "userID") Long userID,
                                  @RequestParam(name = "sex") String sex,
                                  @RequestParam(name = "userIdent") String userIdent,
                                  @RequestParam(name = "phone") String phone){
        User user = new User();
        user.setUserID(userID);
        user.setSex(sex);
        user.setUserIdent(userIdent);
        user.setPhone(phone);
        int update = userMapper.updateById(user);
        if (update>=1){
            modelAndView.setViewName("redirect:/user/1");
            return modelAndView;
        }
        modelAndView.setViewName("userUpdate");
        modelAndView.addObject("user",userMapper.selectById(userID));
        modelAndView.addObject("msg","修改失败！");
        return modelAndView;
    }

    /**
     * 用户删除业务
     * @param modelAndView modelAndView
     * @param userID 用户ID
     * @param attr RedirectAttributes
     * @return 重定向到redirect:/user/1
     */
    @RequestMapping("/delete/{userID}")
    public ModelAndView userDelete(ModelAndView modelAndView,
                                  @PathVariable("userID") Long userID,
                                   RedirectAttributes attr){

        // 由于选课表或者租赁表的其中的外键都为userID
        // 所以在删除时候可能出现违反唯一约束的异常
        try{
            userMapper.deleteById(userID);
        }catch (Exception e){
            Throwable cause = e.getCause();
            // 判断是否为’违反唯一约束‘异常
            if (cause instanceof SQLIntegrityConstraintViolationException){
                attr.addFlashAttribute("msg","删除失败，可能该用户的服务还未结束，可能已选课或租赁储物柜！");
                modelAndView.setViewName("redirect:/user/1");
                return modelAndView;
            }
        }
        modelAndView.setViewName("redirect:/user/1");
        return modelAndView;
    }
}
