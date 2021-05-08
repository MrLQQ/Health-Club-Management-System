package com.example.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("User")
public class User {

    @TableId(type = IdType.ID_WORKER)
    private Long userID;

    /*自动填充，user创建时间*/
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    private String userName;
    private String realName;
    private String password;
    private String userIdent;
    private String sex;
    private String phone;
}
