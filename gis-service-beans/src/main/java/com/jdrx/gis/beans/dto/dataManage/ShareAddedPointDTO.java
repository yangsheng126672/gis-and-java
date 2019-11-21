package com.jdrx.gis.beans.dto.datamanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/21 0021 下午 2:28
 */

@Data
public class ShareAddedPointDTO {
    @ApiModelProperty("管点属性")
    private Map<String,Object> map;

    @ApiModelProperty("设备类型id")
    private Long typeId;

    @ApiModelProperty("x坐标")
    private Double x;

    @ApiModelProperty("y坐标")
    private Double y;

    @ApiModelProperty("管线设备id")
    private String lineDevId;
}
