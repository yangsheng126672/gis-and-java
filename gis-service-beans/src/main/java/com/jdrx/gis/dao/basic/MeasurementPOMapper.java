package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.dto.basic.MeasurementDTO;
import com.jdrx.gis.beans.entry.basic.MeasurementPO;

import java.util.List;

public interface MeasurementPOMapper {

    /**
     * 获取测量列表
     * @return
     */
    List<MeasurementPO> findMeasurementList();

    /**
     * 删除测量信息
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 保存测量信息
     * @param record
     * @return
     */
    int insertSelective(MeasurementDTO record);

}