package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.anno.NoAuthData;
import com.jdrx.gis.beans.entity.basic.ShareDevPO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ShareDevPOMapper {

	int deleteByPrimaryKey(@Param("id") String id,@Param("loginUserName") String loginUserName,@Param("date") Date date);

	int insert(ShareDevPO record);

	int insertSelective(ShareDevPO record);

	@NoAuthData
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

	/**
	 * 查询设备是否为可关闭的阀门
	 * @param devIds    设备列表
	 * @param typeIds   可关闭的阀门类型集合
	 * @return
	 */
	List<Map<String, Integer>> findNodeType(@Param("devIds") List<String> devIds, @Param("typeIds") List<Long> typeIds);

	/**
	 * 根据ID集获取数据
	 * @param devIds
	 * @return
	 */
	@NoAuthData
	List<ShareDevPO> findByDevIds(@Param("devIds") List<String> devIds);
}