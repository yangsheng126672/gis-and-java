package com.jdrx.gis.beans.entity.query;

import lombok.Data;
import lombok.ToString;

/**
 * @Description: 管段长度
 * @Author: liaosijun
 * @Time: 2019/7/16 14:35
 */
@Data
@ToString
public class PipeLengthPO {

	/** 总长度 */
	private Double totalLength;

	/** 日期 */
	private String calDate;
}