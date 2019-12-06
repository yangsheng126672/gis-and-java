package com.jdrx.gis.beans.constants.basic;

/**
 * @Author: liaosijun
 * @Time: 2019/12/6 13:16
 */
public enum EPassStatus {

	NO_PASS(0, "未通过"), PASSED(1, "通过");
	Integer val;
	String desc;

	private EPassStatus(Integer val, String desc){
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
