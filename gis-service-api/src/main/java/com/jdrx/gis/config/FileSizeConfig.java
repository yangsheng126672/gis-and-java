package com.jdrx.gis.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * @Author: liaosijun
 * @Time: 2019/11/5 21:18
 */
@Configuration
public class FileSizeConfig {

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory(); //文件最大10M,DataUnit提供5中类型B,KB,MB,GB,TB
		factory.setMaxFileSize("20MB"); /// 设置单个上传数据总大小
		return factory.createMultipartConfig();
	}
}
