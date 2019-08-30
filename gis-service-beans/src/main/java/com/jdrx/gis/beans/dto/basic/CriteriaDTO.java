package com.jdrx.gis.beans.dto.basic;

import com.jdrx.gis.beans.dto.base.InsertDTO;
import com.jdrx.gis.beans.dto.base.UpdateDTO;
import com.jdrx.gis.beans.dto.query.CriteriaWithDataTypeCategoryCodeDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

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

	@NotNull(message = "条件集合不能为空", groups = {InsertDTO.class})
	@ApiModelProperty("条件集合")
	private List<CriteriaWithDataTypeCategoryCodeDTO> criteriaList;

	@NotNull(message = "拼接的可执行SQL条件", groups = {InsertDTO.class})
	@ApiModelProperty("拼接的可执行SQL条件")
	private String assemblyStr;
}