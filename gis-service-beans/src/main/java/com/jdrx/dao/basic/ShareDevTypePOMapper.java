package com.jdrx.dao.basic;

import com.jdrx.beans.entry.basic.ShareDevTypePO;

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
	List<ShareDevTypePO> selectAllDevByTypeId(Long id);

	/**
	 * 根据当前类型ID，递归查询所有子类
	 * @param id
	 * @return
	 */
	List<ShareDevTypePO> selectAllDevByCurrTypeId(Long id);

	/**
	 * 主键查询
	 * @param id
	 * @return
	 */
	ShareDevTypePO getByPrimaryKey(Long id);
}