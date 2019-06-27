package com.jdrx.gis.beans.dto.basic;

import com.jdrx.gis.beans.dto.base.InsertDTO;
import com.jdrx.gis.beans.dto.base.UpdateDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Description: 数据字典详情DTO
 * @Author: liaosijun
 * @Time: 2019/6/27 16:07
 */
@Data
public class DictDetailDTO {

	@ApiModelProperty("id")
	@NotNull(message = "ID不能为空", groups = {UpdateDTO.class})
	private Long id;

	@ApiModelProperty("typeId")
	@NotNull(message = "类型ID不能为空", groups = {InsertDTO.class})
	private Long typeId;

	@ApiModelProperty("名称")
	@NotNull(message = "名称不能为空", groups = {InsertDTO.class})
	private String name;

	@ApiModelProperty("值")
	@NotNull(message = "值不能为空", groups = {InsertDTO.class})
	private String val;

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