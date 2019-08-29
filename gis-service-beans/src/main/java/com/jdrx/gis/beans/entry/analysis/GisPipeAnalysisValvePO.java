package com.jdrx.gis.beans.entry.analysis;

import java.util.Date;

public class GisPipeAnalysisValvePO {
    /**
     * null
     */
    private Long id;

    /**
     * 关联爆管记录id
     */
    private Long rid;

    /**
     * 一次关阀列表
     */
    private String valveFirst;

    /**
     * 关阀失败阀门
     */
    private String valveFailed;

    /**
     * 是否删除
     */
    private Boolean deleteFlag;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date updateAt;

    /**
     * 二次关阀列表
     */
    private String valveSecond;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public String getValveFirst() {
        return valveFirst;
    }

    public void setValveFirst(String valveFirst) {
        this.valveFirst = valveFirst == null ? null : valveFirst.trim();
    }

    public String getValveFailed() {
        return valveFailed;
    }

    public void setValveFailed(String valveFailed) {
        this.valveFailed = valveFailed == null ? null : valveFailed.trim();
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getValveSecond() {
        return valveSecond;
    }

    public void setValveSecond(String valveSecond) {
        this.valveSecond = valveSecond == null ? null : valveSecond.trim();
    }
}