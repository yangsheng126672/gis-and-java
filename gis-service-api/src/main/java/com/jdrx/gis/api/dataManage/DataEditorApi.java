package com.jdrx.gis.api.dataManage;

import com.jdrx.gis.api.analysis.SpatialAnalysisApi;
import com.jdrx.gis.service.analysis.SpatialAnalysisService;
import com.jdrx.gis.service.dataManage.DataEditorService;
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
 * @Time 2019/11/6 0006 下午 1:18
 */

@RestController
@Api("管网编辑服务")
@RequestMapping(value = "api/0/dataEditor", method = RequestMethod.POST)
public class DataEditorApi {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(SpatialAnalysisApi.class);

    @Autowired
    private DataEditorService dataEditorService;


    @ApiOperation(value = "获取管点类型")
    @RequestMapping(value ="getSharePointType")
    public ResposeVO getSharePointType() throws Exception{
        Logger.debug("api/0/analysis/getSharePointType 获取管点类型");
        return  ResponseFactory.ok(dataEditorService.getAllPointType());
    }
    @ApiOperation(value = "获取管线类型")
    @RequestMapping(value ="getShareLineType")
    public ResposeVO getShareLineType() throws Exception{
        Logger.debug("api/0/analysis/getShareLineType 获取管线类型");
        return  ResponseFactory.ok(dataEditorService.getAllLineType());
    }

    @ApiOperation(value = "根据类型id获取属性模板")
    @RequestMapping(value ="getDevExtByTopid")
    public ResposeVO getDevExtByTopid(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto) throws Exception{
        if (dto == null || dto.getId() ==null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/analysis/getDevExtByTopid 获取管点属性模板");
        return  ResponseFactory.ok(dataEditorService.getDevExtByTopPid(dto.getId()));

    }
}
