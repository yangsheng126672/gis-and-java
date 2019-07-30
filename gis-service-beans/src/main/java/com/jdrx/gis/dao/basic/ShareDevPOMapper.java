package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.entry.basic.ShareDevPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

	/**
	 * 根据多个设备类型，查询它们下面的设备信息
	 * @param typeIds 叶子节点的类型
	 * @return
	 */
	List<ShareDevPO> findDevListByTypeIds(@Param("typeIds") String typeIds);

}