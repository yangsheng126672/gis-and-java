package com.jdrx.gis.beans.entity.dataManage;

import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.beans.entity.log.GisDevVer;

import java.util.List;
import java.util.Map;

/**
 * @Author: liaosijun
 * @Time: 2019/11/20 10:46
 */
public class DevSaveParam {

	/** 数据列表 */
	private List<Map<String, Object>> dataMapList;

	/** 登录用户名称 */
	private String loginUserName;

	/** Excel的sheet名称 */
	private String sheetName;

	/** 模板ID */
	private Long tplTypeId;

	/** 已经存在的编码 */
	private List<GISDevExtPO> existsCodes;

	/** 批次号 */
	private String batchNum;

	/** 1-insert, 2-update */
	private int saveFlag;

	/** 版本信息*/
	private GisDevVer gisDevVer;

	public List<Map<String, Object>> getDataMapList() {
		return dataMapList;
	}

	public void setDataMapList(List<Map<String, Object>> dataMapList) {
		this.dataMapList = dataMapList;
	}

	public String getLoginUserName() {
		return loginUserName;
	}

	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public Long getTplTypeId() {
		return tplTypeId;
	}

	public void setTplTypeId(Long tplTypeId) {
		this.tplTypeId = tplTypeId;
	}

	public List<GISDevExtPO> getExistsCodes() {
		return existsCodes;
	}

	public void setExistsCodes(List<GISDevExtPO> existsCodes) {
		this.existsCodes = existsCodes;
	}

	public String getBatchNum() {
		return batchNum;
	}

	public void setBatchNum(String batchNum) {
		this.batchNum = batchNum;
	}

	public int getSaveFlag() {
		return saveFlag;
	}

	public void setSaveFlag(int saveFlag) {
		this.saveFlag = saveFlag;
	}

	public GisDevVer getGisDevVer() {
		return gisDevVer;
	}

	public void setGisDevVer(GisDevVer gisDevVer) {
		this.gisDevVer = gisDevVer;
	}
}
