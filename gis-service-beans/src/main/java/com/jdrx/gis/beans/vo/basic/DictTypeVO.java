package com.jdrx.gis.beans.vo.basic;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 字典类型VO
 * @Author: liaosijun
 * @Time: 2019/6/27 15:59
 */
@Data
public class DictTypeVO {
	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 类型名称
	 */
	private String name;

	/**
	 * 名称对应的值
	 */
	private String val;

	/**
	 * 父ID
	 */
	private Long pId;

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