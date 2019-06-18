package com.jdrx.beans.vo.query;

import lombok.Data;

/**
 * @Description: 子类个数汇总VO
 * @Author: liaosijun
 * @Time: 2019/6/18 17:09
 */
@Data
public class SonsNumVO {

	/** 设备类型名称 */
	private String typeName;

	/** 设备个数 */
	private Long num;
}