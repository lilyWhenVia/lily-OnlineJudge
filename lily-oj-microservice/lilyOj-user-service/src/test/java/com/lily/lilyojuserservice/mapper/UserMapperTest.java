package com.lily.lilyojuserservice.mapper;

import com.lily.lilyojuserservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by lily via on 2024/4/26 23:12
 */
@SpringBootTest
class UserMapperTest {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Test
    public void testSelectById() {
        //selectList()根据MP内置的条件构造器查询一个list集合，null表示没有条件，即查询所有
        userMapper.selectList(null).forEach(System.out::println);
    }

    @Test
    public void testUserService() {
        userService.userLogin("lily", "12345678", null);
    }

}