package com.jdrx.gis.beans.vo.basic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author lr
 * @Time 2019/12/10 0010 下午 4:46
 */
@Data
public class PipeLengthVO {
    @ApiModelProperty("权限值")
    private Long authId;

    @ApiModelProperty("管网长度")
    private Double length;

}
