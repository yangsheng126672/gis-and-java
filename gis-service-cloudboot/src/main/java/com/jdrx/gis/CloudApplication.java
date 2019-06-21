package com.jdrx.gis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by wjm on 2017/5/18.
 */


@EnableAutoConfiguration
@ComponentScan(basePackages = "com.jdrx")
@MapperScan("com.jdrx.gis.dao.*")
@SpringCloudApplication
public class CloudApplication {
    public static void main(String[] args){
        SpringApplication.run(CloudApplication.class, args);
    }
}
