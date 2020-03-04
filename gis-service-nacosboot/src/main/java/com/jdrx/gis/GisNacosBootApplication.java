package com.jdrx.gis;

import com.jdrx.gis.config.PGConfigProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *  GIS NACOS 启动类
 */
@ComponentScan(basePackages = "com.jdrx")
@EnableCaching
@SpringBootApplication
@MapperScan({"com.jdrx.gis.dao.*","com.jdrx.share.*"})
@ImportAutoConfiguration({PGConfigProperties.class})
@EnableScheduling
public class GisNacosBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(GisNacosBootApplication.class, args);
    }
}
