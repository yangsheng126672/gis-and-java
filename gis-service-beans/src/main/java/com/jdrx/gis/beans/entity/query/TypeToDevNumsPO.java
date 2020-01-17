package com.jdrx.gis.beans.entity.query;

import lombok.Data;

/**
 * @Author: liaosijun
 * @Time: 2020/1/16 17:16
 */
@Data
public class TypeToDevNumsPO {

	/** 类型ID */
	private Long id;

	/** 类型名称 */
	private String name;

	/** 层级深度 */
	private Integer depth;

	/** 层级路径 */
	private String path;

	/**  枝干or叶子 */
	private Short limbLeaf;

	/** 设备数量 */
	private Long num;

}
