package com.jdrx.api.query;

import com.jdrx.beans.dto.query.QueryDevDTO;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import com.jdrx.service.query.QueryDevService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Description: gis页面查询服务
 * @Author: liaosijun
 * @Time: 2019/6/12 11:01
 */
@RestController
@Api("空间查询服务")
@RequestMapping(value = "api/0/query", method = RequestMethod.POST)
public class QueryDevApi {

	private static final Logger Logger = LoggerFactory.getLogger(QueryDevApi.class);

	@Autowired
	private QueryDevService queryDevService;

	@ApiOperation(value = "获取所有图层对应设备个数")
	@RequestMapping(value = "findFirstHierarchyDevTypeNum")
	public ResposeVO findFirstHierarchyDevTypeNum() throws BizException{
		Logger.debug("api/0/query/findFirstHierarchyDevTypeNum 获取所有图层对应设备个数");
		return ResponseFactory.ok(queryDevService.findFirstHierarchyDevTypeNum());
	}

	@ApiOperation(value = "根据设备类型ID查设备列表信息")
	@RequestMapping(value = "findDevListByTypeID")
	public ResposeVO findDevByTypeID(@ApiParam(name = "dto", required = true) @RequestBody @Valid QueryDevDTO dto) throws BizException{
		Logger.debug("api/0/query/findDevListByTypeID 根据设备类型ID查设备列表信息");
		return ResponseFactory.ok(queryDevService.findDevListByTypeID(dto.getId()));
	}

	@ApiOperation(value = "根据设备类型ID查表格列名")
	@RequestMapping(value = "findFieldNamesByTypeID")
	public ResposeVO findFieldNamesByTypeID(@ApiParam(name = "dto", required = true) @RequestBody @Valid QueryDevDTO dto) throws BizException{
		Logger.debug("api/0/query/findFieldNamesByTypeID 根据设备类型ID查表格列名");
		return ResponseFactory.ok(queryDevService.findFieldNamesByTypeID(dto.getId()));
	}

	@ApiOperation(value = "水管类型的水管口径数量统计")
	@RequestMapping(value = "findWaterPipeCaliberSum")
	public ResposeVO findWaterPipeCaliberSum() throws BizException {
		Logger.debug("api/0/query/findWaterPipeCaliberSum 水管口径数量统计");
		return ResponseFactory.ok(queryDevService.findWaterPipeCaliberSum());
	}

	@ApiOperation(value = "当前设备类型下的子类型设备个数")
	@RequestMapping(value = "findSonsNumByPid")
	public ResposeVO findSonsNumByPid(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto)
		throws BizException {
		if (ObjectUtils.isEmpty(dto.getId())){
			Logger.debug("设备ID参数为空");
			return ResponseFactory.err("设备ID参数为空", EApiStatus.ERR_VALIDATE);
		}
		return ResponseFactory.ok(queryDevService.findSonsNumByPid(dto.getId()));
	}

	@ApiOperation(value = "导出空间查询信息", notes = "导出空间查询信息" , produces="application/octet-stream")
	@RequestMapping(value = "exportDevInfoByPID", method = RequestMethod.POST)
	public void export(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto,
	                   HttpServletResponse response) throws Exception {
		queryDevService.exportDevInfoByPID(response, dto.getId());
	}
}