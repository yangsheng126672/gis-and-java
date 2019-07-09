package com.jdrx.gis.beans.dto.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: TODO
 * @Author: liaosijun
 * @Time: 2019/7/8 17:18
 */
@Data
public class TypeIDDTO {
	@ApiModelProperty("设备类型ID")
	@NotNull(message = "设备类型ID")
	private Long typeId;
}