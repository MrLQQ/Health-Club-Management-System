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

@Controller
@RequestMapping("/pick")
public class PickController {

    @Autowired
    PickMapper pickMapper;

    @Autowired
    CourseMapper courseMapper;

    @RequestMapping("/{userID}/{courseID}")
    public ModelAndView PickCourse(ModelAndView modelAndView,
                                   @PathVariable("userID") Long userID,
                                   @PathVariable("courseID") Long courseID,
                                   RedirectAttributes attr){

        modelAndView.setViewName("redirect:/course/emp/showAll?userID="+userID);

        QueryWrapper<Pick> pickQueryWrapper = new QueryWrapper<>();
        pickQueryWrapper.eq("userID",userID)
                .eq("courseID",courseID);
        Pick pickInData = pickMapper.selectOne(pickQueryWrapper);

        if (pickInData!=null){
            attr.addFlashAttribute("msg","不允许重复选择！");
        } else {
            Pick pick = new Pick();
            pick.setCourseID(courseID);
            pick.setUserID(userID);

            int insert = pickMapper.insert(pick);
            if (insert>0){
                Course course = courseMapper.selectById(courseID);
                course.setResidue(course.getResidue()-1);
                courseMapper.updateById(course);
            }else {
                attr.addFlashAttribute("msg","选课失败");
            }
        }

        return modelAndView;
    }

    @RequestMapping("courseList/{ID}")
    public ModelAndView PickCourseList(@PathVariable("ID") Long ID, ModelAndView modelAndView, @RequestParam("type") String type){
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

    @ResponseBody
    @RequestMapping("/delete/{courseID}/{userID}")
    public void deletePick(@PathVariable("courseID") Long courseID,
                           @PathVariable("userID") Long userID){

        QueryWrapper<Pick> pickQueryWrapper = new QueryWrapper<>();
        pickQueryWrapper.eq("courseID",courseID)
                        .eq("userID",userID);
        pickMapper.delete(pickQueryWrapper);

        Course course = courseMapper.selectById(courseID);
        course.setResidue(course.getResidue()+1);

        courseMapper.updateById(course);
    }
}
