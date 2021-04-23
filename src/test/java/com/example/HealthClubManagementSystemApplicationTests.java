package com.example;

import com.example.mapper.UserMapper;
import com.example.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HealthClubManagementSystemApplicationTests {

    @Autowired
    private UserMapper userMapper;


    @Test
    void insertTest() {
        User user = new User();
        user.setUserName("MrLQQ");
        user.setPhone("17640408843");
        user.setSex("男");
        user.setUserIdent("非会员");
        int result = userMapper.insert(user);
        System.out.println(result);
        System.out.println(user);
    }

    @Test
    void updateTest(){
        User user = new User();
        user.setUserID(1385610107157155841L);
        user.setUserName("MrLQQ");
        user.setPhone("17640408843");
        user.setSex("女");
        user.setUserIdent("会员");
        userMapper.updateById(user);
        System.out.println(user);
    }

}
