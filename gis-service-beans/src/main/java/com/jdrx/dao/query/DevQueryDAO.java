package com.jdrx.dao.query;

import com.jdrx.beans.entry.query.SpaceInfTotalPO;

import java.util.List;

/**
 * @Description: gis查询
 * @Author: liaosijun
 * @Time: 2019/6/12 11:32
 */
public interface DevQueryDAO {

	List<SpaceInfTotalPO> queryAllDevNum();
}