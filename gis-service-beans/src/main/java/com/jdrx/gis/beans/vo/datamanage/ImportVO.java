package com.jdrx.gis.beans.vo.datamanage;

/**
 * @Author: liaosijun
 * @Time: 2019/11/21 13:50
 */
public class ImportVO {

	/**
	 * 接口处理是否成功
	 */
	private Boolean retStatus;

	/**
	 *  Y - 有更新， N- 无更新
	 */
	private String isOverride;

	/**
	 * 有更新时的提示信息
	 */
	private String msg;

	public Boolean getRetStatus() {
		return retStatus;
	}

	public void setRetStatus(Boolean retStatus) {
		this.retStatus = retStatus;
	}

	public String getIsOverride() {
		return isOverride;
	}

	public void setIsOverride(String isOverride) {
		this.isOverride = isOverride;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
