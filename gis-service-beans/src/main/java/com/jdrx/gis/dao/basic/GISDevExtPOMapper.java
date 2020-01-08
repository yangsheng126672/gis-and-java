package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.anno.NoAuthData;
import com.jdrx.gis.beans.dto.query.AttrQeuryDTO;
import com.jdrx.gis.beans.entity.basic.CodeXYPO;
import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.beans.entity.dataManage.MultiMediaPO;
import com.jdrx.gis.beans.entity.query.PipeLengthPO;
import com.jdrx.gis.beans.vo.basic.AnalysisVO;
import com.jdrx.gis.beans.vo.basic.FeatureVO;
import com.jdrx.gis.beans.vo.basic.PipeLengthVO;
import com.jdrx.gis.beans.vo.basic.PointVO;
import com.jdrx.gis.beans.vo.datamanage.ExportCadVO;
import com.jdrx.gis.beans.vo.datamanage.LineXYVo;
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
	List<FeatureVO> findFeaturesByDevIds(@Param("devIds") List<String> devIds);

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

	/**
	 * 根据设备的编码获取设备信息
	 * @param code
	 * @return
	 */
	@NoAuthData
	GISDevExtPO selectByCode(@Param("code") String code);

	/**
	 * 根据传入的坐标列表转换成geometry的文本形式
	 * @param codeXYPOs
	 * @param srid
	 * @return
	 */
	List<Map<String, Object>> findGeomMapByPointCode(@Param("codeXYPOs") List<CodeXYPO> codeXYPOs, @Param("srid") Integer srid);

	/**
	 * 批量插入
	 * @param gisDevExtPOList
	 * @return
	 */
	Integer batchInsertSelective(@Param("gisDevExtPOList") List<GISDevExtPO> gisDevExtPOList);

	/**
	 * 根据批次号查询该批数据
	 * @param batchNum
	 * @return
	 */
	List<GISDevExtPO> selectExistRecords(@Param("batchNum") String batchNum);

	/**
	 * 经纬度坐标系转自定义坐标系
	 * @param geom
	 * @param srid
	 * @return
	 */
	String transformWgs84ToCustom(@Param("geom") String geom,@Param("srid") Integer srid);

	/**
	 * 保存单个实体对象
	 * @param record
	 * @return
	 */
	int insertSelective(GISDevExtPO record);

	/**
	 * 将geom从WKB转为WKT
	 * @param geom
	 * @return
	 */
	String transformGeomAsText(@Param("geom") String geom);

	/**
	 * 将geom转为带指定srid的字符串
	 * @param geom
	 * @param srid
	 * @return
	 */
	String addGeomWithSrid(@Param("geom") String geom,@Param("srid") Integer srid);

	/**
	 * 根据geom获取点XY坐标
	 * @param geom
	 * @return
	 */
	PointVO getPointXYFromGeom(@Param("geom") String geom);

    /**
     * 根据4326获取到4544坐标系
     */
    PointVO get4544From4326(@Param("geom") String geom);

	/**
	 * 根据设备id逻辑删除
	 * @param devId
	 * @return
	 */
	Integer deleteDevExtByDevId(@Param("devId")String devId,@Param("loginUserName") String loginUserName);

	/**
	 * 批量更新
	 * @param gisDevExtPOList
	 * @return
	 */
	Integer batchUpdate(@Param("gisDevExtPOList") List<GISDevExtPO> gisDevExtPOList);

	/**
	 * 根据code获取ext数据
	 * @param codes
	 * @return
	 */
	@NoAuthData
	List<GISDevExtPO> selectByCodes(@Param("codes") String codes);

	/**
	 * 选择更细
	 * @param record
	 * @return
	 */
	int updateByPrimaryKeySelective(GISDevExtPO record);

    /**
     * 管点移动更改share_dev中的lng和lag
     */
     int updateShareDev(@Param("x") String x,@Param("y")String y,@Param("devId")String devId);

	/**
	 * 根据管点编码查询关联的管线
	 * @param val
	 * @return
	 */
	List<GISDevExtPO> selectLineByCode(@Param("val") String val);

	/**
	 * 更新设备的data_info
	 * @param record
	 * @return
	 */
	int updateDataInfoByDevId(GISDevExtPO record);

	/**
	 * 获取管段长度(系统统一保留三位小数）
	 * @param geom
	 * @return
	 */
	Double getLengthByGeomStr(@Param("geom") String geom);

	/**
	 * 根据id查询其geom
	 * @param id
	 * @return
	 */
	List<ExportCadVO> selectGeomByTypeId(@Param("id") Long id);

	/**
	 * 根据devId获取要素基础信息
	 * @param devId
	 * @return
	 */
	FeatureVO findFeaturesByDevId(@Param("devId") String devId);

	/**
	 * 根据权限值获取管网总长度(系统统一保留三位小数）
	 * @return
	 */
	 List<PipeLengthVO> getPipeLengthByAuthId();

	/**
	 * 根据devIds获取孤立设备基础分析信息
	 * @param devIds
	 * @return
	 */
	List<AnalysisVO> getLonelyShareDevByDevIds(@Param("devIds") List<String> devIds);

    /**
     * 根据设备id逻辑删除share_dev
     * @param devId
     * @return
     */
    Integer deleteShareDevByDevId(@Param("devId")String devId);
    /**
     * 根据devIds获取重复点基础信息
     * @param devIds
     * @return
     */

    List<AnalysisVO> getRepeatPointsByDevIds(@Param("devIds") List<String> devIds);
	/**
	 * 根据devIds获取重复线基础信息
	 * @param devIds
	 * @return
	 */
	List<AnalysisVO> getRepeatLinesByDevIds(@Param("devIds") List<String> devIds);

	/**
	 * 根据geom获取空间类型
	 * @param geomStr
	 * @return
	 */
	String getGeomTypeByGeomStr(@Param("geomStr") String geomStr);

	/**
	 * 保存视频和图片
	 * @param po
	 * @return
	 */
	int updateMultiVideo(@Param("po") MultiMediaPO po);

	/**
	 * 根据devId获取线的起始坐标和终止坐标
	 * @param devId
	 * @return
	 */
	LineXYVo getXYByDevId(@Param("devId") String devId);
	/**
	 * 根据前端所传起点编码和终点编码判断是否存在该管线
	 * @param
	 * @return
	 */
	List<GISDevExtPO> getFromStartCodeAndEndCode(@Param("startCode") String startCode, @Param("endCode") String endCode);


}

