package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.entity.basic.GISShareDevTypeExtPO;

public interface GISShareDevTypeExtPOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GISShareDevTypeExtPO record);

    int insertSelective(GISShareDevTypeExtPO record);

    GISShareDevTypeExtPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GISShareDevTypeExtPO record);

    int updateByPrimaryKey(GISShareDevTypeExtPO record);
}