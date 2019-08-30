package com.jdrx.gis.beans.dto.basic;

import com.jdrx.gis.beans.dto.base.InsertDTO;
import com.jdrx.gis.beans.dto.base.UpdateDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: TODO
 * @Author: liaosijun
 * @Time: 2019/8/21 15:36
 */
@Data
public class CriteriaDTO {

	@ApiModelProperty("ID")
	@NotNull(message = "ID不能为空", groups = {UpdateDTO.class})
	private Long id;

	@ApiModelProperty("类型ID")
	@NotNull(message = "类型ID不能为空", groups = {InsertDTO.class})
	private Long typeId;

	@ApiModelProperty("模板的类型ID")
	@NotNull(message = "模板的类型ID不能为空", groups = {InsertDTO.class})
	private Long tplId;

	@ApiModelProperty("查询条件")
	@NotNull(message = "查询条件不能为空", groups = {InsertDTO.class})
	private String criteria;
}