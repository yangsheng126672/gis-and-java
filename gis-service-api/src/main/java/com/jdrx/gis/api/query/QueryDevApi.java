package com.jdrx.gis.api.query;

import com.jdrx.gis.beans.dto.query.ExpRangeTypeDTO;
import com.jdrx.gis.beans.dto.query.RangeDTO;
import com.jdrx.gis.beans.dto.query.RangeTypeDTO;
import com.jdrx.gis.beans.dto.query.TypeIDDTO;
import com.jdrx.gis.service.query.QueryDevService;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
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

	@ApiOperation(value = "获取划定范围内图层对应设备个数")
	@RequestMapping(value = "findFirstHierarchyDevTypeNum")
	public ResposeVO findFirstHierarchyDevTypeNum(@ApiParam(name = "dto", required = true) @RequestBody @Valid RangeDTO rangeDTO) throws BizException{
		Logger.debug("api/0/query/findFirstHierarchyDevTypeNum 获取所有图层对应设备个数 dto = {}", rangeDTO.toString());
		return ResponseFactory.ok(queryDevService.findFirstHierarchyDevTypeNum(rangeDTO));
	}

	@ApiOperation(value = "根据设备类型ID和划取的经纬度范围查设备列表信息(分页)")
	@RequestMapping(value = "findDevListPageByTypeID")
	public ResposeVO findDevListPageByTypeID(@ApiParam(name = "dto", required = true) @RequestBody @Valid RangeTypeDTO dto) throws BizException{
		Logger.debug("api/0/query/findDevListByTypeID 根据设备类型ID和划取的经纬度范围查设备列表信息");
		return ResponseFactory.ok(queryDevService.findDevListPageByTypeID(dto));
	}

	@ApiOperation(value = "根据设备类型ID查表格列名")
	@RequestMapping(value = "findFieldNamesByTypeID")
	public ResposeVO findFieldNamesByTypeID(@ApiParam(name = "dto", required = true) @RequestBody @Valid TypeIDDTO dto) throws BizException{
		Logger.debug("api/0/query/findFieldNamesByTypeID 根据设备类型ID查表格列名");
		return ResponseFactory.ok(queryDevService.findFieldNamesByTypeID(dto.getTypeId()));
	}

	@ApiOperation(value = "水管类型的水管口径数量统计")
	@RequestMapping(value = "findWaterPipeCaliberSum")
	public ResposeVO findWaterPipeCaliberSum(@ApiParam(name = "dto", required = true) @RequestBody @Valid RangeDTO rangeDTO) throws BizException {
		Logger.debug("api/0/query/findWaterPipeCaliberSum 水管口径数量统计 {}", rangeDTO.toString());
		return ResponseFactory.ok(queryDevService.findWaterPipeCaliberSum(rangeDTO));
	}

	@ApiOperation(value = "当前设备类型下的子类型设备个数")
	@RequestMapping(value = "findSonsNumByPid")
	public ResposeVO findSonsNumByPid(@ApiParam(name = "dto", required = true) @RequestBody @Valid ExpRangeTypeDTO dto)
		throws BizException {
		if (ObjectUtils.isEmpty(dto.getTypeId())){
			Logger.debug("设备类型ID参数为空");
			return ResponseFactory.err("设备类型ID参数为空", EApiStatus.ERR_VALIDATE);
		}
		Logger.debug("api/0/query/findSonsNumByPid 当前设备类型下的子类型设备个数");
		return ResponseFactory.ok(queryDevService.findSonsNumByPid(dto));
	}

	@ApiOperation(value = "导出空间查询信息", notes = "导出空间查询信息", produces="application/octet-stream")
	@RequestMapping(value = "exportDevListByPID", method = RequestMethod.POST)
	public ResposeVO export(@ApiParam(name = "dto", required = true) @RequestBody @Valid ExpRangeTypeDTO dto,
	                   HttpServletResponse response) throws Exception {
		return ResponseFactory.ok(queryDevService.exportDevInfoByPID(response, dto));
	}

	/**
	@ApiOperation(value = "根据设备ID集合查设备列表数据(分页)", notes = "根据设备ID集合查设备列表数据(分页)")
	@RequestMapping(value = "findDevListPageByDevIDs", method = RequestMethod.POST)
	public ResposeVO findDevListPageByDevIDs(@ApiParam(name = "dto", required = true) @RequestBody @Valid QueryDevDTO devDTO)
		throws BizException {
		Logger.debug("api/0/query/findDevListPageByDevIDs 根据设备ID集合查设备列表数据(分页)");
		if (Objects.isNull(devDTO)){
			throw new BizException("参数为空");
		}
		return ResponseFactory.ok(queryDevService.findDevListPageByDevIDs(devDTO));
	}*/

	/**
	 *  查所有数据太多，用分页
	@ApiOperation(value = "根据设备ID集合查设备列表数据", notes = "根据设备ID集合查设备列表数据")
	@RequestMapping(value = "findDevListByDevIDs", method = RequestMethod.POST)
	public ResposeVO findDevListByDevIDs(@ApiParam(name = "dto", required = true) @RequestBody @Valid QueryDevDTO devDTO)
			throws BizException {
		Logger.debug("api/0/query/findDevListByDevIDs 根据设备ID集合查设备列表数据");
		if (Objects.isNull(devDTO)){
			throw new BizException("参数为空");
		}
		return ResponseFactory.ok(queryDevService.findDevListByDevIDs(devDTO.getDevIds()));
	}**/
}