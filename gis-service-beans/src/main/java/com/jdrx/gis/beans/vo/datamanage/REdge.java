package com.jdrx.gis.beans.vo.datamanage;



/**
 * 边 == 关系
 */
public class REdge extends RObject{

	//关系的ID  ==  聚合、连接、属于、包括等，这些关系可能是枚举字典，因此记录关系ID是有必要的
	private Long relationID;
	//关系名称
	private String name;
	//材质
	private String cztype;
	//管径
	private Integer gj;
	//权限
	private Long belong_to;

	
	/**
	 * 关系指向哪一个节点 == 可能这个节点还有关系【节点关系递增下去】
	 */
	private RNode  rNode;

	public Long getRelationID() {
		return relationID;
	}

	public void setRelationID(Long relationID) {
		this.relationID = relationID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RNode getrNode() {
		return rNode;
	}

	public void setrNode(RNode rNode) {
		this.rNode = rNode;
	}

	public String getCztype() {
		return cztype;
	}

	public void setCztype(String cztype) {
		this.cztype = cztype;
	}

	public Integer getGj() {
		return gj;
	}

	public void setGj(Integer gj) {
		this.gj = gj;
	}

	public Long getBelong_to() {
		return belong_to;
	}

	public void setBelong_to(Long belong_to) {
		this.belong_to = belong_to;
	}
}
