package com.example;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.*;
import com.example.pojo.Emp;
import com.example.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Random;

@SpringBootTest
class HealthClubManagementSystemApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private BoxMapper boxMapper;

    @Autowired
    private PickMapper pickMapper;

    @Autowired
    private AdminMapper adminMapper;



    @Test
    void addEmp() {
        String []first_name={"赵","钱","孙","李","周","吴","郑","王"};
        String []last_name={"老大","老二","老三","老四","老五"};

        String []first_address={"东城","西城","南城","北城"};
        String []last_address={"一号胡同","二号胡同","三号胡同","四号胡同","五号胡同"};

        for (int i = 0; i < 15; i++) {
            Emp emp = new Emp();
            emp.setEmpName("emp"+RandomUtil.randomNumbers(3));
            emp.setRealName(first_name[new Random().nextInt(8)]+last_name[new Random().nextInt(5)]);
            emp.setPassword("0000");
            emp.setJob(i%3==0?"保洁":"教练");
            emp.setSex(i%2==0?"男":"女");
            emp.setAddress(first_address[new Random().nextInt(4)]+last_address[new Random().nextInt(5)]);
            emp.setPhone(RandomUtil.randomNumbers(5)+RandomUtil.randomNumbers(6));
            int result = empMapper.insert(emp);
            System.out.println(result);
            System.out.println(emp);

        }
    }

    @Test
    void addUser(){
        String []first_name={"张","王","赵","李","郑","王","杨"};
        String []last_name={"小小","小怡","小尔","小飒","小思","小武","小陆"};

        String []first_address={"东城","西城","南城","北城"};
        String []last_address={"一号胡同","二号胡同","三号胡同","四号胡同","五号胡同"};

        for (int i = 0; i < 15; i++) {
            User user = new User();
            user.setUserName("user"+RandomUtil.randomNumbers(3));
            user.setRealName(first_name[new Random().nextInt(7)]+last_name[new Random().nextInt(7)]);
            user.setPassword("0000");
            user.setUserIdent(i%3==0?"非会员":"会员");
            user.setSex(i%2==0?"男":"女");
            user.setPhone(RandomUtil.randomNumbers(5)+RandomUtil.randomNumbers(6));
            int result = userMapper.insert(user);
            System.out.println(result);
            System.out.println(user);

        }
    }

    @Test
    void selectEmpIDTest(){
        QueryWrapper<Emp> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("empID")
                        .eq("job","教练");
        List<Map<String, Object>> maps = empMapper.selectMaps(userQueryWrapper);
        for (Map<String, Object> map : maps) {
            System.out.println(map.get("empID"));
        }
    }

   /* @Test
>>>>>>> c8eb31d (test)
    void addCourse(){
        ArrayList<Long> empIDs = new ArrayList<>();
        QueryWrapper<Emp> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("empID")
                        .eq("job","教练");
        List<Map<String, Object>> maps = empMapper.selectMaps(userQueryWrapper);
        for (Map<String, Object> map : maps) {
           empIDs.add((Long) map.get("empID"));
           empIDs.forEach(System.out::println);
        }

        for (int i = 0; i < 10; i++) {
            Course course = new Course();
            course.setCourseName("健身");
            course.setEmpID(empIDs.get(new Random().nextInt(empIDs.size())));
            course.setResidue(30);
            int insert = courseMapper.insert(course);
            System.out.println(insert);
        }
<<<<<<< HEAD
    }
=======
    }*/

}

