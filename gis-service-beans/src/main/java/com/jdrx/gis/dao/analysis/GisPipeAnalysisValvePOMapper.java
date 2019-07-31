package com.jdrx.gis.dao.analysis;


import com.jdrx.gis.beans.entry.analysis.GisPipeAnalysisValvePO;

public interface GisPipeAnalysisValvePOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GisPipeAnalysisValvePO record);

    int insertSelective(GisPipeAnalysisValvePO record);

    GisPipeAnalysisValvePO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GisPipeAnalysisValvePO record);

    int updateByPrimaryKey(GisPipeAnalysisValvePO record);
}