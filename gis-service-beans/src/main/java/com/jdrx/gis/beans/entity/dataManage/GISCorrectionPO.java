package com.jdrx.gis.beans.entity.dataManage;

import lombok.Data;

import java.util.Date;

@Data
public class GISCorrectionPO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 设备ID
     */
    private String devId;

    /**
     * 编码
     */
    private String code;

    /**
     * 0-未审核，1-审核
     */
    private Short status;

    /**
     * 是否删除,0-正常，1-删除
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

}