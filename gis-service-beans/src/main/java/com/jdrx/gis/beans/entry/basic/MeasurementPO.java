package com.jdrx.gis.beans.entry.basic;

import lombok.Data;
import java.util.Date;

@Data
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
    private String meaturedValue;

    /**
     * 备注
     */
    private String remark;

    /**
     * 空间信息
     */
    private Object geom;

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
     * 地图显示级别
     */
    private Integer zoom;

}