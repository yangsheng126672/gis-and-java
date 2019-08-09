package com.jdrx.gis.beans.vo.query;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @Author: liaosijun
 * @Time: 2019/7/31 17:59
 */
@Data
@Accessors(chain = true)
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

	/**
	 * 类型名称
	 */
	private String typeName;

	/**
	 * 父类ID
	 */
	private Long pId;

	/**下级节点*/
	protected List<GISDevExt2VO> children = new ArrayList<>();


}