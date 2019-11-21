package com.jdrx.gis.beans.dto.datamanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @Author: liaosijun
 * @Time: 2019/11/19 19:44
 */
@Data
public class ImportDTO {
	@ApiModelProperty("设备数据")
	@NotNull(message = "设备数据不能为空")
	private Map<String, List> dataMap;
	@ApiModelProperty("提示的标识，1-需要提示，0-不需提示")
	@NotNull(message = "提示标识不能为空")
	private Integer ts;
}
