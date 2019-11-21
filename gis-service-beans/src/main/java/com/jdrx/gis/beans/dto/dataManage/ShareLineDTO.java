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
    private Map<String,Object> map;

    @ApiModelProperty("设备类型id")
    private Long typeId;

}
