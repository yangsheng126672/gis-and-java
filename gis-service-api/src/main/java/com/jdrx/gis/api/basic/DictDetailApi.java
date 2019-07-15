package com.jdrx.gis.api.basic;

import com.jdrx.gis.beans.dto.base.InsertDTO;
import com.jdrx.gis.beans.dto.base.UpdateDTO;
import com.jdrx.gis.beans.dto.basic.DictDetailDTO;
import com.jdrx.gis.beans.dto.basic.DictQueryByValDTO;
import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.beans.vo.basic.DictDetailVO;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Description: 数据字典Api
 * @Author: liaosijun
 * @Time: 2019/6/27 13:18
 */
@RestController
@Api("数据字典接口")
@RequestMapping(value = "api/0/dictDetail", method = RequestMethod.POST)
public class DictDetailApi {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DictDetailApi.class);

	@Autowired
	private DictDetailService dictDetailService;


	@ApiOperation(value = "新增字典")
	@RequestMapping(value = "addDictDetail")
	public ResposeVO addDictType(@ApiParam(name = "dictDetailDto", required = true) @RequestBody @Validated({InsertDTO.class})
			                             DictDetailDTO dictDetailDTO) throws BizException {
		Logger.debug("api/0/dictDetail/addDictDetail 新增字典 {}", dictDetailDTO.toString());
		Boolean b = dictDetailService.addDictDetail(dictDetailDTO);
		return ResponseFactory.ok(b);
	}

	@ApiOperation(value = "根据ID删除字典数据")
	@RequestMapping(value = "delDictDetailById")
	public ResposeVO delDictDetailById(@ApiParam(name = "id", required = true) @RequestBody @Valid
			                                 IdDTO<Long> idDTO) throws BizException {
		Logger.debug("api/0/dictDetail/delDictDetailById 根据ID删除字典数据 {}", idDTO.getId());
		Boolean b = dictDetailService.delDictDetailById(idDTO.getId());
		return ResponseFactory.ok(b);
	}

	@ApiOperation(value = "修改字典")
	@RequestMapping(value = "updateDictDetailById")
	public ResposeVO updateDictDetailById(@ApiParam(name = "dictDetailDto", required = true) @RequestBody @Validated({UpdateDTO.class})
			                                DictDetailDTO dictDetailDTO) throws BizException {
		Logger.debug("api/0/dictDetail/updateDictDetailById 修改字典类型 {}", dictDetailDTO.toString());
		Boolean b = dictDetailService.updateDictType(dictDetailDTO);
		return ResponseFactory.ok(b);
	}

	@ApiOperation(value = "根据ID查字典数据")
	@RequestMapping(value = "getDictDetailById")
	public ResposeVO getDictDetailById(@ApiParam(name = "id", required = true) @RequestBody @Valid
			                                 IdDTO<Long> idDTO) throws BizException {
		Logger.debug("api/0/dictDetail/getDictDetailById 根据ID查字典数据 {}", idDTO.getId());
		DictDetailVO dictDetailVO = dictDetailService.getDictTypeById(idDTO.getId());
		return ResponseFactory.ok(dictDetailVO);
	}


	/**
	@ApiModelProperty(value = "根据设备类型ID集查图层服务的URL列表")
	@RequestMapping(value = "findLayerUrlListByTypeIds")
	public ResposeVO findLayerUrlListByTypeIds(@ApiParam(name = "typeIds", required = true) @RequestBody @Valid
	                                           Long[] typeIds) throws BizException {
		Logger.debug("api/0/dictDetail/findLayerUrlListByTypeIds 根据设备类型ID集查图层服务的URL列表 {}", typeIds.toString());
		List<Map<String,String>> list = dictDetailService.findLayerUrlListByTypeIds(typeIds);
		return ResponseFactory.ok(list);
	}**/


	@ApiOperation(value = "根据typeID查字典数据")
	@RequestMapping(value = "findDictDetailListByTypeId")
	public ResposeVO findDictDetailListByTypeId(@ApiParam(name = "id", required = true) @RequestBody @Valid
			                                   IdDTO<Long> idDTO) throws BizException {
		Logger.debug("api/0/dictDetail/findDictDetailListByTypeId 根据typeID查字典数据 {}", idDTO.getId());
		List<DictDetailPO> list = dictDetailService.findDictDetailListByTypeId(idDTO.getId());
		return ResponseFactory.ok(list);
	}

	@ApiOperation(value = "根据参数值查字典数据")
	@RequestMapping(value = "findDictDetailListByVal")
	public ResposeVO findDictDetailListByVal(@ApiParam(name = "dto", required = true) @RequestBody @Valid
			                                         DictQueryByValDTO dto) throws BizException {
		Logger.debug("api/0/dictDetail/findDictDetailListByVal 根据参数值查字典数据 {}", dto.toString());
		List<DictDetailPO> list = dictDetailService.findDetailsByTypeVal(dto.getVal());
		return ResponseFactory.ok(list);
	}
}