package com.jdrx.gis.api.analysis;

import com.jdrx.gis.service.analysis.SpatialAnalysisService;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
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
 * @Description
 * @Author lr
 * @Time 2019/10/23 0023 上午 9:52
 */

@RestController
@Api("空间分析查询服务")
@RequestMapping(value = "api/0/spatialAnalysis", method = RequestMethod.POST)
public class SpatialAnalysisApi {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(SpatialAnalysisApi.class);

    @Autowired
    private SpatialAnalysisService spatialAnalysisService;

    @ApiOperation(value = "获取连通性分析结果")
    @RequestMapping(value ="getConnectivityAnalysis")
    public ResposeVO getConnectivityAnalysis(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto) throws Exception{
        if (dto == null || dto.getId() ==null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/analysis/getConnectivityAnalysis 获取连通性分析结果");
        return  ResponseFactory.ok(spatialAnalysisService.getConnectivityAnalysis(dto.getId()));

    }

    @ApiOperation(value = "获取管点类型")
    @RequestMapping(value ="getSharePointType")
    public ResposeVO getSharePointType() throws Exception{
        Logger.debug("api/0/analysis/getSharePointType 获取管点类型");
        return  ResponseFactory.ok(spatialAnalysisService.getAllPointType());
    }

    @ApiOperation(value = "根据设备id获取管点属性模板")
    @RequestMapping(value ="getDevExtByTopid")
    public ResposeVO getDevExtByTopid(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto) throws Exception{
        if (dto == null || dto.getId() ==null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/analysis/getDevExtByTopid 获取管点属性模板");
        return  ResponseFactory.ok(spatialAnalysisService.getDevExtByTopPid(dto.getId()));

    }



}
