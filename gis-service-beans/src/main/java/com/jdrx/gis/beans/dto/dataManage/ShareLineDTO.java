package com.jdrx.gis.beans.dto.datamanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/20 0020 下午 3:03
 */

@Data
public class ShareLineDTO {
    @ApiModelProperty("管线属性")
    private Map<String,Object> mapAttr;

    @ApiModelProperty("设备类型id")
    private Long typeId;

    @ApiModelProperty("起点编码")
    String startCode;

    @ApiModelProperty("终点编码")
    String endCode;

    @ApiModelProperty("材质")
    String material;

    @ApiModelProperty("管径")
    Integer caliber;

}
