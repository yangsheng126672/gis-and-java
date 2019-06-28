package com.jdrx.gis.api.basic;

import com.jdrx.gis.beans.entry.basic.GISDevExtPO;
import com.jdrx.gis.beans.entry.basic.MeasurementPO;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import com.jdrx.gis.service.basic.BasicDevQuery;
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
		return ResponseFactory.ok(basicDevQuery.findDevTypeList());
	}

	@ApiOperation(value = "根据设备ID查当前设备的属性信息")
	@RequestMapping(value = "getDevExtByDevId")
	public ResposeVO getDevExtByDevId(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto) throws BizException{
		if (ObjectUtils.isEmpty(dto.getId())){
			Logger.debug("设备ID参数为空");
			return ResponseFactory.err("设备ID参数为空", EApiStatus.ERR_VALIDATE);
		}
		GISDevExtPO gisDevExtPO =  basicDevQuery.getDevExtByDevId(dto.getId());
		return ResponseFactory.ok(gisDevExtPO);
	}

	@ApiOperation(value = "获取所有测量列表")
	@RequestMapping(value = "findMeasurementList")
	public ResposeVO findMeasurementList() throws BizException{
		return ResponseFactory.ok(basicDevQuery.findMeasurementList());
	}

	@ApiOperation(value = "保存测量结果")
	@RequestMapping(value ="saveMeasurement" )
	public ResposeVO saveMeasurement(@ApiParam(name = "dto", required = true)@RequestBody @Valid MeasurementPO dto){
		return ResponseFactory.ok(basicDevQuery.saveMeasurement(dto));
	}

	@ApiOperation(value = "删除测量信息")
	@RequestMapping(value ="deleteMeasurementByID" )
	public ResposeVO deleteMeasurementByID(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto){
		return  ResponseFactory.ok(basicDevQuery.deleteMeasurementByID(dto.getId()));

	}

}