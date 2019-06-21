package com.jdrx.gis.beans.vo.query;

import lombok.Data;

/**
 * @Description: 空间查询根据水管口径统计
 * @Author: liaosijun
 * @Time: 2019/6/18 10:47
 */
@Data
public class WaterPipeTypeNumVO {

	/** 管径类型名称 */
	private String typeName;

	/** 不同管径对应的数量 */
	private Long num;
}