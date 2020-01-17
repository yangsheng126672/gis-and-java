package com.jdrx.gis.dao.query;

import com.jdrx.gis.beans.anno.NoAuthData;
import com.jdrx.gis.beans.entity.query.TypeToDevNumsPO;

import java.util.List;

/**
 * @Author: liaosijun
 * @Time: 2020/1/16 17:14
 */
public interface SelfExamination {

	/**
	 * 获取有层级的类型对应的设备数量
	 * @return
	 */
	@NoAuthData
	List<TypeToDevNumsPO> findTypeTodevNums();

	/**
	 * 获取每个类型对应设备数量
	 * @return
	 */
	@NoAuthData
	List<TypeToDevNumsPO> findDevNums();
}
