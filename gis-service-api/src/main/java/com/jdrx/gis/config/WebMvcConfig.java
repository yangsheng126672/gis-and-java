package com.jdrx.gis.config;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private PathConfig pathConfig;
	/**
	 * 静态资源处理
	 **/
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String path = pathConfig.getUploadPath();
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		registry.addResourceHandler(path + "/**").addResourceLocations("file:" + path);
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
	}
}
