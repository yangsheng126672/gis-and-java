package com.jdrx.gis.beans.constants.basic;

/**
 * @Description: 设备类型枝干叶子
 * @Author: liaosijun
 * @Time: 2019/6/18 20:07
 */
public enum ELimbLeaf {
	LIMB(1, "枝干"), LEAF(2, "叶子");

	Integer val;
	String desc;

	private ELimbLeaf(Integer val, String desc){
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