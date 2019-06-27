package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.entry.basic.DictTypePO;

public interface DictTypePOMapper {


    int deleteByPrimaryKey(Long id);

    int insert(DictTypePO record);

    int insertSelective(DictTypePO record);

    DictTypePO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DictTypePO record);

    int updateByPrimaryKey(DictTypePO record);

	int logicDeleteById(Long id);
}