package com.jdrx.gis.beans.entry.analysis;

import lombok.Data;

import java.util.Date;

@Data
public class GisPipeAnalysisValvePO {
    /**
     * null
     */
    private Long id;

    /**
     * null
     */
    private Long rid;

    /**
     * null
     */
    private String valveFirst;

    /**
     * null
     */
    private String valveFealed;

    /**
     * null
     */
    private String valveSecond;

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