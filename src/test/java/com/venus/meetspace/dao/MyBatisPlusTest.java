package com.venus.meetspace.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.venus.meetspace.repository.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@MybatisPlusTest
public class MyBatisPlusTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void contextLoads() {
        assertNotNull(userMapper);
    }
}
