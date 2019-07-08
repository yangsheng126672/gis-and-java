package com.jdrx.gis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 数据字典配置
 * @Author: liaosijun
 * @Time: 2019/6/28 14:28
 */
@Configuration
@ConfigurationProperties("dict")
@Data
public class DictConfig {

	/**口径类型的值*/
	private String caliberType;

	/** arcgis发布图层的URL对应的参数 */
	private String layerUrl;

	/** 点和面的图层URL */
	private String plLayerUrl;
}