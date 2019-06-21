package com.jdrx.gis.beans.constants.basic;

/**
 * @Description: 管径大小
 * @Author: liaosijun
 * @Time: 2019/6/18 15:56
 */
public enum ECaliber {
	D1("DN100（不含）以下管段", "D1"),
	D2("DN100-DN200管段", "D2"),
	D3("DN200-DN400管段", "D3"),
	D4("DN400-DN600管段", "D4"),
	D5("DN600-DN900管段", "D5"),
	D6("DN900（含）以上管段", "D6");

	/** 类型 */
	String name;
	/** 描述 */
	String code;

	private ECaliber(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}