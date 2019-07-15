package com.jdrx.gis.beans.dto.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author lr
 * @Time 2019/7/15 0015 下午 2:14
 */

@Data
public class CaliberDTO {
    @ApiModelProperty("管径大小")
    @NotNull(message = "管径大小")
    private String num;
}

