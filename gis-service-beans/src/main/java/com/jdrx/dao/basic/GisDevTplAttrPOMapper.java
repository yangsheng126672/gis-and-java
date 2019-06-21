package com.jdrx.dao.basic;

import com.jdrx.beans.entry.basic.GisDevTplAttrPO;

import java.util.List;

public interface GisDevTplAttrPOMapper {

    int insert(GisDevTplAttrPO record);

    int insertSelective(GisDevTplAttrPO record);

    GisDevTplAttrPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GisDevTplAttrPO record);

    int updateByPrimaryKey(GisDevTplAttrPO record);

	List<GisDevTplAttrPO> findTplAttrsByDevId(Long devId);
}