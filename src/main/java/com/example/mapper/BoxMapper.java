package com.example.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pojo.Box;
import com.example.pojo.BoxUserVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author LQQ
 */
@Repository
public interface BoxMapper extends BaseMapper<Box> {

    /**
     * 查询储物柜的租赁信息
     * Box表和User表进行联合查询
     * @param page Page<BoxUserVO> 分页工具
     * @return List<BoxUserVO>
     */
    @Select("SELECT Box.*,(select userName from User where Box.userID=User.userID) as userName FROM Box")
    List<BoxUserVO> getBoxInfo(Page<BoxUserVO> page);

    /**
     * 条件查询储物柜的租赁信息
     * Box表和User表进行联合条件查询
     * @param page Page<BoxUserVO> 分页工具
     * @param boxUserVOWrapper Wrapper<BoxUserVO>条件构造器
     * @return List<BoxUserVO>
     */
    @Select("SELECT Box.*,(select userName from User where Box.userID=User.userID) as userName FROM Box ${ew.customSqlSegment}")
    List<BoxUserVO> getBoxInfoByConditional(Page<BoxUserVO> page, @Param(Constants.WRAPPER)Wrapper<BoxUserVO> boxUserVOWrapper);
}
