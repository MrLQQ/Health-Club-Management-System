package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 储物柜-用户关系实体类
 * @author LQQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoxUserVO {
    private String boxID;
    private Long userID;
    private Date startTime;
    private Date endTime;
    private String userName;
}
