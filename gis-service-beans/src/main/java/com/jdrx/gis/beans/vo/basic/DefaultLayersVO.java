package com.jdrx.gis.beans.vo.basic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author lr
 * @Time 2019/9/20 0020 下午 4:08
 */

@Data
public class DefaultLayersVO {
    /**
     * cad图层
     */
    String cad;

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
     * 地图范围
     */
    String extent;

    /**
     * 地图分辨率
     */
    String resolutions;


}
