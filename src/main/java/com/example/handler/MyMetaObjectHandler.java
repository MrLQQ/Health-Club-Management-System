package com.example.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * 自动填充数据表中某些日期字段<br>
 * 如：用户的创建时间，职工的入职时间。
 * @author LQQ
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {


    /**
     * 插入时的填充策略
     * @param metaObject MetaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start inset fill...");
        this.setFieldValByName("createTime",new Date(),metaObject);
    }


    /**
     * 修改时的填充策略
     * @param metaObject MetaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill...");
        this.setFieldValByName("OrderTime",new Date(),metaObject);
    }
}
