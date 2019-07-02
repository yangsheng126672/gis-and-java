package com.jdrx.gis.dao.query;

import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.beans.vo.query.FieldNameVO;
import com.jdrx.gis.beans.vo.query.SpaceInfoVO;
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
	List<ShareDevTypePO> findFirstHierarchyDevType();

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
	Long getCountByTypeIds(@Param("typeIds") List<Long> typeIds);

	/**
	 * 根据类型ID查询所属设备信息
	 * @param typeId
	 * @return
	 */
	List<SpaceInfoVO> findDevListByTypeID(Long typeId);

	/**
	 * 根据类型ID查表头
	 * @param id
	 * @return
	 */
	List<FieldNameVO> findFieldNamesByTypeID(Long id);


	/**
	 * 水管口径数量查询
	 * @return
	 */
	Long findWaterPipeCaliberSum(@Param("pre")String pre, @Param("min")Integer min, @Param("max")Integer max, @Param("suf") String suf);

}