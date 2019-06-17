package com.jdrx.dao.query;

import com.jdrx.beans.entry.basic.ShareDevTypePO;
import com.jdrx.beans.entry.query.SpaceInfTotalPO;
import com.jdrx.beans.vo.SpaceInfoVO;

import java.util.List;

/**
 * @Description: gis查询
 * @Author: liaosijun
 * @Time: 2019/6/12 11:32
 */
public interface DevQueryDAO {

	/**
	 * 查第一层，即图层大分类，目前定为6类
	 * @return
	 */
	List<ShareDevTypePO> findFirstierarchy();

	/**
	 * 根据PID查所有子类（不包含枝干）
	 * @param pid
	 * @return
	 */
	List<ShareDevTypePO> findDevTypeByPID(Long pid);

	/**
	 * 根据类型ID的集合查询所属设备个数
	 * @param typeIds
	 * @return
	 */
	Integer getCountByTypeIds(List<Long> typeIds);

	/**
	 * 根据类型ID查询所属设备信息
	 * @param typeId
	 * @return
	 */
	List<SpaceInfoVO> findDevByTypeId(Long typeId);

	List<SpaceInfTotalPO> queryAllDevNum();
}