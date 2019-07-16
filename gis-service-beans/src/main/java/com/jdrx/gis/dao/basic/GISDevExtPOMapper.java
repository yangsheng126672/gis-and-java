package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.dto.query.AttrQeuryDTO;
import com.jdrx.gis.beans.entry.basic.GISDevExtPO;
import com.jdrx.gis.beans.entry.query.PipeLengthPO;
import com.jdrx.gis.beans.vo.query.GISDevExtVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GISDevExtPOMapper {

    GISDevExtPO getDevExtByDevId(Long devId);

	/**
	 * 根据ID集合查询设备列表信息
	 */
	List<GISDevExtVO> findDevListByDevIds(@Param("devIds") List<Long> devIds);

	/**
	 * 根据所选区域或属性键入的参数值查设备列表信息
	 * @param dto
	 * @return
	 */
	List<GISDevExtVO> findDevListByAreaOrInputVal(@Param("dto") AttrQeuryDTO dto, @Param("devIds") String devIds);

	/**
	 * 根据所选区域或属性键入的参数值查设备列表 个数
	 * @param dto
	 * @param devIds
	 * @return
	 */
	Integer findDevListByAreaOrInputValCount(@Param("dto") AttrQeuryDTO dto, @Param("devIds") String devIds);

	/**
	 * 查水管总长度
	 * @return
	 */
	PipeLengthPO findPipeLength(@Param("val") String val);
}