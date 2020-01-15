package com.jdrx.gis.api.dataManage;

import com.jdrx.gis.service.dataManage.SelfExaminationReportService;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: liaosijun
 * @Time: 2020/1/15 16:27
 */
@RestController
@Api("数据校验")
@RequestMapping(value = "api/0/verify", method = RequestMethod.POST)
public class DataVerifyApi {

	@Autowired
	private SelfExaminationReportService selfExaminationReportService;

	@ApiOperation(value = "导出数据")
	@RequestMapping(value ="exportData")
	public ResposeVO getSharePointType() throws BizException{
		return ResponseFactory.ok(selfExaminationReportService.exportSelfExaminationReport());
	}
}
