package com.jdrx.gis.beans.entry.log;

import java.util.Date;

public class GisTransLog {
    /**
     * 主键
     */
    private Long id;

    /**
     * 交易ID
     */
    private String transId;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口
     */
    private String api;

    /**
     * 接口的请求参数
     */
    private String reqParams;

    /**
     * 响应码，0-成功，其它失败
     */
    private Short returnCode;

    /**
     * 客户端host
     */
    private String reqHost;

    /**
     * 操作人员
     */
    private String operator;

    /**
     * 接口消耗时间，单位ms
     */
    private Integer cost;

    /**
     * 创建时间
     */
    private Date createAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId == null ? null : transId.trim();
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName == null ? null : apiName.trim();
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api == null ? null : api.trim();
    }

    public String getReqParams() {
        return reqParams;
    }

    public void setReqParams(String reqParams) {
        this.reqParams = reqParams == null ? null : reqParams.trim();
    }

    public Short getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Short returnCode) {
        this.returnCode = returnCode;
    }

    public String getReqHost() {
        return reqHost;
    }

    public void setReqHost(String reqHost) {
        this.reqHost = reqHost == null ? null : reqHost.trim();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

	@Override
	public String toString() {
		return "GisTransLog{" +
				"id=" + id +
				", transId='" + transId + '\'' +
				", apiName='" + apiName + '\'' +
				", api='" + api + '\'' +
				", reqParams='" + reqParams + '\'' +
				", returnCode=" + returnCode +
				", reqHost='" + reqHost + '\'' +
				", operator='" + operator + '\'' +
				", cost=" + cost +
				", createAt=" + createAt +
				'}';
	}
}