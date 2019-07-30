package com.jdrx.gis.beans.dto.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @Description: TODO
 * @Author: liaosijun
 * @Time: 2019/7/29 19:53
 */
@Data
@ToString
public class DevIDsForTypesDTO {

	@ApiModelProperty("设备ID集合")
	@NotNull(message = "设备ID列表不能为空")
	private Long[] devIds;

	@ApiModelProperty("类型ID集合")
	@NotNull(message = "类型ID列表不能为空")
	private Long[] typeIds;
}