package com.jdrx.gis.beans.entity.basic;

import lombok.Data;

import java.util.Date;

@Data
public class ShareDevPO {
    /**
     * 主键
     */
    private String id;

    /**
     * 设备类型ID
     */
    private Long typeId;

    /**
     * 名称
     */
    private String name;

    /**
     * 状态
     */
    private Short status;

    /**
     * 序列号
     */
    private String sn;

    /**
     * 经度
     */
    private String lng;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 详细地址
     */
    private String addr;

    /**
     * 平台编码
     */
    private String platformCode;

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
     * null
     */
    private Short delFlag;


}