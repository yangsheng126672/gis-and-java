package com.jdrx.gis.beans.entry.analysis;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author lr
 * @Time 2019/8/29 0029 下午 5:11
 */
@Data
public class ExportValveRecondDTO {
    @ApiModelProperty("名称")
    String name;
    @ApiModelProperty("爆管点编号")
    Long lineId;
    @ApiModelProperty("爆管点经纬度")
    Double[] point;
    @ApiModelProperty("成功阀门设备ID集合")
    private String[] firstDevIds;
    @ApiModelProperty("成功阀门设备ID集合")
    private String[] secondDevIds;
    @ApiModelProperty("失败阀门设备ID集合")
    private String[] failedDevIds;
}
