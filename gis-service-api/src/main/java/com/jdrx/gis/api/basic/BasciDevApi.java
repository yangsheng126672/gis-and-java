package com.jdrx.gis.api.basic;

import com.jdrx.gis.beans.dto.base.KeyWordDTO;
import com.jdrx.gis.beans.dto.base.PageDTO;
import com.jdrx.gis.beans.dto.basic.MeasurementDTO;
import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.service.basic.BasicDevQuery;
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

	@ApiOperation(value = "获取首层图层")
	@RequestMapping(value = "findFirstHierarchyDevType")
	public ResposeVO findFirstHierarchyDevType() throws BizException{
		Logger.debug("api/0/basic/findFirstHierarchyDevType 获取首层图层");
		return ResponseFactory.ok(basicDevQuery.findFirstHierarchyDevType());
	}

	@ApiOperation(value = "获取所有设备类型列表")
	@RequestMapping(value = "findDevTypeList")
	public ResposeVO findDevTypeList() throws BizException{
		Logger.debug("api/0/basic/findDevTypeList 获取所有设备类型列表");
		return ResponseFactory.ok(basicDevQuery.findDevTypeList());
	}


	@ApiOperation(value = "获取所有图例图标类型列表")
	@RequestMapping(value = "findIconTypeList")
	public ResposeVO findIconTypeList() throws BizException{
		Logger.debug("api/0/basic/findIconTypeList 获取所有图例图标类型列表");
		return ResponseFactory.ok(basicDevQuery.findDevTypeIconList());
	}

	@ApiOperation(value = "根据设备ID查当前设备的属性信息")
	@RequestMapping(value = "getDevExtByDevId")
	public ResposeVO getDevExtByDevId(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<String> dto) throws BizException{
		if (ObjectUtils.isEmpty(dto.getId())){
			Logger.debug("设备ID参数为空");
			return ResponseFactory.err("设备ID参数为空", EApiStatus.ERR_VALIDATE);
		}
		Logger.debug("api/0/basic/getDevExtByDevId 根据设备ID查当前设备的属性信息");
		GISDevExtPO gisDevExtPO =  basicDevQuery.getDevExtByDevId(dto.getId());
		return ResponseFactory.ok(gisDevExtPO);
	}

	@ApiOperation(value = "获取所有测量列表")
	@RequestMapping(value = "findMeasurementList")
	public ResposeVO findMeasurementList(@ApiParam(name = "dto", required = true)@RequestBody @Valid PageDTO dto) throws BizException{
		Logger.debug("api/0/basic/findMeasurementList 获取所有测量列表");
		return ResponseFactory.ok(basicDevQuery.findMeasurementList(dto));
	}

	@ApiOperation(value = "保存测量结果")
	@RequestMapping(value ="saveMeasurement" )
	public ResposeVO saveMeasurement(@ApiParam(name = "dto", required = true)@RequestBody @Valid MeasurementDTO dto) throws BizException{
		Logger.debug("api/0/basic/saveMeasurement 保存测量结果");
		return ResponseFactory.ok(basicDevQuery.saveMeasurement(dto));
	}

	@ApiOperation(value = "删除测量信息")
	@RequestMapping(value ="deleteMeasurementByID" )
	public ResposeVO deleteMeasurementByID(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto) throws BizException {
		Logger.debug("api/0/basic/deleteMeasurementByID 删除测量信息");
		return  ResponseFactory.ok(basicDevQuery.deleteMeasurementByID(dto.getId()));

	}

	@ApiOperation(value = "获取所有点线要素空间信息及dev_id")
	@RequestMapping(value = "getAllFeaturesUrl")
	public ResposeVO getAllFeaturesUrl() throws BizException{
		Logger.debug("api/0/basic/getAllFeatures 获取所有点线要素空间信息及dev_id");
		return ResponseFactory.ok(basicDevQuery.getAllFeaturesUrl());
	}

	@ApiOperation(value = "获取巡检系统所有资源url")
	@RequestMapping(value = "getAllSourceUrl")
	public ResposeVO getAllSourceUrl() throws BizException{
		Logger.debug("api/0/basic/getAllSourceUrl 获取巡检系统所有资源url");
		return ResponseFactory.ok(basicDevQuery.getXjLayerSourceUrl());
	}

	@ApiOperation(value = "获取默认地图相关配置")
	@RequestMapping(value = "getDefaultLayers")
	public ResposeVO getDefaultLayers() throws BizException{
		Logger.debug("api/0/basic/getDefaultLayers获取默认地图相关配置");
		return ResponseFactory.ok(basicDevQuery.getDefaultLayers());
	}

	@ApiOperation(value = "根据关键字搜索设备")
	@RequestMapping(value = "findObjectByString")
	public ResposeVO findObjectByString(@ApiParam(name = "dto", required = true) @RequestBody @Valid KeyWordDTO dto) throws BizException{
		Logger.debug("api/0/basic/findObjectByString根据关键字搜索设备");
		return ResponseFactory.ok(basicDevQuery.getFeaturesByString(dto.getKey()));
	}


}