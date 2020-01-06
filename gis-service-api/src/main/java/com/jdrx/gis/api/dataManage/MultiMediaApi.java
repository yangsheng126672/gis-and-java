package com.jdrx.gis.api.dataManage;

import com.jdrx.gis.beans.dto.dataManage.MediaDTO;
import com.jdrx.gis.service.dataManage.MultiMediaService;
import com.jdrx.platform.common.support.gateway.GwConstants;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: liaosijun
 * @Time: 2019/12/23 11:18
 */
@RestController
@Api("设备图片和视频操作")
@RequestMapping(value = "api/0/multiMedia", method = RequestMethod.POST)
public class MultiMediaApi {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(MultiMediaApi.class);

	@Autowired
	private MultiMediaService multiMediaService;

	@ApiOperation(value = "上传文件并获取Url")
	@RequestMapping(value ="uploadFilesAndGetUrls")
	public ResposeVO uploadFilesAndGetUrls(@RequestParam(value = "file") MultipartFile file) {
		Logger.debug("api/0/multiMedia/uploadFilesAndGetUrls 上传文件并获取Url");
		try {
			String url = multiMediaService.uploadFilesAndGetUrls(file);
			return ResponseFactory.ok(url);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseFactory.err("上传文件失败！", EApiStatus.ERR_SYS);
		}
	}

	@ApiOperation(value = "保存多媒体信息")
	@RequestMapping(value ="saveMultiMedia")
	public ResposeVO saveMultiMedia(@ApiParam(name = "dto", required = true) @RequestBody @Valid MediaDTO dto,
	                                @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
	                                @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) {
		Logger.debug("api/0/multiMedia/saveMultiMedia 保存多媒体信息");
		try {
			int affect = multiMediaService.saveMultiMedia(dto, userId, token);
			if (affect > 0) {
				return ResponseFactory.ok("保存成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
		return ResponseFactory.err("保存失败", EApiStatus.ERR_SYS.getMessage());
	}
}
