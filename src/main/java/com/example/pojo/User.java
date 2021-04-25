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
    private Date createTime;
    private String userName;
    private String userIdent;
    private String sex;
    private String Phone;
}
