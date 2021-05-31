package com.example.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.example.pojo.Pick;
import com.example.pojo.PickCourseUserVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @author LQQ
 */
@Repository
public interface PickMapper extends BaseMapper<Pick>{

    /**
     * 条件查询选课详细信息
     * Pick表、User表、Course表进行联合条件查询
     * @param pickCourseUserVOWrapper Wrapper<PickCourseUserVO>条件构造器
     * @return List<PickCourseUserVO>
     */
    @Select("SELECT Pick.*,(select userName from User where Pick.userID=User.userID) as userName," +
            "(select courseName from Course where Pick.courseID=Course.courseID) as courseName " +
            "FROM Pick ${ew.customSqlSegment}")
    List<PickCourseUserVO> getPickInfo(@Param(Constants.WRAPPER) Wrapper<PickCourseUserVO> pickCourseUserVOWrapper);
}