package com.example.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 储物柜实体类
 * @author LQQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("Box")
public class Box{

    @TableId(type = IdType.NONE)
    private String boxID;
    private Long userID;
    private Date startTime;
    private Date endTime;

}
