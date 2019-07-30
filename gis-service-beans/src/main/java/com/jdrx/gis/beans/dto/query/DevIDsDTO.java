package com.jdrx.gis.beans.dto.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @Description: TODO
 * @Author: liaosijun
 * @Time: 2019/7/30 14:42
 */
@Data
@ToString
public class DevIDsDTO {
	@ApiModelProperty("设备ID集")
	@NotNull(message = "设备ID集合不能为空")
	private Long[] devIds;
}