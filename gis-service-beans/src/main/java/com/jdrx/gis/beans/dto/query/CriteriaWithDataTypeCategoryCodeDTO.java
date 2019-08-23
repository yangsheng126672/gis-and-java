package com.jdrx.gis.beans.dto.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: TODO
 * @Author: liaosijun
 * @Time: 2019/8/21 18:03
 */
@Data
public class CriteriaWithDataTypeCategoryCodeDTO {

	@ApiModelProperty("数据类型的分类编码，N-数字，S-字符，D-日期时间")
	private String dataTypeCategoryCode;

	@ApiModelProperty("条件值")
	private String criteria;

	@ApiModelProperty("英文字段")
	private String fieldName;

	// 根据前面的条件组装成该字段
	private String assemblyStr;
}