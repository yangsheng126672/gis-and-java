package com.jdrx.gis.beans.dto.dataManage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @Author: liaosijun
 * @Time: 2019/12/4 16:23
 */
@Data
public class CorrectionDTO {
	@ApiModelProperty("设备id")
	@NotNull(message = "设备ID不能为空")
	private String devId;

	@ApiModelProperty("设备编码")
	@NotNull(message = "设备编码不能为空")
	private String code;

	@ApiModelProperty("模板属性map")
	@NotNull(message = "模板属性值不能为空")
	private Map mapAttr;
}
