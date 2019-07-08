package com.jdrx.gis.beans.dto.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: GIS地图范围划定DTO
 * @Author: liaosijun
 * @Time: 2019/7/5 8:52
 */
@Data
public class RangeDTO {

	@ApiModelProperty("投影坐标系编号")
	@NotNull(message = "投影坐标系编号不能为空")
	private String inSR;

	@ApiModelProperty("经纬度范围,逗号隔开,如果该值为空即查询所有设备")
	private String range;

}