package com.jdrx.gis.beans.dto.dataManage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author lr
 * @Time 2019/12/13 0013 上午 10:37
 */
@Data
public class PointDTO {
    @ApiModelProperty("经度")
    private double lng;

    @ApiModelProperty("纬度")
    private double lat;
}