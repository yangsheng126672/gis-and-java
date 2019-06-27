package com.jdrx.gis.beans.vo.basic;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 字典数据VO
 * @Author: liaosijun
 * @Time: 2019/6/27 16:13
 */
@Data
public class DictDetailVO {

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 类型ID
	 */
	private Long typeId;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 值
	 */
	private String val;

	/**
	 * 平台编码
	 */
	private String platformCode;

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
}