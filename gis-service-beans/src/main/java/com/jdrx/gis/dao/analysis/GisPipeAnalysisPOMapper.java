package com.jdrx.gis.dao.analysis;


import com.jdrx.gis.beans.dto.analysis.RecondParamasDTO;
import com.jdrx.gis.beans.entry.analysis.GisPipeAnalysisPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GisPipeAnalysisPOMapper {

    int insertSelective(GisPipeAnalysisPO record);

    GisPipeAnalysisPO selectById(Long id);

    List<GisPipeAnalysisPO> selectByParamas(@Param("dto") RecondParamasDTO recondDTO);

}