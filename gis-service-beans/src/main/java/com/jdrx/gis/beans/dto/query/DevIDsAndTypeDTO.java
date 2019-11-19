package com.jdrx.gis.beans.dto.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @Description: TODO
 * @Author: liaosijun
 * @Time: 2019/7/31 17:03
 */
@Data
@ToString
public class DevIDsAndTypeDTO {

	@ApiModelProperty("设备ID集合")
	@NotNull(message = "设备ID集合不能为空")
	private String[] devIds;

	@ApiModelProperty("设备类型ID")
	@NotNull(message = "设备类型ID不能为空")
	private Long typeId;
}