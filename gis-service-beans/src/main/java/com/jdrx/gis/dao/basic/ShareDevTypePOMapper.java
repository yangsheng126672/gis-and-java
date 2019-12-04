package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.entity.basic.ShareDevTypePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShareDevTypePOMapper {

	List<ShareDevTypePO> findDevTypeList();

	/**
	 * 根据设备类型ID查它的子类，第二层即可，不继续递归
	 * @param id
	 * @return
	 */
	List<ShareDevTypePO> findDevTypeListByTypeId(Long id);

	/**
	 * 根据父类ID递归所有子类
	 * @param id
	 * @return
	 */
	List<ShareDevTypePO> findAllDevTypeListByTypePId(Long id);

	/**
	 * 根据当前类型ID，递归查询所有子类
	 * @param id
	 * @return
	 */
	List<ShareDevTypePO> findAllDevTypeListByCurrTypeId(Long id);

	/**
	 * 主键查询
	 * @param id
	 * @return
	 */
	ShareDevTypePO getByPrimaryKey(Long id);

	/**
	 * 从typeId往上递归，查询到离typeId最近，在gis_tpl_type配置了模板的记录
	 * @param id
	 * @return
	 */
	List<ShareDevTypePO> findHasTplDevTypeListById(Long id);

	/**
	 * 根据设备类型ID获取设备类型列表信息
	 * 传入的设备类型大都是枝干类型，查询枝干类型下面的叶子类型
	 * @param typeIds
	 * @return
	 */
	List<ShareDevTypePO> findLeafTypesByLimbTypeIds(@Param("typeIds") List<Long> typeIds);

	/**
	 * 根据传来的设备ID查询这些dev_id的父类type_id
	 * @param devIds
	 * @return
	 */
	List<Long> findTypeIdsByDevIds(@Param("devIds") String devIds);

	/**
	 * 根据ids 查列表
	 * @param ids
	 * @return
	 */
	List<ShareDevTypePO> findDevTypeListByIds(@Param("ids") List<Long> ids);

	/**
	 * 获取所有管点类型
	 * @param ids
	 * @return
	 */
	List<ShareDevTypePO> findPointTypeByIds(@Param("ids") String ids);

	/**
	 * 获取所有管线类型
	 * @param ids
	 * @return
	 */
	List<ShareDevTypePO> findLineTypeByIds(@Param("ids") String ids);
	/**
	 * 判断类型名称是否存在
	 * @param name      类型名称
	 * @return
	 */
	ShareDevTypePO selectByTypeName(@Param("name") String name);

	/**
	 * 获取所有叶子类型
	 * @return
	 */
	List<ShareDevTypePO> findAllDevLeafType();

	/**
	 * 通过顶层名称获取对应ID
	 * @param typeName
	 * @return
	 */
	long getIdByNameForTopHierarchy(@Param("name") String typeName);

	/**
	 * 根据顶层id获取所有子类下面的id
	 * @param topId
	 * @return
	 */
	List getAllTypeIdByTopId(@Param("topId") Long topId);

}