package com.jdrx.gis.api.dataManage;

import com.jdrx.gis.api.analysis.SpatialAnalysisApi;
import com.jdrx.gis.beans.dto.dataManage.*;
import com.jdrx.gis.service.dataManage.DataEditorService;
import com.jdrx.platform.common.support.gateway.GwConstants;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.neo4j.cypher.internal.v3_4.functions.E;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

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
        Logger.debug("api/0/dataEditor/getSharePointType 获取管点类型");
        return  ResponseFactory.ok(dataEditorService.getAllPointType());
    }

    @ApiOperation(value = "保存新增管点")
    @RequestMapping(value ="saveSharePoint")
    public ResposeVO saveSharePoint(@ApiParam(name = "dto", required = true) @RequestBody @Valid ShareAddedPointDTO dto,
                                    @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
                                    @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token,
                                     @RequestHeader(name ="deptPath") String deptPath) throws Exception{
        Logger.debug("api/0/dataEditor/saveSharePoint 保存新增管点");
        return  ResponseFactory.ok(dataEditorService.saveAddedSharePoint(dto,userId,token,deptPath));
    }

    @ApiOperation(value = "保存新增管网")
    @RequestMapping(value ="saveShareNets")
    public ResposeVO saveShareNets(@ApiParam(name = "dto", required = true) @RequestBody @Valid ShareAddedNetsDTO dto,
                                   @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
                                   @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token,
                                   @RequestHeader(name ="deptPath") String deptPath) throws Exception{
        Logger.debug("api/0/dataEditor/saveShareNets 保存新增管网");
        return  ResponseFactory.ok(dataEditorService.saveShareNets(dto,userId,token,deptPath));
    }

    @ApiOperation(value = "获取管线类型")
    @RequestMapping(value ="getShareLineType")
    public ResposeVO getShareLineType() throws Exception{
        Logger.debug("api/0/dataEditor/getShareLineType 获取管线类型");
        return  ResponseFactory.ok(dataEditorService.getAllLineType());
    }

    @ApiOperation(value = "根据类型id获取属性模板")
    @RequestMapping(value ="getDevExtByTopid")
    public ResposeVO getDevExtByTopid(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto) throws Exception{
        if (dto == null || dto.getId() ==null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/dataEditor/getDevExtByTopid 获取管点属性模板");
        return  ResponseFactory.ok(dataEditorService.getDevExtByTopPid(dto.getId()));
    }

    @ApiOperation(value = "判断管点编号是否存在，是否带有-（true：存在 false：不存在）")
    @RequestMapping(value ="getCodeExist")
    public ResposeVO getCodeExist(@ApiParam(name = "code", required = true) @RequestBody @Valid  IdDTO<String> dto) throws Exception{
        Logger.debug("api/0/dataEditor/getCodeExist 判断编码问题是否重复，是否带有-");
        try{
            Boolean flag = dataEditorService.getCodeExist(dto.getId());
            return  ResponseFactory.ok(flag);
        }catch (Exception e){
            return  ResponseFactory.err(e.getMessage(),EApiStatus.ERR_SYS.getStatus());
        }
    }

    @ApiOperation(value = "根据设备code精准查询设备")
    @RequestMapping(value ="getGISDevExtByCode")
    public ResposeVO getGISDevExtByCode(@ApiParam(name = "code", required = true) @RequestBody @Valid  IdDTO<String> dto) throws Exception{
        Logger.debug("api/0/dataEditor/getGISDevExtByCode 根据设备code精准查询设备");
        return  ResponseFactory.ok(dataEditorService.getGISDevExtByCode(dto.getId()));
    }

    @ApiOperation(value = "更新设备属性信息")
    @RequestMapping(value ="updateShareDataInfo")
    public ResposeVO updateShareDataInfo(@ApiParam(name = "dto", required = true) @RequestBody @Valid MapAttrDTO dto,
                                         @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
                                         @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws Exception{
        Logger.debug("api/0/dataEditor/updateShareDataInfo 更新设备属性信息");
        return  ResponseFactory.ok(dataEditorService.updateGISDevExtAttr(dto.getMapAttr(),userId,token));
    }

    @ApiOperation(value = "移动管点及关联管线")
    @RequestMapping(value ="moveSharePointWithLine")
    public ResposeVO moveSharePointWithLine(@ApiParam(name = "dto", required = true) @RequestBody @Valid MovePointDTO dto,
                                            @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
                                            @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws Exception{
        Logger.debug("api/0/dataEditor/moveSharePointWithLine 移动管点及关联管线");
        return  ResponseFactory.ok(dataEditorService.moveShareDevPoint(dto,userId,token));
    }

    @ApiOperation(value = "连接两点功能")
    @RequestMapping(value ="connectPointsByDevIds")
    public ResposeVO connectPointsByDevIds(@ApiParam(name = "dto", required = true) @RequestBody @Valid ConnectPointsDTO dto,
                                           @RequestHeader(name ="deptPath") String deptPath,
                                           @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
                                           @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws Exception{
        Logger.debug("api/0/dataEditor/connectPointsByDevIds 连接两点功能");
        try{
             Boolean b = dataEditorService.connectPoints(dto,userId,token,deptPath);
             return ResponseFactory.ok(b);
        }catch(BizException e){
            return ResponseFactory.err(e.getMessage(),EApiStatus.ERR_SYS.getStatus());
        }

    }

    @ApiOperation(value = "根据设备id删除设备")
    @RequestMapping(value ="deleteShareDevByDevId")
    public ResposeVO deleteShareDevByDevId(@ApiParam(name = "dto", required = true) @RequestBody @Valid  IdDTO<String> dto,
                                           @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
                                           @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws Exception{
        Logger.debug("api/0/dataEditor/deleteShareDevByDevId 根据设备id删除设备");
        try {
            Boolean aBoolean = dataEditorService.deleteShareDevByDevId(dto.getId(),userId,token);
            return  ResponseFactory.ok(aBoolean);
        } catch (BizException e) {
            e.printStackTrace();
            return ResponseFactory.err("当前管点连接着管线，不允许删除该管点！", EApiStatus.ERR_SYS.getStatus());
        }
    }

    @ApiOperation(value = "经纬度转为地方坐标系")
    @RequestMapping(value ="transformWgs84ToCustom")
    public ResposeVO transformWgs84ToCustom(@ApiParam(name = "dto", required = true) @RequestBody @Valid PointDTO dto) throws Exception{
        Logger.debug("api/0/dataEditor/transformWgs84ToCustom 经纬度转为地方坐标系");
        return  ResponseFactory.ok(dataEditorService.transformWgs84ToXY(dto));
    }

}
