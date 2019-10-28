package com.jdrx.gis.beans.vo.basic;

import lombok.Data;

/**
 * @Description
 * @Author lr
 * @Time 2019/9/23 0023 下午 4:31
 */

@Data
public class InspectionVO {

    /**
     *项目名称
     */
    String title;

    /**
     *point图层
     */
    String point;

    /**
     *线图层
     */
    String line;

    /**
     *中心点x坐标
     */
    String x;

    /**
     *中心点y坐标
     */
    String y;

    /**
     *管网wms资源
     */
    String wms;

    /**
     *城市经纬度范围
     */
    String extent;



}
