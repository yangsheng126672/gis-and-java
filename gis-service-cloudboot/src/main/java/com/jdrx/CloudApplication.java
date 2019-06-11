package com.jdrx.platform.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by wjm on 2017/5/18.
 */


@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.jdrx")
@EnableHystrix
@SpringCloudApplication
public class CloudApplication {
    public static void main(String[] args){
        SpringApplication.run(CloudApplication.class, args);
    }
}
