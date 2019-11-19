package com.jdrx.gis.beans.dto.query;

import com.jdrx.gis.beans.dto.base.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @Description: 设备编号数组和类型ID
 * @Author: liaosijun
 * @Time: 2019/7/29 17:15
 */
@Data
@ToString
public class DevIDsForTypeDTO extends PageDTO {

	@ApiModelProperty("设备ID集合")
	private String[] devIds;

	@ApiModelProperty("设备类型ID")
	@NotNull(message = "设备类型ID不能为空")
	private Long typeId;

	@ApiModelProperty("时间戳")
	private String time;
}