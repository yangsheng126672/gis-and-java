package com.jdrx.gis.beans.constants.basic;

/**
 * @Author: liaosijun
 * @Time: 2019/11/20 21:13
 */
public enum EupdateAndInsert {
	INSERT(1, "insert"), UPDATE(2, "update");

	Integer val;
	String desc;

	private EupdateAndInsert(Integer val, String desc){
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
