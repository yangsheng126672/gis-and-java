package com.jdrx.gis.beans.dto.dataManage;

import com.jdrx.gis.beans.dto.base.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: liaosijun
 * @Time: 2019/12/5 16:29
 */
@Data
public class QueryAuditDTO extends PageDTO {

	@ApiModelProperty("设备编码")
	private String code;

	@ApiModelProperty("上报人")
	private String createBy;

	@ApiModelProperty("查询开始日期，格式YYYY-MM-DD")
	private String startDate;

	@ApiModelProperty("查询截止日期，格式YYYY-MM-DD")
	private String endDate;
}
