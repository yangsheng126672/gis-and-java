package com.jdrx.gis.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.PostConstruct;

/**
 * 配置请求头在线程间的作用域, 保证在父子线程间可以传递请求头
 * ,用于传递数据库信息
 * Create by dengfan at 2019/9/18 0018 16:20
 */
@Configuration
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class RequestAttrScopeConfig {

    @Autowired
    private DispatcherServlet servlet;


    @PostConstruct
    public void configInheritable() {
        servlet.setThreadContextInheritable(true);
    }
}
