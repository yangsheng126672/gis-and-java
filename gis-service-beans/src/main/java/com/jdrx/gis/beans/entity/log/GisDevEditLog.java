package com.jdrx.gis.beans.entity.log;

import java.util.Date;

public class GisDevEditLog {
	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 版本号
	 */
	private Long verNum;

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
	 * 空间信息
	 */
	private Object geom;

	/**
	 * 模板类型ID
	 */
	private Long tplTypeId;

	/**
	 * JSON数据
	 */
	private Object dataInfo;

	/**
	 * 权属单位
	 */
	private Long belongTo;

	/**
	 * 图片地址
	 */
	private String picUrls;

	/**
	 * 视频地址
	 */
	private String videoUrls;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVerNum() {
		return verNum;
	}

	public void setVerNum(Long verNum) {
		this.verNum = verNum;
	}

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId == null ? null : devId.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code == null ? null : code.trim();
	}

	public Integer getCaliber() {
		return caliber;
	}

	public void setCaliber(Integer caliber) {
		this.caliber = caliber;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material == null ? null : material.trim();
	}

	public Object getGeom() {
		return geom;
	}

	public void setGeom(Object geom) {
		this.geom = geom;
	}

	public Long getTplTypeId() {
		return tplTypeId;
	}

	public void setTplTypeId(Long tplTypeId) {
		this.tplTypeId = tplTypeId;
	}

	public Object getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(Object dataInfo) {
		this.dataInfo = dataInfo;
	}

	public Long getBelongTo() {
		return belongTo;
	}

	public void setBelongTo(Long belongTo) {
		this.belongTo = belongTo;
	}

	public String getPicUrls() {
		return picUrls;
	}

	public void setPicUrls(String picUrls) {
		this.picUrls = picUrls == null ? null : picUrls.trim();
	}

	public String getVideoUrls() {
		return videoUrls;
	}

	public void setVideoUrls(String videoUrls) {
		this.videoUrls = videoUrls == null ? null : videoUrls.trim();
	}

	public Boolean getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy == null ? null : createBy.trim();
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy == null ? null : updateBy.trim();
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}
}