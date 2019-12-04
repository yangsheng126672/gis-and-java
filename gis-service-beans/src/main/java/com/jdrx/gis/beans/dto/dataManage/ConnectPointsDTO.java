package com.jdrx.gis.beans.dto.dataManage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author lr
 * @Time 2019/12/3 0003 下午 5:20
 */
@Data
public class ConnectPointsDTO {
    @ApiModelProperty("设备ids")
    List<String> devIds;

    @ApiModelProperty("管线属性信息")
    Map<String,Object> mapAttr;

    @ApiModelProperty("设备类型id")
    private Long typeId;

    @ApiModelProperty("起点编码")
    String qdbm;

    @ApiModelProperty("终点编码")
    String zdbm;

    @ApiModelProperty("材质")
    String material;

    @ApiModelProperty("管径")
    Integer caliber;
}
