package com.jdrx.gis.beans.vo.basic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author lr
 * @Time 2020/2/18 0018 下午 7:13
 */

@Data
public class NeoRelVO {
    @ApiModelProperty("管线设备id")
    private String relationId;

    @ApiModelProperty("管线起点编码")
    private String startCode;

    @ApiModelProperty("管线终点编码")
    private String endCode;

    @ApiModelProperty("管径")
    private String caliber;

    @ApiModelProperty("材质")
    private String material;

    @ApiModelProperty("权属值")
    private String belongTo;

}
