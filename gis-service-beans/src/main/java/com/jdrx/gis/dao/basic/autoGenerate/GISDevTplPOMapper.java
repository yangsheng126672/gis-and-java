package com.jdrx.gis.dao.basic.autoGenerate;

import com.jdrx.gis.beans.entry.basic.GISDevTplPO;

public interface GISDevTplPOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GISDevTplPO record);

    int insertSelective(GISDevTplPO record);

    GISDevTplPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GISDevTplPO record);

    int updateByPrimaryKey(GISDevTplPO record);
}