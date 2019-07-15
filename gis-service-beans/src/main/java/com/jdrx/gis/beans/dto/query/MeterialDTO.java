package com.jdrx.gis.beans.dto.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author lr
 * @Time 2019/7/15 0015 下午 2:12
 */
@Data
public class MeterialDTO {
    @ApiModelProperty("管网材质")
    @NotNull(message = "管网材质")
    private String type;
}
