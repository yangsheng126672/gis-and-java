package com.jdrx.gis.beans.entry.analysis;

import lombok.Data;
import org.apache.ibatis.annotations.Options;

import java.util.Date;

@Data
public class GisPipeAnalysisPO {
    /**
     * null
     */
    private Long id;

    /**
     * null
     */
    private String code;

    /**
     * null
     */
    private Object pointgeom;

    /**
     * null
     */
    private Object areaFirst;

    /**
     * null
     */
    private Object areaSecond;

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