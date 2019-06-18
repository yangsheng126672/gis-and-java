package com.jdrx.dao.basic;

import com.jdrx.beans.entry.basic.DictTypePO;

public interface DictTypePOMapper {


    int deleteByPrimaryKey(Long id);

    int insert(DictTypePO record);

    int insertSelective(DictTypePO record);

    DictTypePO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DictTypePO record);

    int updateByPrimaryKey(DictTypePO record);
}