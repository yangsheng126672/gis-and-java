package com.jdrx.gis.beans.dto.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 属性查询DTO
 * @Author: liaosijun
 * @Time: 2019/6/21 11:03
 */
@Data
public class AttrQeuryDTO {

	@NotNull
	@ApiModelProperty("设备类型ID")
	private Long typeId;

	@NotNull
	@ApiModelProperty("属性名")
	private String fieldName;

	@ApiModelProperty("参数内容")
	private String paramVal;

	@ApiModelProperty("选中区域的设备ID集合")
	private List<Long> devIds;
}