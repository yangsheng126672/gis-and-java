package com.jdrx.gis.dao.log.autoGenerate;

import com.jdrx.gis.beans.entity.log.GisDevVer;

public interface GisDevVerMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GisDevVer record);

    int insertSelective(GisDevVer record);

    GisDevVer selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GisDevVer record);

    int updateByPrimaryKey(GisDevVer record);
}