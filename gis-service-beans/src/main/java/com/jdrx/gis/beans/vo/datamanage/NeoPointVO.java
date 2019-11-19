package com.jdrx.gis.beans.vo.datamanage;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/18 0018 下午 3:50
 */

public class NeoPointVO {
    //设备id
    private Long dev_id;

    //设备编码
    private String code;

    //类型名称
    private String name;

    //经度
    private Double x;

    //纬度
    private Double y;

    //权限
    private String belong_to;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public String getBelong_to() {
        return belong_to;
    }

    public void setBelong_to(String belong_to) {
        this.belong_to = belong_to;
    }
}
