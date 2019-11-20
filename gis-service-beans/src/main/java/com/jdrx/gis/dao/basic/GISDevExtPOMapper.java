package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.dto.query.AttrQeuryDTO;
import com.jdrx.gis.beans.entry.basic.CodeXYPO;
import com.jdrx.gis.beans.entry.basic.GISDevExtPO;
import com.jdrx.gis.beans.entry.query.PipeLengthPO;
import com.jdrx.gis.beans.vo.basic.FeatureVO;
import com.jdrx.gis.beans.vo.datamanage.NeoLineVO;
import com.jdrx.gis.beans.vo.datamanage.NeoPointVO;
import com.jdrx.gis.beans.vo.query.GISDevExt2VO;
import com.jdrx.gis.beans.vo.query.GISDevExtVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GISDevExtPOMapper {

    GISDevExtPO getDevExtByDevId(String devId);

	/**
	 * 根据ID集合查询设备列表信息
	 */
	List<GISDevExtVO> findDevListByDevIds(@Param("devIds") List<String> devIds);

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

	/**
	 * 根据devIds 获取设备属性信息，并附type_id
	 * @param devIds
	 * @return
	 */
	List<GISDevExt2VO> findDevListAttTypeByDevIds(@Param("devIds") String devIds);

	/**
	 * 根据关键字搜索相关设备要素
	 * @param val
	 * @return
	 */
	List<FeatureVO> findFeaturesByString(@Param("val") String val);

	/**
	 * 根据devIds获取要素基础信息
	 * @param devIds
	 * @return
	 */
	List<FeatureVO> findFeaturesByDevIds(@Param("devIds") String devIds);

	/**
	 * 获取管点数据
	 * @return
	 */
	List<NeoPointVO> getPointDevExt(String devIds);

	/**
	 * 获取管线数据
	 * @return
	 */
	List<NeoLineVO> getLineDevExt(String devIds);

	/*
	 * 根据设备的编码获取设备信息
	 * @param code
	 * @return
	 */
	GISDevExtPO selectByCode(@Param("code") String code);


	Map<String, String> findGeomMapByPointCode(@Param("codeXYPO") CodeXYPO codeXYPO, @Param("srid") String srid);


	Integer batchInsertSelective(@Param("gisDevExtPOList") List<GISDevExtPO> gisDevExtPOList);

}

