package com.jdrx.gis.beans.vo.query;

import lombok.Data;

/**
 * @Description: 空间查询列表的表头
 * @Author: liaosijun
 * @Time: 2019/6/18 9:50
 */
@Data
public class FieldNameVO {

	/** 字段中文描述 */
	private String fieldDesc;

	/** 字段名称 */
	private String fieldName;

	/** 数据类型分类 */
	private String dataType;

	/** 展示单个设备数据时，把展示的字段排序，增加排序序号 */
	private Short idx;

	/** 是否必填 必填：true  */
	private Boolean fill;
}