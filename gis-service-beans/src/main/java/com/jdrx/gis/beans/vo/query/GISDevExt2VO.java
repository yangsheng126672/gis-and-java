package com.jdrx.gis.beans.vo.query;

import lombok.Data;

/**
 * @Description: TODO
 * @Author: liaosijun
 * @Time: 2019/7/31 17:59
 */
@Data
public class GISDevExt2VO {

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 设备ID
	 */
	private Long devId;

	/**
	 * 类型ID
	 */
	private Long typeId;

	/**
	 * geom
	 */
	private String geom;
}