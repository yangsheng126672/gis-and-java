package com.jdrx.gis.dao.log.autoGenerate;

import com.jdrx.gis.beans.entity.log.GisDevEditLog;

public interface GisDevEditLogMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GisDevEditLog record);

    int insertSelective(GisDevEditLog record);

    GisDevEditLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GisDevEditLog record);

    int updateByPrimaryKey(GisDevEditLog record);
}