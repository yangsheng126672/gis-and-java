package com.jdrx.gis.beans.vo.datamanage;

/**
 * @Author: luobin
 * @Time: 2019/11/21 13:50
 */
public class ExportCadVO {

	/**
	 * 设备名称
	 */
	private String name;

	/**
	 *  设备类型（"POINT"/"LINESTRING"）
	 */
	private String type;

	/**
	 * 转换后的geom
	 */
	private String geom;

	public void setName(String name) {this.name = name;}

	public void setType(String type) {this.type = type;}

	public void setGeom(String geom) {this.geom = geom;}

	public String getName() {return name;}

	public String getType() {return type;}

	public String getGeom() {return geom;}
}
