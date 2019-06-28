package com.jdrx.gis.service.basic;

import com.alibaba.fastjson.JSONArray;
import com.jdrx.gis.beans.entry.basic.GISDevExtPO;
import com.jdrx.gis.beans.entry.basic.MeasurementPO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.MeasurementPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.dao.query.DevQueryDAO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jdrx.gis.util.ComUtil.getNodeJson;

/**
 * @Description: 基本功能中的图层服务
 * @Author: liaosijun
 * @Time: 2019/6/14 11:30
 */
@Service
public class BasicDevQuery {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(BasicDevQuery.class);

	@Autowired
	private ShareDevTypePOMapper shareDevTypePOMapper;

	@Autowired
	private GISDevExtPOMapper gisDevExtPOMapper;

	@Autowired
	private MeasurementPOMapper measurementPOMapper;

	@Autowired
	private DevQueryDAO devQueryDAO;

	/**
	 * 查询所有设备类型
	 * @return
	 */
	public JSONArray findDevTypeList(){
		List<ShareDevTypePO> list  = shareDevTypePOMapper.findDevTypeList();
		JSONArray jsonArray = getNodeJson(-1L,list);
		return jsonArray;
	}

	/**
	 * 根据设备ID查当前设备的属性信息
	 * @param devId
	 */
	public GISDevExtPO getDevExtByDevId(Long devId){
		GISDevExtPO gisDevExtPO = gisDevExtPOMapper.getDevExtByDevId(devId);
		return gisDevExtPO;
	}

	/**
	 * 获取所有测量列表
	 * @return
	 */
	public List<MeasurementPO> findMeasurementList(){
		List<MeasurementPO> list  = measurementPOMapper.findMeasurementList();
		return list;
	}

	/**
	 * 保存测量信息
	 * @param dto
	 * @return
	 */
	public Integer saveMeasurement(MeasurementPO dto){
		return measurementPOMapper.insertSelective(dto);
	}

	/**
	 * 删除测量信息
	 * @param id
	 * @return
	 */
	public Integer deleteMeasurementByID(Long id){
		return measurementPOMapper.deleteByPrimaryKey(id);
	}

	/**
	 * 获取首层图层
	 * @return
	 */
	public List<ShareDevTypePO> findFirstHierarchyDevType() {
		List<ShareDevTypePO> devTypePOs = devQueryDAO.findFirstHierarchyDevType();
		return devTypePOs;
	}

}