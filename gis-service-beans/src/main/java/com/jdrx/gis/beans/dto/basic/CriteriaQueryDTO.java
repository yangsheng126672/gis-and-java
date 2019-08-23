package com.jdrx.gis.beans.dto.basic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: TODO
 * @Author: liaosijun
 * @Time: 2019/8/21 15:36
 */
@Data
public class CriteriaQueryDTO {

	@ApiModelProperty("类型ID")
	@NotNull(message = "类型ID不能为空")
	private Long typeId;

	@ApiModelProperty("模板的类型ID")
	@NotNull(message = "模板的类型ID不能为空")
	private Long tplId;

	@ApiModelProperty("字段名称")
	@NotNull(message = "字段名称不能为空")
	private String fieldName;

}