package com.jdrx.gis.beans.dto.datamanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/23 0023 下午 3:36
 */
@Data
public class MapAttrDTO {

    @ApiModelProperty("模板属性map")
    private Map mapAttr;
}
