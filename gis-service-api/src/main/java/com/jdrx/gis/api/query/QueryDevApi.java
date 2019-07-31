package com.jdrx.gis.api.query;

import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.query.*;
import com.jdrx.gis.service.query.QueryDevService;
import com.jdrx.gis.util.RedisComponents;
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
	@Autowired
	private RedisComponents redisComponents;

	@ApiOperation(value = "根据传来的设备集合获取第一级图层和图层对应的设备个数")
	@RequestMapping(value = "findFirstHierarchyDevTypeNum")
	public ResposeVO findFirstHierarchyDevTypeNum(@ApiParam(name = "dto", required = true) @RequestBody @Valid DevIDsDTO devIDsDTO) throws BizException{
		Logger.debug("api/0/query/findFirstHierarchyDevTypeNum 根据传来的设备集合获取第一级图层和图层对应的设备个数 dto = {}", devIDsDTO.toString());
		return ResponseFactory.ok(queryDevService.findFirstHierarchyDevTypeNum(devIDsDTO));
	}

//	@ApiOperation(value = "根据设备类型ID和划取的经纬度范围查设备列表信息(分页)")
//	@RequestMapping(value = "findDevListPageByTypeID")
//	public ResposeVO findDevListPageByTypeID(@ApiParam(name = "dto", required = true) @RequestBody @Valid RangeTypeDTO dto) throws BizException{
//		Logger.debug("api/0/query/findDevListByTypeID 根据设备类型ID和划取的经纬度范围查设备列表信息");
//		return ResponseFactory.ok(queryDevService.findDevListPageByTypeID(dto));
//	}

	@ApiOperation(value = "根据设备类型ID和划设备ID集合查设备列表信息(分页)")
	@RequestMapping(value = "findDevListPageByTypeID")
	public ResposeVO findDevListPageByTypeID(@ApiParam(name = "dto", required = true) @RequestBody @Valid DevIDsForTypeDTO dto) throws BizException {
		Logger.debug("api/0/query/findDevListByTypeID 根据设备类型ID和划设备ID集合查设备列表信息");
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
	public ResposeVO findSonsNumByPid(@ApiParam(name = "dto", required = true) @RequestBody @Valid DevIDsForTypeDTO dto)
		throws BizException {
		if (ObjectUtils.isEmpty(dto.getTypeId())){
			Logger.debug("设备类型ID参数为空");
			return ResponseFactory.err("设备类型ID参数为空", EApiStatus.ERR_VALIDATE);
		}
		Logger.debug("api/0/query/findSonsNumByPid 当前设备类型下的子类型设备个数");
		return ResponseFactory.ok(queryDevService.findSonsNumByPid(dto));
	}

	@ApiOperation(value = "导出空间查询信息", notes = "导出空间查询信息")
	@RequestMapping(value = "exportDevListByPID", method = RequestMethod.POST)
	public ResposeVO export(@ApiParam(name = "dto", required = true) @RequestBody @Valid DevIDsForTypeDTO dto) {
		try {
			String key = dto.getTypeId() + GISConstants.UNDER_LINE + dto.getTime();
			new Thread(() -> {
				try {
					String result = queryDevService.exportDevInfoByPID(dto);
					redisComponents.set(key, result, GISConstants.DOWNLOAD_EXPIRE);
					Logger.debug("生成导出文件成功，key = {}", key);
				} catch (BizException e) {
					e.printStackTrace();
					Logger.error("导出设备列表信息失败！{}", Thread.currentThread().getName());
					redisComponents.set(key, EApiStatus.ERR_SYS.getStatus(), 60);
					try {
						throw new BizException(e);
					} catch (BizException e1) {
						e1.printStackTrace();
					}
				}
			}).start();
			return ResponseFactory.ok(Boolean.TRUE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseFactory.err("文件生成中...", EApiStatus.ERR_SYS);
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

	@ApiOperation(value = "查询下载文件")
	@RequestMapping(value = "getDownLoadFile")
	public ResposeVO getDownLoadFile(@RequestBody DevIDsForTypeDTO dto) throws BizException {
		Logger.debug("根据划定范围和类型获取导出文件的存放路径");
		String key = dto.getTypeId() + GISConstants.UNDER_LINE + dto.getTime();
		String result = queryDevService.getDownLoadFile(key);
		return ResponseFactory.ok(result);
	}

	@ApiOperation(value = "根据传来的设备类型和设备ID筛选设备类型下的设备列表信息")
	@RequestMapping(value = "findDevListByTypeIdsAndDevIds")
	public ResposeVO findDevListByTypeIdsAndDevIds(@RequestBody DevIDsForTypesDTO dto) throws BizException {
		Logger.debug("根据传来的设备类型和设备ID筛选设备类型下的设备列表信息 dto={}", dto.toString());
		return ResponseFactory.ok(queryDevService.findDevListByTypeIdsAndDevIds(dto));
	}
}