package com.jdrx.gis.beans.entity.log;

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
     * 交易编码
     */
    private String transCode;

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
     * 响应提示，success-成功，失败-相应提示
     */
    private String returnCode;

    /**
     * null
     */
    private String returnMsg;

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

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode == null ? null : transCode.trim();
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

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode == null ? null : returnCode.trim();
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg == null ? null : returnMsg.trim();
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
				", transCode='" + transCode + '\'' +
				", apiName='" + apiName + '\'' +
				", api='" + api + '\'' +
				", reqParams='" + reqParams + '\'' +
				", returnCode='" + returnCode + '\'' +
				", returnMsg='" + returnMsg + '\'' +
				", reqHost='" + reqHost + '\'' +
				", operator='" + operator + '\'' +
				", cost=" + cost +
				", createAt=" + createAt +
				'}';
	}
}