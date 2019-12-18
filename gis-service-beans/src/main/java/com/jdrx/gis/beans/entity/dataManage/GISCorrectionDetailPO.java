package com.jdrx.gis.beans.entity.dataManage;

import lombok.Data;

import java.util.Date;

@Data
public class GISCorrectionDetailPO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 设备ID
     */
    private Long coRecordId;

    /**
     * 字段中文描述
     */
    private String fieldDesc;

    /**
     * 英文字段名称
     */
    private String fieldName;

    /**
     * 修改值
     */
    private String updVal;

	/**
	 * 是否通过，0-未通过，1-通过
	 */
	private Short hasPass;

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