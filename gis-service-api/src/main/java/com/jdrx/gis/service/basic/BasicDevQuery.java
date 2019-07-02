package com.jdrx.gis.service.basic;

import com.alibaba.fastjson.JSONArray;
import com.jdrx.gis.beans.entry.basic.GISDevExtPO;
import com.jdrx.gis.beans.entry.basic.MeasurementPO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.MeasurementPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.dao.query.DevQueryDAO;
import com.jdrx.platform.commons.rest.exception.BizException;
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
	public JSONArray findDevTypeList() throws BizException{
		try {

			List<ShareDevTypePO> list = shareDevTypePOMapper.findDevTypeList();
			JSONArray jsonArray = getNodeJson(-1L, list);
			return jsonArray;
		} catch (Exception e) {
			Logger.error("查询所有设备类型失败，{}", e.getMessage());
			throw new BizException("查询所有设备类型失败!");
		}
	}

	/**
	 * 根据设备ID查当前设备的属性信息
	 * @param devId
	 */
	public GISDevExtPO getDevExtByDevId(Long devId) throws BizException {
		try {
			GISDevExtPO gisDevExtPO = gisDevExtPOMapper.getDevExtByDevId(devId);
			return gisDevExtPO;
		} catch (Exception e) {
			Logger.error("根据设备ID{}查当前设备的属性信息失败!", devId);
			throw new BizException("根据设备ID查当前设备的属性信息失败!");
		}
	}

	/**
	 * 获取所有测量列表
	 * @return
	 */
	public List<MeasurementPO> findMeasurementList() throws BizException {
		try {
			List<MeasurementPO> list = measurementPOMapper.findMeasurementList();
			return list;
		} catch (Exception e) {
			Logger.error("获取测量列表失败！", e.getMessage());
			throw new BizException("获取测量列表失败!");
		}
	}

	/**
	 * 保存测量信息
	 * @param dto
	 * @return
	 */
	public Integer saveMeasurement(MeasurementPO dto) throws BizException{
		try {
			return measurementPOMapper.insertSelective(dto);
		} catch (Exception e) {
			Logger.error("保存测量信息失败！", e.getMessage());
			throw new BizException("保存测量信息失败！");
		}
	}

	/**
	 * 删除测量信息
	 * @param id
	 * @return
	 */
	public Integer deleteMeasurementByID(Long id) throws BizException{
		try {
			return measurementPOMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			Logger.error("删除测量信息失败！{}", e.getMessage());
			throw new BizException("删除测量信息失败！");
		}
	}

	/**
	 * 获取首层图层
	 * @return
	 */
	public List<ShareDevTypePO> findFirstHierarchyDevType() throws BizException {
		try {
			List<ShareDevTypePO> devTypePOs = devQueryDAO.findFirstHierarchyDevType();
			return devTypePOs;
		}  catch (Exception e) {
			Logger.error("查询图层失败，{}", e.getMessage());
			throw new BizException("查询图层失败！");
		}
	}

}