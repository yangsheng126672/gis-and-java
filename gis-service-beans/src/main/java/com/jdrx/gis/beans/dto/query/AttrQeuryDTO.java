package com.jdrx.gis.beans.dto.query;

import com.jdrx.gis.beans.dto.base.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @Description: 属性查询DTO
 * @Author: liaosijun
 * @Time: 2019/6/21 11:03
 */
@Data
@ToString
public class AttrQeuryDTO extends PageDTO {

	@NotNull(message = "设备类型ID不能为空")
	@ApiModelProperty("设备类型ID，也是模板表中的类型ID")
	private Long typeId;

	@NotNull(message = "属性字段名不能为空")
	@ApiModelProperty("属性名")
	private String fieldName;

	@ApiModelProperty("参数内容")
	private String paramVal;

	@ApiModelProperty("投影坐标系编号")
	@NotNull(message = "投影坐标系编号不能为空")
	private String inSR;

	@ApiModelProperty("经纬度范围,逗号隔开,如果该值为空即查询所有设备")
	private String range;
}