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

	/**  图例icon对应的URL */
	private String iconUrl;

	/**  管径范围对应的URL */
	private String caliberUrl;

	/** 管材对应的URL */
	private String meterialUrl;

	/** 巡检系统图层资源URL */
	private String xjSourceUrl;

	/** 管网数据坐标系编号 */
	private String waterPipeSrid;

	/** 阀门类型ID */
	private String valveTypeId;

	/** 地图中心点 */
	private String mapCenterVal;

	/** 默认隐藏加载图层 */
	private String defaultLayerUrl;

	/** 项目名称 */
	private String projectName;

	/** 管点类型 */
	private String pointType;

	/** 管线类型 */
	private String lineType;


}