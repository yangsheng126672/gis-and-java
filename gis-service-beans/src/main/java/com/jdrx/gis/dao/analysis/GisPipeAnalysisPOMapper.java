package com.jdrx.gis.dao.analysis;


import com.jdrx.gis.beans.dto.analysis.RecondParamasDTO;
import com.jdrx.gis.beans.entry.analysis.GisPipeAnalysisPO;

import java.util.List;

public interface GisPipeAnalysisPOMapper {

    int insertSelective(GisPipeAnalysisPO record);

    List<GisPipeAnalysisPO> selectAll();

    List<GisPipeAnalysisPO> selectByParamas(RecondParamasDTO recondParamasDTO);

}