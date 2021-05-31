package com.example.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pojo.Course;
import com.example.pojo.CourseEmpVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author LQQ
 */
@Repository
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 查询课程详细信息
     * Course表和Emp表进行联合查询
     * @param page Page<CourseEmpVO>分页工具
     * @return List<CourseEmpVO>
     */
    @Select("SELECT Course.*,(select EmpName from Emp where Course.EmpID=Emp.EmpID) as EmpName FROM Course")
    List<CourseEmpVO> getCourseInfo(Page<CourseEmpVO> page);

    /**
     * 条件查询课程详细信息
     * Course表和Emp表进行联合条件查询
     * @param page Page<CourseEmpVO>分页工具
     * @param courseEmpVOWrapper Wrapper<CourseEmpVO>条件构造器
     * @return List<CourseEmpVO>
     */
    @Select("SELECT Course.*,(select EmpName from Emp where Course.EmpID=Emp.EmpID) as EmpName FROM Course ${ew.customSqlSegment}")
    List<CourseEmpVO> getCourseInfoByConditional(Page<CourseEmpVO> page,@Param(Constants.WRAPPER) Wrapper<CourseEmpVO> courseEmpVOWrapper);
}
