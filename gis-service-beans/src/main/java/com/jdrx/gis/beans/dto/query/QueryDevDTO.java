package com.jdrx.gis.beans.dto.query;

/**
 * @Description: 空间查询DTO
 * @Author: liaosijun
 * @Time: 2019/6/17 15:45
 */

import com.jdrx.gis.beans.dto.base.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QueryDevDTO extends PageDTO {

	@ApiModelProperty("设备类型ID")
	private Long typeId;

	@ApiModelProperty("选中区域的设备ID集合")
	private List<Long> devIds;
}