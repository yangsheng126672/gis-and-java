package com.jdrx.gis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: liaosijun
 * @Time: 2020/1/13 13:31
 */
@Configuration
@ConfigurationProperties("switch")
@Data
public class SwitchConfig {

	/** 数据权限开关， true-打开，false-关闭 */
	private Boolean permission;
}
