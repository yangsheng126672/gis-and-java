package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.entity.dataManage.GISCorrectionDetailPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: liaosijun
 * @Time: 2019/12/5 9:35
 */
public interface GISCorrectionDetailManualMapper{

	/**
	 * 批量插入待审核字段
	 * @param correctionDetails
	 * @return
	 */
	int batchInsert(@Param("correctionDetails") List<GISCorrectionDetailPO> correctionDetails);

	/**
	 * 根据Id和状态获取记录
	 * @param coRecordId
	 * @return
	 */
	List<GISCorrectionDetailPO> findAuditFieldsByRecordId(@Param("coRecordId") Long coRecordId, @Param("status") Integer status);

	/**
	 * 批量更新
	 * @param correctionDetails
	 * @return
	 */
	int batchUpdate(@Param("correctionDetails") List<GISCorrectionDetailPO> correctionDetails);

	/**
	 * 根据Id和通过与否获取记录
	 * @param coRecordId
	 * @return
	 */
	List<GISCorrectionDetailPO> findPassedFieldsByRecordId(@Param("coRecordId") Long coRecordId, @Param("hasPass") Integer hasPass);

}
