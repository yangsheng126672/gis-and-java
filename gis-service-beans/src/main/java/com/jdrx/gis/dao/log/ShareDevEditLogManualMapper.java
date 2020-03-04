package com.jdrx.gis.dao.log;

import com.jdrx.gis.beans.entity.log.ShareDevEditLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: liaosijun
 * @Time: 2020/2/18 16:16
 */
public interface ShareDevEditLogManualMapper {

	/**
	 * 批量增加
	 * @param shareDevEditLogList
	 * @return
	 */
	int batchInsertSelective(@Param("shareDevEditLogList") List<ShareDevEditLog> shareDevEditLogList);
}
