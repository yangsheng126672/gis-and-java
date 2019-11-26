package com.jdrx.gis.service.basic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jdrx.gis.beans.dto.base.PageDTO;
import com.jdrx.gis.beans.dto.basic.MeasurementDTO;
import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.beans.entry.basic.GISDevExtPO;
import com.jdrx.gis.beans.entry.basic.MeasurementPO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.beans.vo.basic.DefaultLayersVO;
import com.jdrx.gis.beans.vo.basic.FeatureVO;
import com.jdrx.gis.beans.vo.basic.InspectionVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.MeasurementPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.dao.query.DevQueryDAO;
import com.jdrx.gis.service.query.LayerService;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.jdbc.beans.vo.PageVO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.jdrx.gis.util.ComUtil.getChildNodes;

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

	@Autowired
	private DictDetailService detailService;

	@Autowired
	private LayerService layerService;

	@Autowired
	private DictConfig dictConfig;

	public final static String SHARE_DEV_TYPE_NAME_PIPE = "水管";

	public final static String SHARE_DEV_TYPE_NAME_OTHER = "其他";

	/**
	 * 递归处理   数据库树结构数据->树形json
	 * @param id
	 * @param list
	 * @return
	 */
	public  JSONArray getNodeJson(Long id,List<ShareDevTypePO> list,final int level) throws BizException{
		if (list.size() ==0){
			return null;
		}
		String layerUrl = null;
		String iconUrl = null;
		List<Map<String,String>> mapList = new ArrayList<>();
		List<Map<String,String>> iconmapList = new ArrayList<>();
		JSONArray childTree = new JSONArray();
		if (level<3){
			//当前层级当前点下的所有子节点
			List<ShareDevTypePO> childList = getChildNodes(id,list);
			for (ShareDevTypePO node : childList) {
				layerUrl = null;
				Long[] ids = new Long[1];
				JSONObject o = new JSONObject();
				o.put("id",node.getId());
				o.put("name", node.getName());
				o.put("type", node.getLimbLeaf());
				o.put("pid",node.getPId());
				ids[0] = node.getId();
				//获取图层url
				mapList = detailService.findLayerUrlListByTypeIds(ids,1);
				if (mapList.size()>0){
					layerUrl = mapList.get(0).get(node.getId().toString());
				}
				o.put("layerUrl",layerUrl);
				//获取图例url
				iconmapList = detailService.findLayerUrlListByTypeIds(ids,2);
				if (iconmapList.size()>0){
					iconUrl = iconmapList.get(0).get(node.getId().toString());
				}
				o.put("iconUrl",iconUrl);
				JSONArray childs = getNodeJson(node.getId(),list,level+1);  //递归调用该方法
				if(!childs.isEmpty()) {
					o.put("children",childs);
				}
				childTree.fluentAdd(o);
			}
		}
		return childTree;
	}

	/**
	 * 获取图例列表
	 * @param id
	 * @param list
	 * @param level
	 * @return
	 * @throws BizException
	 */
	public  JSONArray getIconJsonTree(Long id,List<ShareDevTypePO> list,int level) throws BizException{
		if (list.size() ==0){
			return null;
		}
		String iconUrl = null;
		List<Map<String,String>> iconmapList = new ArrayList<>();
		JSONArray childTree = new JSONArray();
		//当前层级当前点下的所有子节点
		List<ShareDevTypePO> childList = getChildNodes(id,list);
		for (ShareDevTypePO node : childList) {
			//获取图例url
			Long[] ids = new Long[1];
			ids[0] = node.getId();
			iconmapList = detailService.findLayerUrlListByTypeIds(ids,2);
			if (iconmapList.size()>0){
				iconUrl = iconmapList.get(0).get(node.getId().toString());
			}
			JSONObject o = new JSONObject();
			o.put("id",node.getId());
			o.put("name", node.getName());
			o.put("type", node.getLimbLeaf());
			o.put("iconUrl",iconUrl);

			//递归调用该方法
			JSONArray childs = getIconJsonTree(node.getId(),list,level+1);

			if (level<2){
				if(!childs.isEmpty()) {
					o.put("children",childs);
				}
				childTree.fluentAdd(o);
			}else if (level == 2){
				if (!childs.isEmpty()){
					childTree.addAll(childs);
				}
				if (2 == node.getLimbLeaf()){
					childTree.fluentAdd(o);
				}
			}else{
				childTree.fluentAdd(o);
				if (!childs.isEmpty()){
					childTree.addAll(childs);
				}
			}
		}
		return childTree;
	}
	/**
	 * 查询所有设备类型
	 * @return
	 */
	public JSONArray findDevTypeList() throws BizException{
		try {
			List<ShareDevTypePO> list = shareDevTypePOMapper.findDevTypeList();
			JSONArray jsonArray = getNodeJson(-1L, list,0);
			return jsonArray;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询所有设备类型失败，{}", e.getMessage());
			throw new BizException("查询所有设备类型失败!");
		}
	}

	/**
	 * 获取图例层级及图标
	 * @return
	 * @throws BizException
	 */
	public JSONArray findDevTypeIconList() throws BizException{
		try {
			List<ShareDevTypePO> list = shareDevTypePOMapper.findDevTypeList();
			JSONArray jsonArray = getIconJsonTree(-1L, list,0);
			jsonArray.sort(Comparator.comparing(obj -> {
				String name = ((JSONObject) obj).getString("name");
				if(name.equals(SHARE_DEV_TYPE_NAME_PIPE)) {
					return -1;
				}
				if(name.equals(SHARE_DEV_TYPE_NAME_OTHER)) {
					return 1;
				}
				return 0;
			}));
			return jsonArray;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询图例图标失败，{}", e.getMessage());
			throw new BizException("查询图例图标失败!");
		}
	}

	/**
	 * 根据设备ID查当前设备的属性信息
	 * @param devId
	 */
	public GISDevExtPO getDevExtByDevId(String devId) throws BizException {
		try {
			GISDevExtPO gisDevExtPO = gisDevExtPOMapper.getDevExtByDevId(devId);
			return gisDevExtPO;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("根据设备ID{}查当前设备的属性信息失败!", devId);
			throw new BizException("根据设备ID查当前设备的属性信息失败!");
		}
	}

	/**
	 * 获取所有测量列表
	 * @return
	 */
	public PageVO<MeasurementPO> findMeasurementList(PageDTO dto) throws BizException {
		try {
			PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy());
			Page<MeasurementPO> list = (Page<MeasurementPO>)measurementPOMapper.findMeasurementList();
			return new PageVO<>(list);
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
	public Integer saveMeasurement(MeasurementDTO dto) throws BizException{
		try {
			return measurementPOMapper.insertSelective(dto);
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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

	/**
	 * 获取点线所有要素
	 * @return
	 * @throws BizException
	 */
	public List<String> getAllFeaturesUrl() throws BizException{
		try {
			List<String> list = new ArrayList<>();
			List<String>urlList = layerService.getLayerUrls();
			for (String str:urlList){
				list.add(str+"?where=id%3E0&outFields=dev_id&geometryType=esriGeometryEnvelope&returnExtentsOnly=false&f=geojson");
			}
			return list;
		}  catch (Exception e) {
			e.printStackTrace();
			Logger.error("获取所有要素信息失败，{}", e.getMessage());
			throw new BizException("获取所有要素信息失败！");
		}
	}

	/**
	 * 获取巡检系统所需图层url
	 * @return
	 */
	public InspectionVO getXjLayerSourceUrl(){
		InspectionVO vo = new InspectionVO();
		String layerUrl = null;
		Map<String,String> map = new HashMap<>();
		try {
			layerUrl = dictConfig.getXjSourceUrl();
			List<DictDetailPO> detailPOs = detailService.findDetailsByTypeVal(layerUrl);
			for (DictDetailPO dictDetail:detailPOs){
				map.put(dictDetail.getName(),dictDetail.getVal());
				if (dictDetail.getName().equals("wms")){
					vo.setWms(dictDetail.getVal());
				}else if (dictDetail.getName().equals("point")){
					vo.setPoint(dictDetail.getVal());
				}else if (dictDetail.getName().equals("line")){
					vo.setLine(dictDetail.getVal());
				}else if (dictDetail.getName().equals("x")){
					vo.setX(dictDetail.getVal());
				}else if (dictDetail.getName().equals("y")){
					vo.setY(dictDetail.getVal());
				}else if (dictDetail.getName().equals("title")){
					vo.setTitle(dictDetail.getVal());
				}else if (dictDetail.getName().equals("extent")){
					vo.setExtent(dictDetail.getVal());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}


	/**
	 * 获取默认地图相关配置
	 * @return
	 */
	public DefaultLayersVO getDefaultLayers(){
		DefaultLayersVO vo = new DefaultLayersVO();
		String layerUrl = null;
		Map<String,String> map = new HashMap<>();
		try {
			layerUrl = dictConfig.getDefaultLayerUrl();
			List<DictDetailPO> detailPOs = detailService.findDetailsByTypeVal(layerUrl);
			for (DictDetailPO dictDetail:detailPOs){
				map.put(dictDetail.getName(),dictDetail.getVal());
				if (dictDetail.getName().equals("cad")){
					vo.setCad(dictDetail.getVal());
				}else if (dictDetail.getName().equals("point")){
					vo.setPoint(dictDetail.getVal());
				}else if (dictDetail.getName().equals("line")){
					vo.setLine(dictDetail.getVal());
				}else if (dictDetail.getName().equals("x")){
					vo.setX(dictDetail.getVal());
				}else if (dictDetail.getName().equals("y")){
					vo.setY(dictDetail.getVal());
				}else if (dictDetail.getName().equals("title")){
					vo.setTitle(dictDetail.getVal());
				}else if (dictDetail.getName().equals("extent")){
					vo.setExtent(dictDetail.getVal());
				}else if (dictDetail.getName().equals("resolutions")){
					vo.setResolutions(dictDetail.getVal());
				}else if (dictDetail.getName().equals("popLayer")){
					vo.setPopLayer(dictDetail.getVal());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	/**
	 * 根据关键字搜索设备
	 * @param val
	 */
	public List<FeatureVO> getFeaturesByString(String val)throws BizException{
		List<FeatureVO> list = new ArrayList<>();
		try {
			list = gisDevExtPOMapper.findFeaturesByString(val);
		}catch (Exception e){
			e.printStackTrace();
		}
		return list;
	}



}