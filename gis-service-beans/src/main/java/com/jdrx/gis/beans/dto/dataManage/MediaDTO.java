package com.jdrx.gis.beans.dto.dataManage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Author: liaosijun
 * @Time: 2020/1/2 16:48
 */
@Data
public class MediaDTO {

	@ApiModelProperty("设备ID")
	@NotBlank
	private String devId;

	@ApiModelProperty("图片url集合")
	private List<String> picUrls;

	@ApiModelProperty("视频url集合")
	private List<String> videoUrls;
}
