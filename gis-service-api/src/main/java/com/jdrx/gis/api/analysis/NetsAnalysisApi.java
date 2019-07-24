package com.jdrx.gis.api.analysis;

import com.jdrx.gis.api.basic.BasciDevApi;
import com.jdrx.gis.service.analysis.NetsAnalysisService;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description 爆管分析
 * @Author lr
 * @Time 2019/7/19 0019 下午 2:13
 */

@RestController
@Api("爆管分析查询")
@RequestMapping(value = "api/0/analysis", method = RequestMethod.POST)
public class NetsAnalysisApi {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(BasciDevApi.class);

    @Autowired
    private NetsAnalysisService netsAnalysisService;

    @ApiOperation(value = "获取爆管分析结果")
    @RequestMapping(value ="getAnalysisiResult" )
    public ResposeVO getAnalysisiResult(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto) throws Exception{
        Logger.debug("api/0/analysis/getAnalysisiResult 获取爆管分析结果");
        return  ResponseFactory.ok(netsAnalysisService.getAnalysisResult(dto.getId()));

    }
}
