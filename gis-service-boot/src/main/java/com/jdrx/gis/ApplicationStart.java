package com.jdrx.gis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * spring boot 启动类
 * Created by wjm on 2017/5/5.
 */

@EnableAutoConfiguration
@ComponentScan(basePackages = "com.jdrx")
@SpringBootApplication
@MapperScan("com.jdrx.gis.dao.*")
public class ApplicationStart {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationStart.class, args);
    }
}
