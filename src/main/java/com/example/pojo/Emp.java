package com.example.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;


/**
 * 员工实体类
 * @author LQQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("Emp")
public class Emp{

    @TableId(type = IdType.ID_WORKER)
    private Long empID;

    /**
     * 自动填充，emp创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    private String empName;
    private String realName;
    private String password;
    private String job;
    private String sex;
    private String address;
    private String phone;
}
