package com.jdrx.dao.basic;

import com.jdrx.beans.entry.basic.ShareDevPO;

public interface ShareDevPOMapper {

    int insert(ShareDevPO record);

    int insertSelective(ShareDevPO record);

    ShareDevPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ShareDevPO record);

    int updateByPrimaryKey(ShareDevPO record);

	/**
	 * 根据主键获取type_id
	 * @param id
	 * @return
	 */
	Long getTypeIdPrimaryKey(Long id);
}