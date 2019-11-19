package com.jdrx.gis.beans.vo.datamanage;

import java.util.Map;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/18 0018 下午 3:50
 */

public class NeoLineVO {
    //设备id
    private Long dev_id;

    //设备编码
    private String code;

    //管径
    private Integer caliber;

    //管材
    private String material;

    //详细属性
    private Object data_info;

    //权限
    private Long belong_to;

    public Long getDev_id() {
        return dev_id;
    }

    public void setDev_id(Long dev_id) {
        this.dev_id = dev_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getCaliber() {
        return caliber;
    }

    public void setCaliber(Integer caliber) {
        this.caliber = caliber;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Object getData_info() {
        return data_info;
    }

    public void setData_info(Object data_info) {
        this.data_info = data_info;
    }

    public Long getBelong_to() {
        return belong_to;
    }

    public void setBelong_to(Long belong_to) {
        this.belong_to = belong_to;
    }
}
