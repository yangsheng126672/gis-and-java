package com.jdrx.gis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * @Author: liaosijun
 * @Time: 2020/2/21 10:46
 */

@Configuration
public class WebMvcConfig {

	@Autowired
	private PathConfig pathConfig;
	/**
	 * 静态资源处理
	 **/
	@Bean
	public WebMvcConfigurer webMvcConfigurer(){
		return new WebMvcConfigurer() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				String PATH = pathConfig.getUploadPath();
				File file = new File(PATH);
				if(!file.exists()){
					file.mkdir();
				}
				registry.addResourceHandler("/images/**").addResourceLocations("file:" + PATH + "/");
			}
		};
	}

}
