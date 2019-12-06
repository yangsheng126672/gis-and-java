package com.jdrx.gis.service.dataManage;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jdrx.gis.beans.constants.basic.EAuditStatus;
import com.jdrx.gis.beans.dto.dataManage.AuditCorrectionDTO;
import com.jdrx.gis.beans.dto.dataManage.CorrectionDTO;
import com.jdrx.gis.beans.dto.dataManage.QueryAuditDTO;
import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.beans.entity.dataManage.GISCorrectionDetailPO;
import com.jdrx.gis.beans.entity.dataManage.GISCorrectionPO;
import com.jdrx.gis.beans.entity.user.SysOcpUserPo;
import com.jdrx.gis.beans.vo.datamanage.HistoryRecordVO;
import com.jdrx.gis.beans.vo.query.FieldNameVO;
import com.jdrx.gis.dao.basic.GISCorrectionPOManualMapper;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.autoGenerate.GISCorrectionPOMapper;
import com.jdrx.gis.dubboRpc.UserRpc;
import com.jdrx.gis.service.query.AttrQueryService;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.postgresql.util.PGobject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: liaosijun
 * @Time: 2019/12/4 16:34
 */
@Service
public class CorrectionService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(CorrectionService.class);

	@Autowired
	private GISCorrectionPOMapper gisCorrectionPOMapper;

	@Autowired
	private CorrectionDetailService correctionDetailService;

	@Autowired
	private UserRpc userRpc;

	@Autowired
	private AttrQueryService attrQueryService;

	@Autowired
	private GISDevExtPOMapper gisDevExtPOMapper;

	@Autowired
	private GISCorrectionPOManualMapper gisCorrectionPOManualMapper;

	/**
	 * 报错纠错信息
	 * @param dto
	 * @param userId
	 * @param token
	 * @return
	 * @throws BizException
	 * @throws SQLException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Boolean correctAttributeValue(CorrectionDTO dto, Long userId, String token) throws BizException, SQLException {
		try {
			GISCorrectionPO gisCorrectionPO = new GISCorrectionPO();
			gisCorrectionPO.setCode(dto.getCode());
			String devId = dto.getDevId();
			gisCorrectionPO.setDevId(devId);
			gisCorrectionPO.setStatus(Short.parseShort(String.valueOf(EAuditStatus.NO_AUDIT.getVal())));
			SysOcpUserPo sysOcpUserPo = userRpc.getUserById(userId, token);
			String loginUserName = sysOcpUserPo.getName();
			gisCorrectionPO.setCreateBy(loginUserName);
			Date now = new Date();
			gisCorrectionPO.setCreateAt(now);
			int e1 = gisCorrectionPOManualMapper.insertReturnId(gisCorrectionPO);
			Long coRecordId = gisCorrectionPO.getId();
			int e2 = 0;
			Map<String, Object> mapAttr = dto.getMapAttr();
			if (Objects.nonNull(mapAttr) && mapAttr.size() > 0) {
				List<FieldNameVO> fieldNames = attrQueryService.getFieldNames(dto.getDevId());
				if (Objects.isNull(fieldNames) | fieldNames.size() == 0) {
					throw new BizException("设备ID[" + devId + "]对应的模板未找到！");
				}

				List<GISCorrectionDetailPO> detailPOS = Lists.newArrayList();
				for (Map.Entry<String, Object> entry : mapAttr.entrySet()) {
					GISCorrectionDetailPO gisCorrectionDetailPO = new GISCorrectionDetailPO();
					String key = entry.getKey();
					Object val = entry.getValue();
					for (int i = 0; i < fieldNames.size(); i++) {
						FieldNameVO fieldNameVO = fieldNames.get(i);
						if (fieldNameVO.getFieldName().equals(key)) {
							gisCorrectionDetailPO.setCoRecordId(coRecordId);
							gisCorrectionDetailPO.setFieldDesc(fieldNameVO.getFieldDesc());
							gisCorrectionDetailPO.setFieldName(key);
							gisCorrectionDetailPO.setUpdval(String.valueOf(val));
							gisCorrectionDetailPO.setCreateAt(now);
							gisCorrectionDetailPO.setCreateBy(loginUserName);
							detailPOS.add(gisCorrectionDetailPO);
							break;
						}
					}
				}
				e2 = correctionDetailService.batchInsert(detailPOS);
			}
			if (e1 > 0 && e2 > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			throw new BizException(e);
		}
		return false;
	}

	/**
	 * 查询待审核的纠错列表
	 * @return
	 */
	public List<GISCorrectionPO> findNeedAuditAttrList(QueryAuditDTO dto) throws BizException{
		try {
			if (Objects.nonNull(dto)) {
				if (Objects.nonNull(dto.getStartDate()) && Objects.isNull(dto.getEndDate())) {
					throw new BizException("截止日期为空！");
				} else if (Objects.nonNull(dto.getEndDate()) && Objects.isNull(dto.getStartDate())) {
					throw new BizException("开始日期为空！");
				}
			}
			return gisCorrectionPOManualMapper.selectRecords(dto, EAuditStatus.NO_AUDIT.getVal());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException(e);
		}
	}

	/**
	 * 根据ID 查询待审核的字段列表
	 * @param coRecordId
	 * @return
	 * @throws BizException
	 */
	public List<GISCorrectionDetailPO> findAuditFieldsByRecordId(Long coRecordId) throws BizException {
		try {
			List<GISCorrectionDetailPO> detailPOS = correctionDetailService.findAuditFieldsByRecordId(coRecordId);
			return detailPOS;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 更新该记录为已审核
	 * @param devId
	 * @return
	 * @throws BizException
	 */
	public int updateAuditedByDevId(String devId) throws BizException {
		try {
			return gisCorrectionPOManualMapper.updateAuditedByDevId(EAuditStatus.AUDITED.getVal(), devId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException(e);
		}
	}


	/**
	 * 审核
	 * @param list
	 * @return
	 * @throws BizException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Boolean auditCorrectionData(List<AuditCorrectionDTO> list, Long userId, String token) throws BizException {
		try {
			SysOcpUserPo sysOcpUserPo = userRpc.getUserById(userId, token);
			String loginUserName = sysOcpUserPo.getName();
			String devId = list.get(0).getDevId();
			List<GISCorrectionDetailPO> detailPOList = Lists.newArrayList();
			List<FieldNameVO> fieldNames = attrQueryService.getFieldNames(devId);
			GISDevExtPO gisDevExtPO = gisDevExtPOMapper.getDevExtByDevId(devId);
			Object dataInfo = gisDevExtPO.getDataInfo();
			JSONObject dataInfoObj = JSONObject.parseObject(JSONObject.toJSONString(dataInfo));
			String value = (String) dataInfoObj.get("value");
			JSONObject valueObj = JSONObject.parseObject(value);

			for (AuditCorrectionDTO dto : list) {
				GISCorrectionDetailPO detailPO = new GISCorrectionDetailPO();
				detailPO.setId(dto.getDetailId());
				detailPO.setHasPass(Short.parseShort(String.valueOf(dto.getHasPass())));
				detailPO.setUpdateBy(loginUserName);
				detailPOList.add(detailPO);
				for (int i = 0; i < fieldNames.size(); i++) {
					FieldNameVO fieldNameVO = fieldNames.get(i);
					String key = fieldNameVO.getFieldName();
					if (key.equals(dto.getFieldName())) {
						valueObj.put(key,dto.getUpdVal());
						break;
					}
				}
			}

			PGobject dataInfoPG = new PGobject();
			dataInfoPG.setValue(JSONObject.toJSONString(valueObj));
			dataInfoPG.setType("jsonb");
			int e1 = correctionDetailService.batchUpdate(detailPOList);
			int e2 = updateAuditedByDevId(devId);
			int e3 = gisDevExtPOMapper.updateDataInfoByDevId(dataInfoPG, devId);
			if (e1 > 0 && e2 > 0 && e3 > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException(e);
		}
		return false;
	}

	/**
	 * 获取所有记录
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public List<HistoryRecordVO> findAllAuditList(QueryAuditDTO dto) throws BizException {
		try {
			List<HistoryRecordVO> historyRecordVOS = Lists.newArrayList();
			List<GISCorrectionPO> list = gisCorrectionPOManualMapper.selectRecords(dto, null);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (Objects.nonNull(list) && list.size() > 0) {
				list.stream().forEach(gisCorrectionPO -> {
					HistoryRecordVO vo = new HistoryRecordVO();
					Date createAt = gisCorrectionPO.getCreateAt();
					String createAtStr = "";
					if (Objects.nonNull(createAt)) {
						createAtStr = sdf.format(createAt);
					}
					BeanUtils.copyProperties(gisCorrectionPO, vo);
					vo.setCreateAt(createAtStr);
					Integer status = Integer.parseInt(String.valueOf(gisCorrectionPO.getStatus()));
					for (EAuditStatus eAuditStatus : EAuditStatus.values()) {
						if (eAuditStatus.getVal().equals(status)) {
							vo.setStatus(eAuditStatus.getDesc());
							break;
						}
					}
					HashMap<String, Object> passContent = Maps.newHashMap();
					Long id = gisCorrectionPO.getId();
					try {
						List<GISCorrectionDetailPO> details = correctionDetailService.findPassedFieldsByRecordId(id);
						if (Objects.nonNull(details)) {
							for (GISCorrectionDetailPO detailPO : details) {
								passContent.put(detailPO.getFieldDesc(), detailPO.getUpdval());
							}
							vo.setPassMap(passContent);
						}
					} catch (BizException e) {
						e.printStackTrace();
					}
					historyRecordVOS.add(vo);
				});
			}
			return historyRecordVOS;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException(e);
		}
	}
}
