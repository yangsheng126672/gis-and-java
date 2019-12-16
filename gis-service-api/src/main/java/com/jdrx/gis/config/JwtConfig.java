package com.jdrx.gis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: liaosijun
 * @Time: 2019/12/16 10:52
 */
@Configuration
@ConfigurationProperties("jwt")
@Data
public class JwtConfig {

	/** jwt密钥 */
	private String signingKey;
}
