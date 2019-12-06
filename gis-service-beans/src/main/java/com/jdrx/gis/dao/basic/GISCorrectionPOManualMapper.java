package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.dto.dataManage.QueryAuditDTO;
import com.jdrx.gis.beans.entity.dataManage.GISCorrectionPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GISCorrectionPOManualMapper {

	/**
	 * 插入设备纠错的记录
	 * @param record
	 * @return
	 */
	int insertReturnId(GISCorrectionPO record);

	/**
	 * 通过条件和状态去查询记录
	 * @param dto
	 * @return
	 */
    List<GISCorrectionPO> selectRecords(@Param("dto") QueryAuditDTO dto, @Param("status") Integer status);

	/**
	 * 更新审核状态
	 * @param status
	 * @return
	 */
	int updateAuditedByDevId(@Param("status") Integer status, @Param("devId") String devId);

}