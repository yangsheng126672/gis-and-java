package com.jdrx.beans.dto.query;

/**
 * @Description: 空间查询DTO
 * @Author: liaosijun
 * @Time: 2019/6/17 15:45
 */

import com.jdrx.dao.base.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QueryDevDTO extends PageDTO{

	@ApiModelProperty("ID")
	private Long id;

	@ApiModelProperty("IDs")
	private List<Long> ids;
}