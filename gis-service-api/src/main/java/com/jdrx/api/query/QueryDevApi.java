package com.jdrx.api.query;

import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import com.jdrx.service.query.QueryDevService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: gis页面查询服务
 * @Author: liaosijun
 * @Time: 2019/6/12 11:01
 */
@RestController
@Api("查询服务")
@RequestMapping(value = "api/0/query", method = RequestMethod.POST)
public class QueryDevApi {

	private static final Logger Logger = LoggerFactory.getLogger(QueryDevApi.class);

	@Autowired
	private QueryDevService queryDevService;

	@ApiOperation(value = "获取所有设备信息")
	@RequestMapping(value = "getAllDev")
	public ResposeVO getAllDev() {
		return ResponseFactory.ok(queryDevService.queryAllDevNum());
	}

}