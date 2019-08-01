package com.jdrx.gis.beans.entry.analysis;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GisPipeAnalysisPO {
    /**
     * null
     */
    private Long id;

    /**
     * 爆管编号
     */
    private String code;

    /**
     * 经纬
     */
    private BigDecimal x;

    /**
     * 纬度
     */
    private BigDecimal y;

    /**
     * 爆管影响范围空间信息
     */
    private Object area;

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