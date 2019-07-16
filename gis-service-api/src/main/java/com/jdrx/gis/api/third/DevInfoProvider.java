package com.jdrx.gis.api.third;

import com.jdrx.gis.beans.dto.third.GetPipeTotalLenthDTO;
import com.jdrx.gis.service.query.QueryDevService;
import com.jdrx.platform.common.support.gateway.GwConstants;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Description: 提供给三方
 * @Author: liaosijun
 * @Time: 2019/7/16 11:00
 */
@RestController
@Api("属性查询服务")
@RequestMapping(value = "api/0/third", method = RequestMethod.POST)
public class DevInfoProvider {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DevInfoProvider.class);

	@Autowired
	private QueryDevService queryDevService;

	@ApiOperation(value = "根据时间段获取管段总长度")
	@RequestMapping(value = "findPipeTotalLength")
	public ResposeVO findPipeTotalLength(@RequestBody @Valid GetPipeTotalLenthDTO dto, @RequestHeader(GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws BizException {
		Logger.debug("api/0/third/findPipeTotalLength 根据时间段获取管段总长度");
		return ResponseFactory.ok(queryDevService.findPipeLength(dto));
	}

}