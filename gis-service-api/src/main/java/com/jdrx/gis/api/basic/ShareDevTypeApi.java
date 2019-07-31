package com.jdrx.gis.api.basic;

import com.jdrx.gis.beans.dto.query.DevIDsAndTypeDTO;
import com.jdrx.gis.service.basic.ShareDevTypeService;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
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

	@ApiOperation(value = "查询当前类型的二级子类")
	@RequestMapping(value = "findSonListOfOtherType")
	public ResposeVO findSonListOfOtherType(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto) throws BizException {
		Logger.debug("api/0/devtype/findSonListOfOtherType 查询当前类型的二级子类");
		if (ObjectUtils.isEmpty(dto.getId())){
			Logger.debug("设备ID参数为空");
			return ResponseFactory.err("设备ID参数为空", EApiStatus.ERR_VALIDATE);
		}
		return ResponseFactory.ok(shareDevTypeService.findDevTypeListByTypeId(dto.getId()));
	}

	@ApiOperation(value = "根据传来的设备类型和设备IDs获取叶子级别的类型")
	@RequestMapping(value = "findLeafTypesByDevIds")
	public ResposeVO findLeafTypesByDevIds(@RequestBody DevIDsAndTypeDTO dto) throws BizException {
		Logger.debug("根据传来的设备类型和设备IDs获取叶子级别的类型 dto={}", dto.toString());
		return ResponseFactory.ok(shareDevTypeService.findLeafTypesByDevIds(dto));
	}
}