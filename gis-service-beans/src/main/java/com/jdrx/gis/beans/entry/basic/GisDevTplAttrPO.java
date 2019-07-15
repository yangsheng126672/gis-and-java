package com.jdrx.gis.beans.entry.basic;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class GisDevTplAttrPO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 设备类型ID
     */
    private Long typeId;

    /**
     * 字段中文名称
     */
    private String fieldDesc;

    /**
     * 字段英文名称
     */
    private String fieldName;

    /**
     * 字段数据类型
     */
    private String dataType;

	/**
	 * 字段显示序号
	 */
	private Short idx;

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

}