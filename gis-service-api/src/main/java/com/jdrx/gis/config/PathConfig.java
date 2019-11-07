package com.jdrx.gis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: url或者目录
 * @Author: liaosijun
 * @Time: 2019/7/15 14:04
 */
@Configuration
@ConfigurationProperties("ld")
@Data
public class PathConfig {

	private String downloadPath;

	private String uploadFileUrl;

	/** 模板存放的目录 */
	private String templatePath;
}