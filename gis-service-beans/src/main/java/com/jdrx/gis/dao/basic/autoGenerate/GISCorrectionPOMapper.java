package com.jdrx.gis.dao.basic.autoGenerate;

import com.jdrx.gis.beans.entity.dataManage.GISCorrectionPO;

public interface GISCorrectionPOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GISCorrectionPO record);

    int insertSelective(GISCorrectionPO record);

    GISCorrectionPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GISCorrectionPO record);

    int updateByPrimaryKey(GISCorrectionPO record);
}