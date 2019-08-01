package com.jdrx.gis.beans.dto.analysis;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author lr
 * @Time 2019/8/1 0001 上午 9:42
 */

@Data
public class RecondParamasDTO {
    @ApiModelProperty("爆管点编号")
    String code;
    @ApiModelProperty("爆管时间")
    String datetime;
}
