package com.jdrx.gis.beans.entity.dataManage;

import lombok.Data;

import java.util.Date;

/**
 * @Author: liaosijun
 * @Time: 2020/1/6 15:06
 */
@Data
public class MultiMediaPO {

	/** 图片url */
	private String picUrls;

	/** 视频url */
	private String videoUrls;

	/** 设备ID */
	private String devId;

	/** 更新时间 */
	private Date updateAt;

	/** 更新人 */
	private String updateBy;
}
