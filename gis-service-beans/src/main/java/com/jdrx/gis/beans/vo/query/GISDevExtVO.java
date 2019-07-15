package com.jdrx.gis.beans.vo.query;

import lombok.Data;

import java.util.Map;

/**
 * @Description: extVO
 * @Author: liaosijun
 * @Time: 2019/6/20 20:10
 */
@Data
public class GISDevExtVO {

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 设备ID
	 */
	private Long devId;

	/**
	 * 模板类型ID
	 */
	private Long tplTypeId;

	/**
	 * 类名称
	 */
	private String typeName;

	/**
	 * JSON数据
	 */
	private Object dataInfo;

	/** 根据表格title解析出来的设备信息 */
	private Map<String, String> dataMap;

}