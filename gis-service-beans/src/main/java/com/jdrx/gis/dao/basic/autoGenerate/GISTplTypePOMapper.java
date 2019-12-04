package com.jdrx.gis.dao.basic.autoGenerate;

import com.jdrx.gis.beans.entity.basic.GISTplTypePO;

public interface GISTplTypePOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GISTplTypePO record);

    int insertSelective(GISTplTypePO record);

    GISTplTypePO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GISTplTypePO record);

    int updateByPrimaryKey(GISTplTypePO record);
}