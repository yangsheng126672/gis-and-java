package com.jdrx.api.basic;

import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import com.jdrx.service.basic.ShareDevTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description: 设备类型接口调用类
 * @Author: liaosijun
 * @Time: 2019/6/18 16:49
 */
@RestController
@Api("设备类型接口调用类")
@RequestMapping(value = "api/0/devtype", method = RequestMethod.POST)
public class ShareDevTypeApi {
	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ShareDevTypeApi.class);

	@Autowired
	private ShareDevTypeService shareDevTypeService;

	@ApiOperation(value = "类别为其他的二级子类")
	@RequestMapping(value = "findSonListOfOtherType")
	public ResposeVO findSonListOfOtherType(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto) throws BizException {
		Logger.debug("api/0/devtype/findSonListOfOtherType 类别为其他的二级子类");
		if (ObjectUtils.isEmpty(dto.getId())){
			Logger.debug("设备ID参数为空");
			return ResponseFactory.err("设备ID参数为空", EApiStatus.ERR_VALIDATE);
		}
		return ResponseFactory.ok(shareDevTypeService.findDevTypeListByTypeId(dto.getId()));
	}
}