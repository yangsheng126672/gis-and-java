package com.jdrx.gis.service.dataManage;

import com.jdrx.gis.beans.constants.basic.EAuditStatus;
import com.jdrx.gis.beans.constants.basic.EPassStatus;
import com.jdrx.gis.beans.entity.dataManage.GISCorrectionDetailPO;
import com.jdrx.gis.dao.basic.GISCorrectionDetailManualMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: liaosijun
 * @Time: 2019/12/4 17:38
 */
@Service
public class CorrectionDetailService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(CorrectionDetailService.class);

	@Autowired
	private GISCorrectionDetailManualMapper gisCorrectionDetailManualMapper;

	/**
	 * 批量插入纠错信息的属性值
	 * @param correctionDetailPOList
	 * @return
	 * @throws BizException
	 */
	public int batchInsert(List<GISCorrectionDetailPO> correctionDetailPOList) throws BizException {
		try {
			int ef = gisCorrectionDetailManualMapper.batchInsert(correctionDetailPOList);
			return ef;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			throw new BizException("批量插入纠错属性列表失败！");
		}
	}

	/**
	 * 根据Id查待审核字段
	 * @param coRecordId
	 * @return
	 * @throws BizException
	 */
	public List<GISCorrectionDetailPO> findAuditFieldsByRecordId(Long coRecordId) throws BizException {
		try {
			return gisCorrectionDetailManualMapper.findAuditFieldsByRecordId(coRecordId, EAuditStatus.NO_AUDIT.getVal());
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			throw new BizException("根据Id查待审核字段失败！");
		}
	}

	/**
	 * 批量更新
	 * @param correctionDetails
	 * @return
	 * @throws BizException
	 */
	public int batchUpdate(List<GISCorrectionDetailPO> correctionDetails) throws BizException {
		try {
			return gisCorrectionDetailManualMapper.batchUpdate(correctionDetails);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			throw new BizException(e);
		}
	}

	/**
	 * 获取已通过审核的记录
	 * @param coRecordId
	 * @return
	 * @throws BizException
	 */
	public List<GISCorrectionDetailPO> findPassedFieldsByRecordId(Long coRecordId) throws BizException {
		try {
			return gisCorrectionDetailManualMapper.findPassedFieldsByRecordId(coRecordId, EPassStatus.PASSED.getVal());
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			throw new BizException(e);
		}
	}
}
