package com.jdrx.gis.beans.constants.basic;

/**
 * @Author: liaosijun
 * @Time: 2020/2/18 15:40
 */
public enum EDBCommand {
	INSERT(1, "增加"), UPDATE(2, "修改"), DELETE(3, "删除");

	Integer val;
	String desc;

	EDBCommand(Integer val, String desc){
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
