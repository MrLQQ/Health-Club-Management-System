package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程-员工关系实体类
 * @author LQQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseEmpVO {

    private Long courseID;
    private String courseName;
    private String courseIDtoString;
    private Long empID;
    private String time;
    private Integer residue;
    private String empName;
}
