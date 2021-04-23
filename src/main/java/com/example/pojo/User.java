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

    // 字段添加自动填充
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private String userName;
    private String userIdent;
    private String sex;
    private String Phone;
}
