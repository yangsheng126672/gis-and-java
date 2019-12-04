package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.entity.basic.ShareDevPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShareDevPOMapper {

	int deleteByPrimaryKey(String id);

	int insert(ShareDevPO record);

	int insertSelective(ShareDevPO record);

	ShareDevPO selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(ShareDevPO record);

	int updateByPrimaryKey(ShareDevPO record);

	/**
	 * 根据主键获取type_id
	 * @param id
	 * @return
	 */
	Long getTypeIdPrimaryKey(Long id);

	/**
	 * 根据多个设备类型，查询它们下面的设备信息
	 * @param typeIds 叶子节点的类型
	 * @return
	 */
	List<ShareDevPO> findDevListByTypeIds(@Param("typeIds") String typeIds);

	/**
	 * 批量插入设备信息
	 * @param shareDevPOList
	 * @return
	 */
	int batchInsertSelective(@Param("shareDevPOList") List<ShareDevPO> shareDevPOList);

	/**
	 * 批量更新设备
	 * @param shareDevPOList
	 * @return
	 */
	int batchUpdate(@Param("shareDevPOList") List<ShareDevPO> shareDevPOList);

}