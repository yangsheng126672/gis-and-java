package com.jdrx.gis.beans.dto.query;

import com.jdrx.gis.beans.dto.base.PageDTO;
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
public class AttrQeuryDTO extends PageDTO {

	@NotNull(message = "设备类型ID不能为空")
	@ApiModelProperty("设备类型ID，也是模板表中的类型ID")
	private Long typeId;

	@NotNull(message = "属性字段名不能为空")
	@ApiModelProperty("属性名")
	private String fieldName;

	@ApiModelProperty("参数内容")
	private String paramVal;

	@ApiModelProperty("选中区域的设备ID集合，空集合查全部")
	private List<Long> devIds;
}