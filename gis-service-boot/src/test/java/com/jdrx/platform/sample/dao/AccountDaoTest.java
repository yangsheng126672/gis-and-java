package com.jdrx.platform.sample.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by wjm on 2017/5/16.
 */

@SpringBootTest
@ComponentScan("com.jdrx.plant")
@RunWith(SpringRunner.class)

public class AccountDaoTest {

    @Autowired
    AccountDAO accountDao;


    @Test
    public void testGet(){
        accountDao.getById(0l);
    }
}