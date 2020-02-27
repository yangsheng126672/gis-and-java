package com.jdrx.gis.api.basic;

import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.base.KeyWordDTO;
import com.jdrx.gis.beans.dto.base.PageDTO;
import com.jdrx.gis.beans.dto.base.TypeIdDTO;
import com.jdrx.gis.beans.dto.basic.BookMarkDTO;
import com.jdrx.gis.beans.dto.basic.DeleteBookMarkDTO;
import com.jdrx.gis.beans.dto.basic.MeasurementDTO;
import com.jdrx.gis.beans.dto.query.DevIDsForTypeDTO;
import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.service.basic.BasicDevQuery;
import com.jdrx.gis.service.basic.GisDevExtService;
import com.jdrx.gis.service.basic.ShareDevService;
import com.jdrx.gis.service.query.QueryDevService;
import com.jdrx.gis.util.RedisComponents;
import com.jdrx.platform.common.support.gateway.GwConstants;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

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

	@Autowired
	private RedisComponents redisComponents;

	@Autowired
	private QueryDevService queryDevService;

	@Autowired
	private ShareDevService shareDevService;

	@Autowired
	private GisDevExtService gisDevExtService;

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
	public ResposeVO findMeasurementList(@ApiParam(name = "dto", required = true)@RequestBody @Valid PageDTO dto,
										 @RequestHeader(name ="deptPath") String deptPath) throws BizException{
		Logger.debug("api/0/basic/findMeasurementList 获取所有测量列表");
		return ResponseFactory.ok(basicDevQuery.findMeasurementList(dto));
	}

	@ApiOperation(value = "保存测量结果")
	@RequestMapping(value ="saveMeasurement" )
	public ResposeVO saveMeasurement(@ApiParam(name = "dto", required = true)@RequestBody @Valid MeasurementDTO dto,
									 @RequestHeader(name ="deptPath") String deptPath) throws BizException{
		Logger.debug("api/0/basic/saveMeasurement 保存测量结果");
		return ResponseFactory.ok(basicDevQuery.saveMeasurement(dto,deptPath));
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

	@ApiOperation(value = "获取默认地图相关配置")
	@RequestMapping(value = "getDefaultLayers")
	public ResposeVO getDefaultLayers( @RequestHeader(name ="deptPath") String deptPath) throws BizException{
		Logger.debug("api/0/basic/getDefaultLayers获取默认地图相关配置");
		return ResponseFactory.ok(basicDevQuery.getDefaultLayers(deptPath));
	}

	@ApiOperation(value = "根据关键字搜索设备")
	@RequestMapping(value = "findObjectByString")
	public ResposeVO findObjectByString(@ApiParam(name = "dto", required = true) @RequestBody @Valid KeyWordDTO dto) throws BizException{
		Logger.debug("api/0/basic/findObjectByString根据关键字搜索设备");
		return ResponseFactory.ok(basicDevQuery.getFeaturesByString(dto.getKey()));
	}

	@ApiOperation(value = "根据勾选图层ID导出CAD", notes = "根据勾选图层ID导出CAD")
	@RequestMapping(value = "exportCAD", method = RequestMethod.POST)
	public ResposeVO exportCAD(@ApiParam(name = "dto", required = true) @RequestBody @Valid List<TypeIdDTO> dto) {
		try {
			String key = dto.get(0).getId().toString() + GISConstants.UNDER_LINE + dto.get(0).getTime();
			new Thread(() -> {
				try {
					String result = basicDevQuery.findLayerById(dto);
					redisComponents.set(key, result, GISConstants.DOWNLOAD_EXPIRE);
					Logger.debug("生成导出文件成功，key = {}", key);
				} catch (BizException e) {
					e.printStackTrace();
					Logger.error("导出CAD失败！{}", Thread.currentThread().getName());
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

	@ApiOperation(value = "查询下载CAD文件")
	@RequestMapping(value = "getDownLoadCAD")
	public ResposeVO getDownLoadCAD(@RequestBody List<TypeIdDTO> dto) throws BizException {
		Logger.debug("根据勾选图层ID导出CAD");
		String key = dto.get(0).getId().toString() + GISConstants.UNDER_LINE + dto.get(0).getTime();
		String result = queryDevService.getDownLoadFile(key);
		return ResponseFactory.ok(result);
	}

	@ApiOperation(value = "获取管网长度")
	@RequestMapping(value = "getPipeLength")
	public ResposeVO getPipeLength() throws BizException{
		Logger.debug("api/0/basic/getPipeLength 获取管网长度");
		return ResponseFactory.ok(basicDevQuery.getPipeLengthByDeptPath());
	}

	@ApiOperation(value = "根据DevId获取设备信息")
	@RequestMapping(value = "selectShareDevById")
	public ResposeVO selectShareDevById(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<String> dto) throws BizException{
		Logger.debug("api/0/basic/selectShareDevById 根据DevId获取设备信息");
		return ResponseFactory.ok(shareDevService.selectByPrimaryKey(dto.getId()));
	}

	@ApiOperation(value = "根据DevId获取经纬度")
	@RequestMapping(value = "getLngLatByDevId")
	public ResposeVO getLngLatByDevId(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<String> dto) {
		Logger.debug("api/0/basic/getLngLatByDevId 根据DevId获取经纬度");
		try {
			String lngLat = gisDevExtService.getLngLatByDevId(dto.getId());
			return ResponseFactory.ok(lngLat);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			return ResponseFactory.err("获取经纬度失败！", EApiStatus.ERR_SYS);
		}
	}

	@ApiOperation(value = "保存书签")
	@RequestMapping(value ="saveBookmark" )
	public ResposeVO saveBookmark(@ApiParam(name = "dto", required = true)@RequestBody @Valid BookMarkDTO dto,
								  @RequestHeader(name ="deptPath") String deptPath,
								  @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
								  @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws BizException{
		Logger.debug("api/0/basic/saveBookmark 保存书签");
		try{
			int result = basicDevQuery.saveBookmark(dto,deptPath,userId,token);
			if(result>0){
				return ResponseFactory.ok("保存成功");
			}
		}catch(BizException e){
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
               return ResponseFactory.err("保存失败",EApiStatus.ERR_SYS);
	}

	@ApiOperation(value = "删除书签")
	@RequestMapping(value ="deleteBookmark" )
	public ResposeVO deleteBookmark(@ApiParam(name = "dto", required = true)@RequestBody @Valid DeleteBookMarkDTO dto,
								  @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
								  @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws BizException{
		Logger.debug("api/0/basic/deleteBookmark 删除书签");
		try{
			int result = basicDevQuery.deleteBookmarkById(dto,userId,token);
			if(result>0){
				return ResponseFactory.ok("删除成功！");
			}
		}catch(BizException e){
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
		return ResponseFactory.err("删除失败",EApiStatus.ERR_SYS);
	}

	@ApiOperation(value = "获取所有书签列表")
	@RequestMapping(value = "findBookMarkList")
	public ResposeVO findBookMarkList(@ApiParam(name = "dto", required = true)@RequestBody @Valid PageDTO dto,
									  @RequestHeader(name ="deptPath") String deptPath
									  ) throws BizException{
		Logger.debug("api/0/basic/findBookMarkList 获取所有书签信息");
		return ResponseFactory.ok(basicDevQuery.findBookMarkList(dto,deptPath));
	}
}