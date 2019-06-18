package com.jdrx.dao.query;

import com.jdrx.beans.entry.basic.ShareDevTypePO;
import com.jdrx.beans.entry.query.SpaceInfTotalPO;
import com.jdrx.beans.vo.query.FieldNameVO;
import com.jdrx.beans.vo.query.SonsNumVO;
import com.jdrx.beans.vo.query.SpaceInfoVO;
import org.apache.ibatis.annotations.Param;

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

	/**
	 * 根据类型ID查表头
	 * @param id
	 * @return
	 */
	List<FieldNameVO> findFieldNameByTypeID(Long id);


	/**
	 * 水管口径数量查询
	 * @return
	 */
	Long findWaterPipeCaliberSum(@Param("min")Integer min, @Param("max")Integer max);

	/**
	 * 根据设备类型ID 查子类设备的个数，只递归到第二层即可
	 * @param id
	 * @return
	 */
	List<SonsNumVO> findSonsNumByPid(Long id);

	List<SpaceInfTotalPO> queryAllDevNum();
}