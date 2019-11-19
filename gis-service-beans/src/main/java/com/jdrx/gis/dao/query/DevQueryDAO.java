package com.jdrx.gis.dao.query;

import com.jdrx.gis.beans.dto.query.DevIDsForTypeDTO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.beans.entry.query.SpaceInfTotalPO;
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
	Long getCountByTypeIds(@Param("typeIds") List<Long> typeIds, @Param("devIds" ) String devIds);

	/**
	 * 根据设备IDs和类型ID获取设备信息
	 * @return
	 */
	List<SpaceInfoVO> findDevListByTypeID(@Param("dto") DevIDsForTypeDTO dto, @Param("devIds") String devIds);

	/**
	 * 根据类型ID和设备IDs查询所属设备信息查询总条数
	 * @param dto
	 * @param devIds
	 * @return
	 */
	Integer findDevListByTypeIDCount(@Param("dto") DevIDsForTypeDTO dto, @Param("devIds") String devIds);

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
	Long findWaterPipeCaliberSum(@Param("pre")String pre, @Param("min")Integer min,
	                             @Param("max")Integer max, @Param("suf") String suf, @Param("devIds") String devIds);

	/**
	 * 根据设备ID集查每个类型设备个数
	 * @param devIds
	 * @return
	 */
	List<SpaceInfTotalPO> findSpaceInfoByDevIds(@Param("devIds") String devIds);

	/**
	 * 根据设备ID获取模板的字段列表
	 * @param devId
	 * @return
	 */
	List<FieldNameVO> findFieldNamesByDevID(String devId);

}