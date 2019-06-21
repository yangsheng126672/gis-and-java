package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.entry.basic.DictDetailPO;

import java.util.List;

public interface DictDetailPOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(DictDetailPO record);

    int insertSelective(DictDetailPO record);

    DictDetailPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DictDetailPO record);

    int updateByPrimaryKey(DictDetailPO record);

	List<DictDetailPO> selectByVal(String val);
}