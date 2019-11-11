package com.jdrx.gis.dao.log;

import com.jdrx.gis.beans.entry.log.GisTransLog;

public interface GisTransLogMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GisTransLog record);

    int insertSelective(GisTransLog record);

    GisTransLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GisTransLog record);

    int updateByPrimaryKey(GisTransLog record);
}