package com.jdrx.gis.beans.dto.basic;

import com.jdrx.gis.beans.dto.base.InsertDTO;
import com.jdrx.gis.beans.dto.base.UpdateDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Description: 字典类型
 * @Author: liaosijun
 * @Time: 2019/6/27 15:24
 */
@Data
@Accessors
public class DictTypeDTO{

	@ApiModelProperty("id")
	@NotNull(message = "ID不能为空", groups = {UpdateDTO.class})
	private Long id;

	@ApiModelProperty("类型名称")
	@NotNull(message = "类型名称不能为空", groups = {InsertDTO.class})
	private String name;

	@ApiModelProperty("类型值")
	@NotNull(message = "类型值不能为空", groups = {InsertDTO.class})
	private String val;

	@ApiModelProperty("父节点ID")
	@NotNull(message = "父节点ID不能为空", groups = {InsertDTO.class})
	private Long pId;

	@ApiModelProperty("平台编码")
	@NotNull(message = "平台编码不能为空", groups = {InsertDTO.class})
	private String platformCode;

	@ApiModelProperty("创建人")
	@NotNull(message = "创建人不能为空", groups = {InsertDTO.class})
	private String creatBy;

	@ApiModelProperty("创建时间")
	private Date creatAt;

	@ApiModelProperty("修改人")
	@NotNull(message = "修改人不能为空", groups = {UpdateDTO.class})
	private String updateBy;

	@ApiModelProperty("修改时间")
	private Date updateAt;
}