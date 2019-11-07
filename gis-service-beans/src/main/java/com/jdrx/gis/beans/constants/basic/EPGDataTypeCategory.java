package com.jdrx.gis.beans.constants.basic;

/**
 * @Description: TODO
 * @Author: liaosijun
 * @Time: 2019/8/22 11:11
 */
public enum EPGDataTypeCategory {
	A("A", "Array types"),
	B("B", "Boolean types"),
	C("C", "Composite types"),
	D("D", "Date/time types"),
	E("E", "Enum types"),
	G("G", "Geometric types"),
	I("I", "Network address types"),
	N("N", "Numeric types"),
	P("P", "Pseudo-types"),
	R("R", "Range types"),
	S("S", "String types"),
	T("T", "Timespan types"),
	U("U", "User-defined types"),
	V("V", "Bit-string types"),
	X("X", "unknown type");

	private String code;
	private String desc;

	EPGDataTypeCategory(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
