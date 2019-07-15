package com.jdrx.gis.beans.dto.basic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @Description: 数据字典参数值
 * @Author: liaosijun
 * @Time: 2019/7/15 14:23
 */
@Data
@ToString
public class DictQueryByValDTO {

	@ApiModelProperty("参数值")
	@NotNull(message = "参数值")
	private String val;
}