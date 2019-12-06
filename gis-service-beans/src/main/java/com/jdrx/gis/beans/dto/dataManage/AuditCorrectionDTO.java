package com.jdrx.gis.beans.dto.dataManage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: liaosijun
 * @Time: 2019/12/5 18:32
 */
@Data
public class AuditCorrectionDTO {

	@ApiModelProperty("设备id")
	@NotNull(message = "设备ID不能为空")
	private String devId;

	@ApiModelProperty("纠错记录ID")
	@NotNull(message = "纠错记录ID不能为空")
	private Long detailId;

	@ApiModelProperty("字段中文名称")
	@NotNull(message = "字段中文名称不能为空")
	private String fieldDesc;

	@ApiModelProperty("字段英文名称")
	@NotNull(message = "字段英文名称不能为空")
	private String fieldName;

	@ApiModelProperty("原值")
	@NotNull(message = "原值不能为空")
	private String originVal;

	@ApiModelProperty("修改值")
	@NotNull(message = "修改不能为空")
	private String updVal;

	@ApiModelProperty("是否通过标识,0-未通过，1-通过")
	@NotNull(message = "是否通过标识不能为空")
	private String hasPass;
}
