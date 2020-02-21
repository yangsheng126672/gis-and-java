package com.jdrx.gis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * @Author: liaosijun
 * @Time: 2020/2/21 10:46
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	/**
	 * 静态资源处理
	 **/
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		File file = new File("/opt/upload/");
		if (!file.exists()) {
			file.mkdirs();
		}
		registry.addResourceHandler("/opt/upload/**").addResourceLocations("file:/opt/upload/");
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
	}
}
