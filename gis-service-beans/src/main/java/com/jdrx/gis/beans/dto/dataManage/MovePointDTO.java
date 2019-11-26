package com.jdrx.gis.beans.dto.dataManage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/23 0023 下午 1:45
 */

@Data
public class MovePointDTO {
    @ApiModelProperty("设备id")
    private String devId;

    @ApiModelProperty("x坐标")
    private Double x;

    @ApiModelProperty("y坐标")
    private Double y;
}
