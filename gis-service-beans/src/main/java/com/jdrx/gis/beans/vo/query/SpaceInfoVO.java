package com.jdrx.gis.beans.vo.query;

import lombok.Data;

import java.util.Map;

/**
 * @Description: 空间查询VO
 * @Author: liaosijun
 * @Time: 2019/6/17 18:10
 */
@Data
public class SpaceInfoVO {
	/** 设备编号 */
	private Long devId;

	/** 分类名称 */
	private String typeName;

	/** 设备名称 */
	private String name;

	/** 设备地址 */
	private String addr;

	/** 设备属性信息 */
	private Object dataInfo;

	/** 根据表格title解析出来的设备信息 */
	private Map<String, String> dataMap;
}