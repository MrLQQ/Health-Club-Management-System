package com.example.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程实体类
 * @author LQQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("Course")
public class Course {

    @TableId(type = IdType.ID_WORKER)
    private Long courseID;
    private String courseName;
    private Long empID;
    private String time;
    private Integer residue;
}
