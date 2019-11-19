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

	/**
	 * 逻辑删除字典详情数据
	 * @param id
	 * @return
	 */
	int logicDeleteById(Long id);

	/**
	 * 根据typeId查字典数据
	 * @param typeId
	 * @return
	 */
	List<DictDetailPO> findDictDetailListByTypeId(Long typeId);

	/**
	 * 根据接口地址获取接口交易编码
	 * @param apiPath
	 * @return
	 */
	String getTransCodeByApiPath(String apiPath);
}