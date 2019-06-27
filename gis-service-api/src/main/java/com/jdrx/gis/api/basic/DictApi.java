package com.jdrx.gis.api.basic;

import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.service.basic.DictService;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 数据字典Api
 * @Author: liaosijun
 * @Time: 2019/6/27 13:18
 */
@RestController
@Api("数据字典接口类")
@RequestMapping(value = "api/0/dict", method = RequestMethod.POST)
public class DictApi {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DictApi.class);

	@Autowired
	private DictService dictService;

	@ApiOperation(value = "查询管径的配置信息")
	@RequestMapping(value = "findCaliberDicts")
	public ResposeVO findCaliberDicts() throws BizException {
		Logger.debug("api/0/dict/findCaliberDicts 查询管径的配置信息");
		List<DictDetailPO> dictDetailPOs = dictService.findDetailsByTypeVal(GISConstants.CALIBER_TYPE);
		return ResponseFactory.ok(dictDetailPOs);
	}
}