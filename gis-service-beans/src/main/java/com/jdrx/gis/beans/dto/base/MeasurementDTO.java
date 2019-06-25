package com.jdrx.gis.beans.dto.base;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description 空间测量DTO
 * @Author lr
 * @Time 2019/6/25 0025 上午 11:26
 */

public class MeasurementDTO {
    @ApiModelProperty("名称")
    protected String name;
    @ApiModelProperty("测量值")
    protected Double value ;
    @ApiModelProperty("备注信息")
    protected String remark;
    @ApiModelProperty("空间信息")
    protected  String geom;
}
