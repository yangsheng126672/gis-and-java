package com.jdrx.dao.basic;

import com.jdrx.beans.entry.basic.GISDevExtPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GISDevExtPOMapper {

    GISDevExtPO getDevExtByDevId(Long devId);

	/**
	 * 根据ID集合查询设备列表信息
	 */
	List<GISDevExtPO> findDevListByDevIds(@Param("devIds") List<Long> devIds);

}