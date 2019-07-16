package com.jdrx.gis.beans.dto.third;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @Description: 时间段去查管段总长度
 * @Author: liaosijun
 * @Time: 2019/7/16 11:19
 */
@Data
@ToString
public class GetPipeTotalLenthDTO {

	@ApiModelProperty("开始日期")
	@NotNull(message = "开始日期不能为空")
	private String startAt;

	@ApiModelProperty("结束日期")
	@NotNull(message = "结束日期不能为空")
	private String endAt;
}