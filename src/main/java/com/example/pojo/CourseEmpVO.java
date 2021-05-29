package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
