package com.jdrx.gis.api.query;

import com.jdrx.gis.beans.dto.query.AttrQeuryDTO;
import com.jdrx.gis.service.query.AttrQueryService;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Objects;

/**
 * @Description: 根据属性查设备信息
 * @Author: liaosijun
 * @Time: 2019/6/21 10:07
 */
@RestController
@Api("属性查询服务")
@RequestMapping(value = "api/0/attrQuery", method = RequestMethod.POST)
public class AttrQueryApi {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(AttrQueryApi.class);

	@Autowired
	AttrQueryService attrQueryService;

	@ApiOperation(value = "根据设备类型ID递归查询所有配置了模板的子类")
	@RequestMapping(value = "findHasTplDevTypeListById")
	public ResposeVO findHasTplDevTypeListById(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto)
			throws BizException {
		Logger.debug("api/0/query/findHasTplDevTypeListById 根据设备类型ID递归查询所有配置了模板的子类");
		return ResponseFactory.ok(attrQueryService.findHasTplDevTypeListById(dto.getId()));
	}

	@ApiOperation(value = "根据设备类型ID查模板配置信息")
	@RequestMapping(value = "findAttrListByTypeId")
	public ResposeVO findAttrListByTypeId(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto)
		throws BizException {
		Logger.debug("api/0/query/findAttrListByTypeId 根据设备类型ID查模板配置信息");
		return ResponseFactory.ok(attrQueryService.findAttrListByTypeId(dto.getId()));
	}


	@ApiOperation(value = "根据所选区域或属性键入的参数值查设备列表信息")
	@RequestMapping(value = "findDevListByAreaOrInputVal")
	public ResposeVO findDevListByAreaOrInputVal(@RequestBody @Valid AttrQeuryDTO dto) throws BizException {
		Logger.debug("api/0/query/findDevListByAreaOrInputVal 根据所选区域或属性键入的参数值查设备列表信息");
		if (Objects.isNull(dto)) {
			throw new BizException("参数为空");
		}
		return ResponseFactory.ok(attrQueryService.findDevListByAreaOrInputVal(dto));
	}

	@ApiOperation(value = "导出根据所选区域或属性键入的参数值所查询设备列表信息")
	@RequestMapping(value = "exportDevListByAreaOrInputVal")
	public ResposeVO exportDevListByAreaOrInputVal(@RequestBody @Valid AttrQeuryDTO dto,HttpServletResponse response) throws BizException {
		Logger.debug("api/0/query/exportDevListByAreaOrInputVal 导出根据所选区域或属性键入的参数值所查询设备列表信息");
		return ResponseFactory.ok(attrQueryService.exportDevListByAreaOrInputVal(dto, response));
	}


	@ApiOperation(value = "根据所选区域或属性键入的参数值查设备列表信息(分页)")
	@RequestMapping(value = "findDevListPageByAreaOrInputVal")
	public ResposeVO findDevListPageByAreaOrInputVal(@RequestBody @Valid AttrQeuryDTO dto) throws BizException {
		Logger.debug("api/0/query/findDevListPageByAreaOrInputVal 根据所选区域或属性键入的参数值查设备列表信息，分页");
		if (Objects.isNull(dto)) {
			throw new BizException("参数为空");
		}
		return ResponseFactory.ok(attrQueryService.findDevListPageByAreaOrInputVal(dto));
	}

	@ApiOperation(value = "根据管径范围值获取对应的图层url")
	@RequestMapping(value = "findCaliberLayerUrl")
	public ResposeVO findCaliberLayerUrl(@RequestBody @Valid String num) throws BizException {
		if (Objects.isNull(num)) {
			throw new BizException("参数为空");
		}
		return ResponseFactory.ok(attrQueryService.findCiliberLayerUrlByNum(num));
	}

	@ApiOperation(value = "根据管材值获取对应的图层url")
	@RequestMapping(value = "findMeterialLayerUrl")
	public ResposeVO findMeterialLayerUrl(@RequestBody @Valid String type) throws BizException {
		if (Objects.isNull(type)) {
			throw new BizException("参数为空");
		}
		return ResponseFactory.ok(attrQueryService.findMeterialLayerUrlByNum(type));
	}


}