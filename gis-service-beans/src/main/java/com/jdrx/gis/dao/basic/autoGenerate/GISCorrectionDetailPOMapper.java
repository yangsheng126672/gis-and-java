package com.jdrx.gis.dao.basic.autoGenerate;

import com.jdrx.gis.beans.entity.dataManage.GISCorrectionDetailPO;

public interface GISCorrectionDetailPOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GISCorrectionDetailPO record);

    int insertSelective(GISCorrectionDetailPO record);

    GISCorrectionDetailPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GISCorrectionDetailPO record);

    int updateByPrimaryKey(GISCorrectionDetailPO record);
}