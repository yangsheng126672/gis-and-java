package com.jdrx.gis.dao.log;

import com.jdrx.gis.beans.entity.log.GisDevVer;

/**
 * @Author: liaosijun
 * @Time: 2020/2/18 15:32
 */
public interface GisDevVerManualMapper {

	/**
	 * 插入版本信息，并返回ID值
	 * @param gisDevVer
	 * @return
	 */
	Long insertReturnId(GisDevVer gisDevVer);
}
