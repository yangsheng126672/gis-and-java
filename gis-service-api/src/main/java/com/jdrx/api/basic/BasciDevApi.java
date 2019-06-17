package com.jdrx.api.basic;

import com.jdrx.beans.entry.basic.GISDevExtPO;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import com.jdrx.service.basic.BasicDevQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description: 图层展示
 * @Author: liaosijun
 * @Time: 2019/6/14 11:27
 */
@RestController
@Api("基本图层查询服务")
@RequestMapping(value = "api/0/basic", method = RequestMethod.POST)
public class BasciDevApi {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(BasciDevApi.class);

	@Autowired
	private BasicDevQuery basicDevQuery;

	@ApiOperation(value = "获取所有设备信息")
	@RequestMapping(value = "findAllDevType")
	public ResposeVO findAllDevType() throws BizException{
		return ResponseFactory.ok(basicDevQuery.fiandAllDevType());
	}

	@ApiOperation(value = "根据所勾选类型查数据")
	@RequestMapping(value = "findDevByDevId")
	public ResposeVO findDevByTypeID(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto) throws BizException{
		if (StringUtils.isEmpty(dto.getId())){
			Logger.debug("设备ID参数为空");
			return ResponseFactory.err("设备ID参数为空", EApiStatus.ERR_VALIDATE);
		}
		GISDevExtPO gisDevExtPO =  basicDevQuery.getDevById(dto.getId());
		return ResponseFactory.ok(gisDevExtPO);
	}
}