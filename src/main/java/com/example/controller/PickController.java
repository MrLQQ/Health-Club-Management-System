package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.CourseMapper;
import com.example.mapper.PickMapper;
import com.example.pojo.Course;
import com.example.pojo.Pick;
import com.example.pojo.PickCourseUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 选课Controller
 * @author LQQ
 */
@Controller
@RequestMapping("/pick")
public class PickController {

    @Autowired
    PickMapper pickMapper;

    @Autowired
    CourseMapper courseMapper;

    /**
     * 用户选课业务
     * @param modelAndView modelAndView
     * @param userID 用户ID
     * @param courseID 课程ID
     * @param attr 用于重定向后传递数据
     * @return 重定向到"redirect:/course/emp/showAll?userID="+userID
     */
    @RequestMapping("/{userID}/{courseID}")
    public ModelAndView PickCourse(ModelAndView modelAndView,
                                   @PathVariable("userID") Long userID,
                                   @PathVariable("courseID") Long courseID,
                                   RedirectAttributes attr){

        // 重定向到，显示所有课程的页面
        modelAndView.setViewName("redirect:/course/emp/showAll?userID="+userID);

        QueryWrapper<Pick> pickQueryWrapper = new QueryWrapper<>();
        pickQueryWrapper.eq("userID",userID)
                .eq("courseID",courseID);
        Pick pickInData = pickMapper.selectOne(pickQueryWrapper);

        // 判断是否重复选择
        if (pickInData!=null){
            // 目标课程，当前用户已选择
            attr.addFlashAttribute("msg","不允许重复选择！");
        } else {
            // 未选择
            Pick pick = new Pick();
            pick.setCourseID(courseID);
            pick.setUserID(userID);

            int insert = pickMapper.insert(pick);
            if (insert>0){
                // 插入成功即选课成功
                Course course = courseMapper.selectById(courseID);

                // 选课成功后，课程的课容量-1
                course.setResidue(course.getResidue()-1);
                courseMapper.updateById(course);
            }else {
                // 选课失败
                attr.addFlashAttribute("msg","选课失败");
            }
        }

        return modelAndView;
    }

    /**
     * 显示课程列表，根据传入的ID和type，可以在Pick表中
     * 可以查询目标教练所管理的课程
     * 或者查询目标用户的所选的课程
     * @param ID 目标ID
     * @param modelAndView modelAndView
     * @param type ID类型
     *             "courseID":课程ID
     *               "userID":用户ID
     * @return courseList.html
     */
    @RequestMapping("courseList/{ID}")
    public ModelAndView pickCourseList(@PathVariable("ID") Long ID, ModelAndView modelAndView, @RequestParam("type") String type){
        QueryWrapper<PickCourseUserVO> pickCourseUserVOQueryWrapper = new QueryWrapper<>();
        if (type.equals("courseID")){
            /*教练访问*/
            pickCourseUserVOQueryWrapper.eq("courseID",ID);
        }else if (type.equals("userID")){
            /*用户访问*/
            pickCourseUserVOQueryWrapper.eq("userID",ID);
        }

        List<PickCourseUserVO> pickInfo = pickMapper.getPickInfo(pickCourseUserVOQueryWrapper);
        for (PickCourseUserVO pickCourseUserVO : pickInfo) {
            System.out.println(pickCourseUserVO.getCreateTime());
            // 处理时间日期的显示格式
            pickCourseUserVO.setCreateTimeForString
                    (new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss" ).format(pickCourseUserVO.getCreateTime()));
            pickCourseUserVO.setCourseIDtoString(pickCourseUserVO.getCourseID().toString());
            pickCourseUserVO.setUserIDtoString(pickCourseUserVO.getUserID().toString());

            System.out.println(pickCourseUserVO.getCreateTimeForString());
        }
        modelAndView.addObject("pickInfo",pickInfo);
        modelAndView.addObject("type",type);
        modelAndView.setViewName("courseList");
        return modelAndView;
    }

    /**
     * 删除选课记录，退课业务
     * @param courseID 课程ID
     * @param userID 用户ID
     */
    @ResponseBody
    @RequestMapping("/delete/{courseID}/{userID}")
    public void deletePick(@PathVariable("courseID") Long courseID,
                           @PathVariable("userID") Long userID){

        QueryWrapper<Pick> pickQueryWrapper = new QueryWrapper<>();
        pickQueryWrapper.eq("courseID",courseID)
                        .eq("userID",userID);
        // 删除目标选课记录
        pickMapper.delete(pickQueryWrapper);

        // 查询目标课程，在删除选课记录后，改课课容量+1
        Course course = courseMapper.selectById(courseID);
        course.setResidue(course.getResidue()+1);
        courseMapper.updateById(course);
    }
}
