package com.jdrx.gis.beans.constants.basic;

/**
 * @Author: liaosijun
 * @Time: 2019/12/5 11:02
 */
public enum EAuditStatus {

	NO_AUDIT(0, "未审核"), AUDITED(1, "已审核");

	Integer val;
	String desc;

	private EAuditStatus(Integer val, String desc){
		this.val = val;
		this.desc = desc;
	}

	public Integer getVal() {
		return val;
	}

	public void setVal(Integer val) {
		this.val = val;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
