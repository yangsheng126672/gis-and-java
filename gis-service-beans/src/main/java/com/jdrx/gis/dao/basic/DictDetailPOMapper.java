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


	/**
	 * 根据dict_type里面的val字段的值查dict_detail的配置详情信息
	 * @param val
	 * @return
	 */
	List<DictDetailPO> selectByVal(String val);
}