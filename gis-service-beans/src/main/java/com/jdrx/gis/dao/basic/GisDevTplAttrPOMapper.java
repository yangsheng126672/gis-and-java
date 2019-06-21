package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.entry.basic.GisDevTplAttrPO;

import java.util.List;

public interface GisDevTplAttrPOMapper {

    int insert(GisDevTplAttrPO record);

    int insertSelective(GisDevTplAttrPO record);

    GisDevTplAttrPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GisDevTplAttrPO record);

    int updateByPrimaryKey(GisDevTplAttrPO record);

	/**
	 * 根据设备ID查模板配置信息
	 * @param devId
	 * @return
	 */
	List<GisDevTplAttrPO> findTplAttrsByDevId(Long devId);

	/**
	 * 根据设备类型ID查模板配置信息
	 * @param typeId
	 * @return
	 */
	List<GisDevTplAttrPO> findAttrListByTypeId(Long typeId);
}