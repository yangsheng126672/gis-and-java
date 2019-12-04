package com.jdrx.gis.beans.entity.basic;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: liaosijun
 * @Time: 2019/11/14 15:53
 */
@Data
@Accessors(chain = true)
public class CodeXYPO {

	/** 点编码 */
	private String pointCode;

	/** 点X坐标 */
	private String pointX;

	/** 点Y坐标 */
	private String pointY;

	/** 线编码 */
	private String lineCode;

	/** 线起点X坐标 */
	private String lineStartX;

	/** 线起点Y坐标 */
	private String lineStartY;

	/** 线终点X坐标 */
	private String lineEndX;

	/** 线终点Y坐标 */
	private String lineEndY;

	/** 拼接串 */
	private String str;

	/** code */
	private String code;
}
