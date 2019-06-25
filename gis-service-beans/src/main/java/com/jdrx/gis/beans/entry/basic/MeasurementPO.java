package com.jdrx.gis.beans.entry.basic;

import java.math.BigDecimal;

public class MeasurementPO {
    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 测量值
     */
    private BigDecimal meaturedValue;

    /**
     * 备注
     */
    private String remark;

    /**
     * 空间信息
     */
    private Object geom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public BigDecimal getMeaturedValue() {
        return meaturedValue;
    }

    public void setMeaturedValue(BigDecimal meaturedValue) {
        this.meaturedValue = meaturedValue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Object getGeom() {
        return geom;
    }

    public void setGeom(Object geom) {
        this.geom = geom;
    }
}