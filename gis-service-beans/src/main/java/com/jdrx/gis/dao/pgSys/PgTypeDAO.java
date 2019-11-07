package com.jdrx.gis.dao.pgSys;

/**
 * @Author: liaosijun
 * @Time: 2019/11/5 16:32
 */
public interface PgTypeDAO {
	/**
	 * 根据数据类型查分类
	 * @param typName
	 * @return
	 */
	String selectCategoryByTypname(String typName);
}
