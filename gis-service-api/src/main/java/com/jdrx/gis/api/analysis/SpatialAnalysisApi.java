package com.jdrx.gis.api.analysis;

import com.jdrx.gis.beans.dto.query.DevIDsDTO;
import com.jdrx.gis.service.analysis.SpatialAnalysisService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

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

    @ApiOperation(value = "根据点选设备id获取连通性分析结果")
    @RequestMapping(value ="getConnectivityAnalysis")
    public ResposeVO getConnectivityAnalysis(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<String> dto) throws Exception{
        if (dto == null || dto.getId() ==null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/analysis/getConnectivityAnalysis 获取连通性分析结果");
        return  ResponseFactory.ok(spatialAnalysisService.getConnectivityAnalysis(dto.getId()));
    }

    @ApiOperation(value = "根据设备编码获取连通性分析结果")
    @RequestMapping(value ="getConnectivityAnalysisByCode")
    public ResposeVO getConnectivityAnalysisByCode(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<String> dto) throws Exception{
        if (dto == null || dto.getId() ==null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/analysis/getConnectivityAnalysisByCode 根据设备编码获取连通性分析结果");
        return  ResponseFactory.ok(spatialAnalysisService.getConnectivityByCode(dto.getId()));
    }

    @ApiOperation(value = "孤立点分析")
    @RequestMapping(value ="getLonelyPointsByDevIds")
    public ResposeVO getLonelyPointsByDevIds(@ApiParam(name = "iddto", required = true) @RequestBody @Valid DevIDsDTO devIDsDTO) throws Exception{
        Logger.debug("api/0/spatialAnalysis/getLonelyPointsByDevIds 孤立点查询结果");
        return  ResponseFactory.ok(spatialAnalysisService.getLonelyPointsByDevIds(devIDsDTO));
    }

    @ApiOperation(value = "孤立线分析")
    @RequestMapping(value ="getLonelyLinesByDevIds")
    public ResposeVO getLonelyLinesByDevIds(@ApiParam(name = "iddto", required = true) @RequestBody @Valid DevIDsDTO devIDsDTO) throws Exception{
        Logger.debug("api/0/spatialAnalysis/getLonelyLinesByDevIds 孤立线查询结果");
        return  ResponseFactory.ok(spatialAnalysisService.getLonelyLinesByDevIds(devIDsDTO));
    }


    @ApiOperation(value = "重复点分析")
    @RequestMapping(value ="getRepeatPointsByDevIds")
    public ResposeVO getRepeatPointsByDevIds(@ApiParam(name = "devIDsDTO", required = true) @RequestBody @Valid DevIDsDTO devIDsDTO) throws Exception{
        Logger.debug("api/0/spatialAnalysis/getRepeatPointsByDevIds 重复点查询结果");
        return  ResponseFactory.ok(spatialAnalysisService.getRepeatPointsByDevIds(devIDsDTO));
    }

    @ApiOperation(value = "重复线分析")
    @RequestMapping(value ="getRepeatLinesByDevIds")
    public ResposeVO getRepeatLinesByDevIds(@ApiParam(name = "devIDsDTO", required = true) @RequestBody @Valid DevIDsDTO devIDsDTO) throws Exception{
        Logger.debug("api/0/spatialAnalysis/getRepeatPointsByDevIds 重复点查询结果");
        return  ResponseFactory.ok(spatialAnalysisService.getRepeatLinesByDevIds(devIDsDTO));
    }

    @ApiOperation(value = "根据设备id数组删除重复点")
    @RequestMapping(value ="deleteRepeatPointByDevIds")
    public ResposeVO deleteRepeatPointByDevIds(@ApiParam(name = "dto", required = true) @RequestBody @Valid  DevIDsDTO dto,
                                           @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
                                           @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws Exception{
        Logger.debug("api/0/spatialAnalysis/deleteRepeatPointByDevId 根据devid数组删除重复点");
        try {
            List list = new LinkedList<>();
            Boolean aBoolean = spatialAnalysisService.deleteRepeatPointByDevIds(dto.getDevIds(),userId,token);
            return  ResponseFactory.ok(aBoolean);
        } catch (BizException e) {
            e.printStackTrace();
            return ResponseFactory.err(e.getMessage(), EApiStatus.ERR_SYS.getStatus());
        }
    }

    @ApiOperation(value = "根据设备id数组删除重复线")
    @RequestMapping(value ="deleteRepeatLineByDevIds")
    public ResposeVO deleteRepeatLineByDevIds(@ApiParam(name = "dto", required = true) @RequestBody @Valid  DevIDsDTO dto,
                                               @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
                                               @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws Exception{
        Logger.debug("api/0/spatialAnalysis/deleteRepeatLineByDevIds 根据devid数组删除重复线");
        try {
            List list = new LinkedList<>();
            Boolean aBoolean = spatialAnalysisService.deleteRepeatLineByDevIds(dto.getDevIds(),userId,token);
            return  ResponseFactory.ok(aBoolean);
        } catch (BizException e) {
            e.printStackTrace();
            return ResponseFactory.err(e.getMessage(), EApiStatus.ERR_SYS.getStatus());
        }
    }

}
