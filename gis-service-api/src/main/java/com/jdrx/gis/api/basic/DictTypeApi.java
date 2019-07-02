package com.jdrx.gis.api.basic;

import com.jdrx.gis.beans.dto.base.InsertDTO;
import com.jdrx.gis.beans.dto.base.UpdateDTO;
import com.jdrx.gis.beans.dto.basic.DictTypeDTO;
import com.jdrx.gis.beans.vo.basic.DictTypeVO;
import com.jdrx.gis.service.basic.DictTypeService;
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

/**
 * @Description: 数据字典类型接口
 * @Author: liaosijun
 * @Time: 2019/6/27 15:17
 */
@RestController
@Api("数据字典类型接口")
@RequestMapping(value = "api/0/dictType", method = RequestMethod.POST)
public class DictTypeApi {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DictTypeApi.class);

	@Autowired
	DictTypeService dictTypeService;

	@ApiOperation(value = "新增字典类型")
	@RequestMapping(value = "addDictType")
	public ResposeVO addDictType(@ApiParam(name = "dictTypeDto", required = true) @RequestBody @Validated({InsertDTO.class})
	                             DictTypeDTO dictTypeDTO) throws BizException {
		Logger.debug("api/0/dictType/addDictType 新增字典类型 {}", dictTypeDTO.toString());
		Boolean b = dictTypeService.addDictType(dictTypeDTO);
		return ResponseFactory.ok(b);
	}

	@ApiOperation(value = "根据ID删除字典类型数据")
	@RequestMapping(value = "delDictTypeById")
	public ResposeVO delDictTypeById(@ApiParam(name = "id", required = true) @RequestBody @Valid
	                                 IdDTO<Long> idDTO) throws BizException {
		Logger.debug("api/0/dictType/delDictType 根据ID删除字典类型数据 {}", idDTO.getId());
		Boolean b = dictTypeService.delDictTypeById(idDTO.getId());
		return ResponseFactory.ok(b);
	}

	@ApiOperation(value = "修改字典类型")
	@RequestMapping(value = "updateDictTypeById")
	public ResposeVO updateDictType(@ApiParam(name = "dictTypeDto", required = true) @RequestBody @Validated(UpdateDTO.class)
			                                    DictTypeDTO dictTypeDTO) throws BizException {
		Logger.debug("api/0/dictType/updateDictType 修改字典类型 {}", dictTypeDTO.toString());
		Boolean b = dictTypeService.updateDictType(dictTypeDTO);
		return ResponseFactory.ok(b);
	}

	@ApiOperation(value = "根据ID查字典类型数据")
	@RequestMapping(value = "getDictTypeById")
	public ResposeVO getDictTypeById(@ApiParam(name = "id", required = true) @RequestBody @Valid
			                                     IdDTO<Long> idDTO) throws BizException {
		Logger.debug("api/0/dictType/getDictTypeById 根据ID查字典类型数据 {}", idDTO.getId());
		DictTypeVO dictTypeVO = dictTypeService.getDictTypeById(idDTO.getId());
		return ResponseFactory.ok(dictTypeVO);
	}
}