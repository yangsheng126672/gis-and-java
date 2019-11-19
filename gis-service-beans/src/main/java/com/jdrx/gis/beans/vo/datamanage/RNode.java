package com.jdrx.gis.beans.vo.datamanage;


/**
 * 节点
 */
public class RNode extends RObject{
    
	/**
	 * 节点的uuid == 对应其他数据库中的主键
	 */
	private Long uuid;

	private Long dev_id;

	private String name;

	private String jd;

	private String wd;

	/**
	 * 节点里面是否包含有边 == 关系
	 */
	private REdge edge;

	public Long getUuid() {
		return uuid;
	}

	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}

	public REdge getEdge() {
		return edge;
	}

	public void setEdge(REdge edge) {
		this.edge = edge;
	}

	public String getJd() {
		return jd;
	}

	public void setJd(String jd) {
		this.jd = jd;
	}

	public String getWd() {
		return wd;
	}

	public void setWd(String wd) {
		this.wd = wd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getDev_id() {
		return dev_id;
	}

	public void setDev_id(Long dev_id) {
		this.dev_id = dev_id;
	}
}
