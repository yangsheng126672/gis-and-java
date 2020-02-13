package com.jdrx.gis.beans.entity.basic;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName BookMarkPO
 * @Description TODO
 * @Author yangsheng
 * @Date 2020/2/13 14:12
 * @Version 1.0
 */
@Data
public class BookMarkPO {
    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 图片url
     */
    private String url;



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

    /**
     * x坐标
     */
    private Double x;

    /**
     * y坐标
     */
    private Double y;

    /**
     * 权属值
     */
    private Long belongTo;
}
