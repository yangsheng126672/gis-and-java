package com.jdrx.gis.beans.dto.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 分页
 * @Author: liaosijun
 * @Time: 26/20 15:02
 */
@Data
public class PageDTO {
	@ApiModelProperty("当前页数 默认为1")
	protected Integer pageNum = 1;
	@ApiModelProperty("每页数目 默认为20")
	protected Integer pageSize = 20;
	@ApiModelProperty("排序方式")
	protected String orderBy;
}