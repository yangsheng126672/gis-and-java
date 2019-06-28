package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;

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
	 * 根据设备类型ID递归查询所有配置了模板的子类
	 * 不区分层级
	 * @param id
	 * @return
	 */
	List<ShareDevTypePO> findHasTplDevTypeListById(Long id);
}