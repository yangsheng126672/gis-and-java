package com.jdrx.gis.beans.entity.basic;

import lombok.Data;
import org.postgis.PGgeometry;

import java.util.Date;

@Data
public class GISDevExtPO {

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 设备ID
	 */
	private String devId;

	/**
	 * 设备名称
	 */
	private String name;

	/**
	 * 设备编码
	 */
	private String code;

	/**
	 * 管径
	 */
	private Integer caliber;

	/**
	 * 材质
	 */
	private String material;

	/**
	 * 空间信息字符串形式
	 */
	private String geom;

	/**
	 * 模板类型ID
	 */
	private Long tplTypeId;

	/**
	 * JSON数据
	 */
	private Object dataInfo;

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

	/**
	 * 数据权限
	 */
	private Long belongTo;

	/**
	 * geom对象
	 */
	private PGgeometry geomObj;

	/**
	 * 图片地址数组
	 */
	private String picUrls;

	/**
	 * 视频地址数组
	 */
	private String videoUrls;
}