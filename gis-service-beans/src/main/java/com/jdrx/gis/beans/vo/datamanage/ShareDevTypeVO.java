package com.jdrx.gis.beans.vo.datamanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/21 0021 下午 6:10
 */

@Data
public class ShareDevTypeVO {
    @ApiModelProperty("类型名称")
    String typeName;

    @ApiModelProperty("类型id")
    Long typeId;

    @ApiModelProperty("类型图标")
    String iconUrl;
}
