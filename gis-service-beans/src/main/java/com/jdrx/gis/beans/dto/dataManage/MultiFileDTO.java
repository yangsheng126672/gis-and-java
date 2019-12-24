package com.jdrx.gis.beans.dto.dataManage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


/**
 * @Author: liaosijun
 * @Time: 2019/12/23 13:10
 */
@Data
public class MultiFileDTO {

	@ApiModelProperty("文件列表")
	@NotEmpty(message = "文件列表不能为空")
	private MultipartFile[] files;

	@ApiModelProperty("设备ID")
	@NotBlank(message = "设备ID不能为空")
	private String devId ;

}
