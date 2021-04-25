package com.example.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("Emp")
public class Emp {

    @TableId(type = IdType.ID_WORKER)
    private Long empID;
    private Date createTime;
    private String empName;
    private String job;
    private String sex;
    private String address;
    private String phone;
}
