package com.jdrx.gis.beans.dto.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: typeId和范围
 * @Author: liaosijun
 * @Time: 2019/7/8 16:40
 */
@Data
public class ExpRangeTypeDTO {

	@ApiModelProperty("经纬度范围,逗号隔开,如果该值为空即查询所有设备")
	private String range;

	@ApiModelProperty("投影坐标系编号")
	@NotNull(message = "投影坐标系编号不能为空")
	private String inSR;

	@ApiModelProperty("设备类型ID")
	@NotNull(message = "设备类型ID不能为空")
	private Long typeId;
}