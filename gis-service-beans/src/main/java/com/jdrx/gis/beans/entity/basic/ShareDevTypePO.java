package com.jdrx.gis.beans.entity.basic;

import lombok.Data;

import java.util.Date;

@Data
public class ShareDevTypePO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 设备类型名称
     */
    private String name;

    /**
     * 名称对应的值
     */
    private String val;

    /**
     * 父ID
     */
    private Long pId;

    /**
     * 平台编码
     */
    private String platformCode;

    /**
     * 枝干还是叶子，1-枝干，2-叶子
     */
    private Short limbLeaf;

    /**
     * 是否删除
     */
    private Short delFlag;

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