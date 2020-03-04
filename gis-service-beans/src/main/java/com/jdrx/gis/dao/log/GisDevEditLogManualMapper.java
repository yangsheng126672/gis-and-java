package com.jdrx.gis.dao.log;

import com.jdrx.gis.beans.entity.log.GisDevEditLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: liaosijun
 * @Time: 2020/2/18 16:16
 */
public interface GisDevEditLogManualMapper {

	/**
	 * 批量增加
	 * @param gisDevExtLogList
	 * @return
	 */
	int batchInsertSelective(@Param("gisDevExtLogList") List<GisDevEditLog> gisDevExtLogList);

	/**
	 * 插入单条
	 * @return
	 */
	int insertSelective(GisDevEditLog gisDevEditLog);
}
