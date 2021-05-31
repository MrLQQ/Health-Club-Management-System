package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 选课-课程-用户关系实体类
 * @author LQQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PickCourseUserVO {

    private Long userID;
    private String  userIDtoString;
    private Long courseID;
    private String courseIDtoString;
    private String userName;
    private Date createTime;
    private String createTimeForString;
    private String courseName;
}
