package com.example.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员实体类
 * @author LQQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("Admin")
public class Admin {
    @TableId(type = IdType.NONE)
    private String adminName;
    private String adminPassword;
}
