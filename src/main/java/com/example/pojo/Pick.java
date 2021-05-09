package com.example.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("Pick")
public class Pick {

    @TableId
    private Long userID;
    private Long courseID;

    /*自动填充，选课的时间*/
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
