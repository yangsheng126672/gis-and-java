package com.jdrx.gis.beans.vo.basic;

/**
 * @Description
 * @Author lr
 * @Time 2019/10/10 0010 下午 3:12
 */

public class FeatureVO {
    //设备id
    String devId;

    //数据类型
    String type;

    //空间信息
    String geom;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }
}
