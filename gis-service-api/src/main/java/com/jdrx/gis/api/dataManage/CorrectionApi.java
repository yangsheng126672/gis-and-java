package com.jdrx.gis.api.dataManage;

import com.jdrx.gis.beans.dto.dataManage.AuditCorrectionDTO;
import com.jdrx.gis.beans.dto.dataManage.CorrectionDTO;
import com.jdrx.gis.beans.dto.dataManage.QueryAuditDTO;
import com.jdrx.gis.beans.entity.dataManage.GISCorrectionDetailPO;
import com.jdrx.gis.beans.entity.dataManage.GISCorrectionPO;
import com.jdrx.gis.beans.vo.datamanage.HistoryRecordVO;
import com.jdrx.gis.service.dataManage.CorrectionService;
import com.jdrx.platform.common.support.gateway.GwConstants;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
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
import java.util.List;
import java.util.Objects;

/**
 * @Author: liaosijun
 * @Time: 2019/12/4 16:04
 */
@RestController
@Api("设备数据纠错")
@RequestMapping(value = "api/0/correction", method = RequestMethod.POST)
public class CorrectionApi {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(CorrectionApi.class);

	@Autowired
	private CorrectionService correctionService;

	@ApiOperation(value = "提交纠正的错误值")
	@RequestMapping(value ="correctAttributeValue")
	public ResposeVO correctAttributeValue(@ApiParam(name = "dto", required = true) @RequestBody @Valid CorrectionDTO dto,
	                                       @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
	                                       @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws BizException {
		Logger.debug("api/0/correction/correctAttributeValue 提交纠正的错误值");
		boolean bool;
		try {
			bool = correctionService.correctAttributeValue(dto, userId, token);
			return ResponseFactory.ok(bool);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("提交纠正的错误值失败！");
		}
	}

	@ApiOperation(value = "获取待审核的列表")
	@RequestMapping(value ="findNeedAuditAttrList")
	public ResposeVO findNeedAuditAttrList(@ApiParam(name = "dto", required = true) @RequestBody QueryAuditDTO dto ) throws BizException {
		Logger.debug("api/0/correction/findNeedAuditAttrList 查询待审核的列表");
		try {
			List<GISCorrectionPO> needAuditAttrList = correctionService.findNeedAuditAttrList(dto);
			return ResponseFactory.ok(needAuditAttrList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("查询待审核的列表失败！");
		}
	}

	@ApiOperation(value = "根据ID获取待审核的字段列表")
	@RequestMapping(value ="findAuditFieldsByRecordId")
	public ResposeVO findAuditFieldsByRecordId(@ApiParam(name = "dto", required = true) @RequestBody @Valid IdDTO<Long> dto) throws BizException {
		Logger.debug("api/0/correction/findAuditFieldsByRecordId 根据ID获取待审核的字段列表");
		try {
			List<GISCorrectionDetailPO> correctionDetailPOS = correctionService.findAuditFieldsByRecordId(dto.getId());
			return ResponseFactory.ok(correctionDetailPOS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("根据ID获取待审核的字段列表失败！");
		}
	}

	@ApiOperation(value = "审核纠错值")
	@RequestMapping(value ="auditCorrectionData")
	public ResposeVO auditCorrectionData(@ApiParam(name = "dto", required = true) @RequestBody @Valid List<AuditCorrectionDTO> dtos,
										 @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
	                                     @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws BizException {
		Logger.debug("api/0/correction/auditCorrectionData 审核纠错值");
		if (Objects.isNull(dtos) | dtos.size() == 0) {
			throw new BizException("参数为空！");
		}
		try {
			Boolean bool = correctionService.auditCorrectionData(dtos, userId, token);
			return ResponseFactory.ok(bool);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("审核纠错值失败！");
		}
	}

	@ApiOperation(value = "获取所有审核记录")
	@RequestMapping(value = "findAllAuditList")
	public ResposeVO findAllAuditList(@ApiParam(name = "dto", required = true) @RequestBody QueryAuditDTO dto) throws BizException {
		Logger.debug("api/0/correction/findAllAuditList 获取所有审核记录");
		try {
			List<HistoryRecordVO> allAuditList = correctionService.findAllAuditList(dto);
			return ResponseFactory.ok(allAuditList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("获取所有审核记录失败！");
		}
	}
}
