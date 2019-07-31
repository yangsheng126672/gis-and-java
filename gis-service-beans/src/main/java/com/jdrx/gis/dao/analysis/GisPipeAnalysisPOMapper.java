package com.jdrx.gis.dao.analysis;


import com.jdrx.gis.beans.entry.analysis.GisPipeAnalysisPO;
import org.apache.ibatis.annotations.Options;

public interface GisPipeAnalysisPOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GisPipeAnalysisPO record);
    int insertSelective(GisPipeAnalysisPO record);

    GisPipeAnalysisPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GisPipeAnalysisPO record);

    int updateByPrimaryKey(GisPipeAnalysisPO record);
}