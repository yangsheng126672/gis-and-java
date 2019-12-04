package com.jdrx.gis.dao.analysis;

import com.jdrx.gis.beans.entity.analysis.GisWaterUserInfoPO;

import java.util.List;

public interface GisWaterUserInfoPOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GisWaterUserInfoPO record);

    int insertSelective(GisWaterUserInfoPO record);

    GisWaterUserInfoPO selectByPrimaryKey(Long id);

    List<GisWaterUserInfoPO> selectAll();

    int updateByPrimaryKeySelective(GisWaterUserInfoPO record);

    int updateByPrimaryKey(GisWaterUserInfoPO record);

}