package com.jdrx.gis.beans.vo.datamanage;

import lombok.Data;

import java.util.Map;

/**
 * @Author: liaosijun
 * @Time: 2019/12/6 10:26
 */
@Data
public class HistoryRecordVO {

	/** 上报人 */
	private String createBy;

	/** 上报日期 */
	private String createAt;

	/** 设备编码 */
	private String code;

	/** 通过的内容 */
	Map<String, Object> passMap;

	/** 审核状态 */
	private String status;
}
