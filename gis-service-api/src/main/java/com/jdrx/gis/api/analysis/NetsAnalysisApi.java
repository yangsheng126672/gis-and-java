package com.jdrx.gis.api.analysis;

import com.jdrx.gis.api.basic.BasciDevApi;
import com.jdrx.gis.beans.dto.analysis.AnalysisDTO;
import com.jdrx.gis.beans.dto.analysis.AnalysisRecordDTO;
import com.jdrx.gis.beans.dto.analysis.RecondParamasDTO;
import com.jdrx.gis.beans.dto.analysis.SecondAnalysisDTO;
import com.jdrx.gis.beans.dto.query.DevIDsDTO;
import com.jdrx.gis.beans.dto.query.DevIDsForTypeDTO;
import com.jdrx.gis.beans.entry.analysis.ExportValveDTO;
import com.jdrx.gis.service.analysis.NetsAnalysisService;
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
    public ResposeVO getAnalysisiResult(@ApiParam(name = "iddto", required = true) @RequestBody @Valid AnalysisDTO dto) throws Exception{
        if (dto == null || dto.getId() ==null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/analysis/getAnalysisiResult 获取爆管分析结果");
        return  ResponseFactory.ok(netsAnalysisService.getAnalysisResult(dto));

    }

    @ApiOperation(value = "获取二次关阀分析结果")
    @RequestMapping(value ="getSecondAnalysisiResult" )
    public ResposeVO getSecondAnalysisiResult(@ApiParam(name = "dto", required = true) @RequestBody @Valid SecondAnalysisDTO secondAnalysisDTO) throws Exception{
        if (secondAnalysisDTO == null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/analysis/getSecondAnalysisiResult 获取二次关阀分析结果");
        return  ResponseFactory.ok(netsAnalysisService.getSecondAnalysisResult(secondAnalysisDTO));
    }

    @ApiOperation(value = "保存关阀分析结果")
    @RequestMapping(value ="saveSecondAnalysisiResult" )
    public ResposeVO saveSecondAnalysisiResult(@ApiParam(name = "dto", required = true) @RequestBody @Valid AnalysisRecordDTO recordDTO) throws Exception{
        if (recordDTO == null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/analysis/saveSecondAnalysisiResult 保存关阀分析结果");
        return  ResponseFactory.ok(netsAnalysisService.saveAnalysisRecond(recordDTO));
    }

    @ApiOperation(value = "获取爆管历史记录列表")
    @RequestMapping(value ="getAnalysisiReconds" )
    public ResposeVO getAnalysisiReconds(@ApiParam(name = "dto", required = true) @RequestBody @Valid RecondParamasDTO recondParamasDTO) throws Exception{
        Logger.debug("api/0/analysis/getAnalysisiReconds 获取爆管历史记录列表");
        return  ResponseFactory.ok(netsAnalysisService.getAnalysisRecondList(recondParamasDTO));
    }

    @ApiOperation(value = "获取某条爆管记录详细关阀信息")
    @RequestMapping(value ="getAnalysisiValveByID" )
    public ResposeVO getAnalysisiValveByID(@ApiParam(name = "dto", required = true) @RequestBody @Valid IdDTO<Long> idDTO) throws Exception{
        Logger.debug("api/0/analysis/getAnalysisiValveByID 获取某条爆管记录详细关阀信息");
        return  ResponseFactory.ok(netsAnalysisService.getValveById(idDTO));
    }

    @ApiOperation(value = "导出关阀分析结果")
    @RequestMapping(value ="exportAnalysisiResult" )
    public ResposeVO exportAnalysisiResult(@ApiParam(name = "dto", required = true) @RequestBody @Valid ExportValveDTO dto) throws Exception{
        if (dto == null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/analysis/exportAnalysisiResult 导出关阀分析结果");
        return  ResponseFactory.ok(netsAnalysisService.exportAnalysisResult(dto));
    }
}
