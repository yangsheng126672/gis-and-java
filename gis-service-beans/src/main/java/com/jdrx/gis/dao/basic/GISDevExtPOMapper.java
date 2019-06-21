package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.dto.query.AttrQeuryDTO;
import com.jdrx.gis.beans.vo.query.GISDevExtVO;
import com.jdrx.gis.beans.entry.basic.GISDevExtPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GISDevExtPOMapper {

    GISDevExtPO getDevExtByDevId(Long devId);

	/**
	 * 根据ID集合查询设备列表信息
	 */
	List<GISDevExtVO> findDevListByDevIds(@Param("devIds") List<Long> devIds);

	/**
	 *
	 * @param dto
	 * @return
	 */
	List<GISDevExtVO> findDevListByAreaOrInputVal(@Param("dto") AttrQeuryDTO dto);

}