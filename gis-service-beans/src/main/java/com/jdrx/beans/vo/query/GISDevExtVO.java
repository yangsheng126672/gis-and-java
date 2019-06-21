package com.jdrx.beans.vo.query;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @Description: extVO
 * @Author: liaosijun
 * @Time: 2019/6/20 20:10
 */
@Data
public class GISDevExtVO {

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 设备ID
	 */
	private Long devId;

	/**
	 * 是否删除
	 */
	private Boolean deleteFlag;

	/**
	 * 创建人
	 */
	private String createBy;

	/**
	 * 创建时间
	 */
	private Date createAt;

	/**
	 * 修改人
	 */
	private String updateBy;

	/**
	 * 修改时间
	 */
	private Date updateAt;

	/** 根据表格title解析出来的设备信息 */
	private Map<String, String> dataMap;
}