package com.jdrx.gis.service.basic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.base.PageDTO;
import com.jdrx.gis.beans.dto.base.TypeIdDTO;
import com.jdrx.gis.beans.dto.basic.BookMarkDTO;
import com.jdrx.gis.beans.dto.basic.MeasurementDTO;
import com.jdrx.gis.beans.entity.basic.*;
import com.jdrx.gis.beans.entity.cad.*;
import com.jdrx.gis.beans.entity.user.SysOcpUserPo;
import com.jdrx.gis.beans.vo.basic.DefaultLayersVO;
import com.jdrx.gis.beans.vo.basic.FeatureVO;
import com.jdrx.gis.beans.vo.datamanage.ExportCadVO;
import com.jdrx.gis.beans.vo.basic.PipeLengthVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.config.SwitchConfig;
import com.jdrx.gis.dao.basic.BookMarkMapper;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.MeasurementPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.dao.query.DevQueryDAO;
import com.jdrx.gis.dubboRpc.UserRpc;
import com.jdrx.gis.filter.assist.OcpService;
import com.jdrx.gis.service.query.LayerService;
import com.jdrx.gis.util.FileUtil;
import com.jdrx.gis.util.JavaFileToFormUpload;
import com.jdrx.gis.util.Neo4jUtil;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.jdbc.beans.vo.PageVO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

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

	@Autowired
	private PathConfig pathConfig;

	@Autowired
	private Neo4jUtil neo4jUtil;

	@Autowired
	private UserRpc userRpc;

	public final static String SHARE_DEV_TYPE_NAME_PIPE = "水管";

	public final static String SHARE_DEV_TYPE_NAME_OTHER = "其他";

	@Autowired
	private SwitchConfig switchConfig;

	@Autowired
	private OcpService ocpService;

	@Autowired
	private BookMarkMapper bookMarkMapper;
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
	public Integer saveMeasurement(MeasurementDTO dto,String deptPath) throws BizException{
		try {
			Long deptId = ocpService.setDeptPath(deptPath).getUserWaterworksDeptId();
			dto.setBelongTo(deptId);
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
	 * 获取默认地图相关配置
	 * @return
	 */
	public DefaultLayersVO getDefaultLayers(String deptPath) throws BizException{
		DefaultLayersVO vo = new DefaultLayersVO();
		String layerUrl = null;
		Map<String,String> map = new HashMap<>();
		Long deptId = null;
		String extentStr = null;
		try {
			if (!StringUtils.isEmpty(deptPath)){
				deptId = ocpService.setDeptPath(deptPath).getUserWaterworksDeptId();
				//获取地图中心点
				String ceterStr = getMapCenterByByAuthId(deptId);
				if (!StringUtils.isEmpty(ceterStr)){
					vo.setX(ceterStr.split(",")[0]);
					vo.setY(ceterStr.split(",")[1]);
				}
				//获取图层范围
				extentStr = getLayerExtentByAuthId(deptId);
				vo.setLayerExtent(extentStr);
			}

			Long tmpNumber = System.currentTimeMillis();
			String tmpStr = "&number="+String.valueOf(tmpNumber);
			layerUrl = dictConfig.getDefaultLayerUrl();
			List<DictDetailPO> detailPOs = detailService.findDetailsByTypeVal(layerUrl);
			for (DictDetailPO dictDetail:detailPOs){
				map.put(dictDetail.getName(),dictDetail.getVal());
				if (dictDetail.getName().equals("cad")){
					vo.setCad(dictDetail.getVal());
				}else if (dictDetail.getName().equals("point")){
					vo.setPoint(dictDetail.getVal()+"&inSR=4326&geometry="+extentStr + tmpStr);
				}else if (dictDetail.getName().equals("line")){
					vo.setLine(dictDetail.getVal()+"&inSR=4326&geometry="+extentStr + tmpStr);
				}else if (dictDetail.getName().equals("title")){
					vo.setTitle(dictDetail.getVal());
				}else if (dictDetail.getName().equals("extent")){
					vo.setExtent(dictDetail.getVal());
				}else if (dictDetail.getName().equals("resolutions")){
					vo.setResolutions(dictDetail.getVal());
				}else if (dictDetail.getName().equals("popLayer")){
					vo.setPopLayer(dictDetail.getVal());
				}else if (dictDetail.getName().equals("x")){
					if(vo.getX() == null){
						vo.setX(dictDetail.getVal());
					}
				}else if (dictDetail.getName().equals("y")){
					if (vo.getY() == null){
						vo.setY(dictDetail.getVal());
					}
				}else if (dictDetail.getName().equals("layerExtent")){
					if (vo.getLayerExtent() == null){
						vo.setLayerExtent(dictDetail.getVal());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			throw new BizException("获取地图默认配置失败");
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

	/**
	 * 获取地图中心点
	 * @param deptId
	 * @return
	 */
	public String getMapCenterByByAuthId(Long deptId){
		String centerStr = null;
		try {
			//获取地图中心点
			List<DictDetailPO> list = detailService.findDetailsByTypeVal(dictConfig.getMapCenterVal());
			for(DictDetailPO po: list){
				if (deptId != null){
					if (po.getName().equals(deptId.toString())){
						centerStr = po.getVal();
					}
				}else {
					if (StringUtils.isEmpty(po.getName())){
						centerStr = po.getVal();
					}
				}

			}
		}catch (Exception e){
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
		return centerStr;
	}

	/**
	 * 获取图层范围
	 * @param deptId
	 * @return
	 */
	public String getLayerExtentByAuthId(Long deptId){
		if (deptId == null){
			return null;
		}
		String extent = null;
		try {
			List<DictDetailPO> list = detailService.findDetailsByTypeVal(dictConfig.getLayerExtent());
			for(DictDetailPO po: list){
				if (deptId != null){
					if (po.getName().equals(String.valueOf(deptId))){
						extent = po.getVal();
					}
				}else {
					if (StringUtils.isEmpty(po.getName())){
						extent = po.getVal();
					}
				}

			}
		}catch (Exception e){
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
		return extent;
	}

	/**
	 * 导出CAD的核心代码
	 * @param list_id
	 */
	public String findLayerById(List<TypeIdDTO> list_id) throws BizException {
		try {
			Document doc = new Document();//工具类，制作CAD图层用
			for (TypeIdDTO id:list_id) {
				List<ExportCadVO> list = gisDevExtPOMapper.selectGeomByTypeId(id.getId());
				List<FeatureVO> featureVO = new ArrayList<>();
				if(id.getId()==19) {//当节点为阀门时，查出其再逻辑管网上的相邻节点之一，用来给阀门做角度
					List<String> devId_list = new ArrayList<>();
					for (ExportCadVO exportCadVO : list) {
						String devId2 = neo4jUtil.getValveNodeByDevId(exportCadVO.getDevId()).getDev_id();
						if (devId2 != null && devId2.length() != 0) {
							devId_list.add(devId2);
						}
					}
					featureVO = gisDevExtPOMapper.findFeaturesListByDevIdList(devId_list);//节点太多，使用集合形式，批量查询对应属性
				}
				for (ExportCadVO exportCadVO:list) {//对于每个数据进行绘制
					String name = exportCadVO.getName();
					String type = exportCadVO.getType();
					String geom = exportCadVO.getGeom();
					if(type.equals("POINT")){
						String aa = geom.substring(6,geom.length()-1);
						Double x = Double.parseDouble(aa.split(" ")[0]);
						Double y = Double.parseDouble(aa.split(" ")[1]);
						double R = 0.3;
						double d = R/(Math.sqrt(2));
						if(id.getId()==19){//阀门
							String devId = exportCadVO.getDevId();
							String devId2 = neo4jUtil.getValveNodeByDevId(devId).getDev_id();
							double sin;
							double cos;
							double d1=2/(Math.sqrt(2));
							double d2=-2/(Math.sqrt(2));
							for (FeatureVO fea:featureVO){
								//从相邻节点的信息集featureVO，中匹配当前节点对应的相邻节点的信息
								if(devId2!=null && devId2.length()!=0 && devId2.equals(fea.getDevId())){
									String geom1= fea.getGeom();
									String aa1 = geom1.substring(6,geom1.length()-1);
									Double x1 = Double.parseDouble(aa1.split(" ")[0]);
									Double y1 = Double.parseDouble(aa1.split(" ")[1]);
									sin = (y1-y)/(Math.sqrt((x1-x)*(x1-x)+(y1-y)*(y1-y)));
									cos = (x1-x)/(Math.sqrt((x1-x)*(x1-x)+(y1-y)*(y1-y)));
									d1 = (sin+cos)*(2/(Math.sqrt(2)));
									d2 = (sin-cos)*(2/(Math.sqrt(2)));
								}
							}
							double r=R/2;
							Vertex v1 = new Vertex(x-(r*d1), y-(r*d2), "Vertex");
							Vertex v2 = new Vertex(x+(r*d2), y-(r*d1), "Vertex");
							Vertex v3 = new Vertex(x, y, "Vertex");
							Vertex v4 = new Vertex(x+(r*d1), y+(r*d2), "Vertex");
							Vertex v5 = new Vertex(x-(r*d2), y+(r*d1), "Vertex");
							Vertex v6 = new Vertex(x, y, "Vertex");
							PolyLine polyLine = new PolyLine("FaMen",(short)3);
							polyLine.AddVertex(v1);
							polyLine.AddVertex(v2);
							polyLine.AddVertex(v3);
							polyLine.AddVertex(v4);
							polyLine.AddVertex(v5);
							polyLine.AddVertex(v6);
							doc.add(polyLine);
						}
						if(id.getId()==26){//管件
							Vertex v1 = new Vertex(x-d, y-d, "Vertex");
							Vertex v2 = new Vertex(x+d, y-d, "Vertex");
							Vertex v3 = new Vertex(x+d, y, "Vertex");
							Vertex v4 = new Vertex(x+0.5*d, y, "Vertex");
							Vertex v5 = new Vertex(x+0.5*d, y+d, "Vertex");
							Vertex v6 = new Vertex(x-0.5*d, y+d, "Vertex");
							Vertex v7 = new Vertex(x-0.5*d, y, "Vertex");
							Vertex v8 = new Vertex(x-d, y, "Vertex");
							PolyLine polyLine = new PolyLine("GuanJian",(short)3);
							polyLine.AddVertex(v1);
							polyLine.AddVertex(v2);
							polyLine.AddVertex(v3);
							polyLine.AddVertex(v4);
							polyLine.AddVertex(v5);
							polyLine.AddVertex(v6);
							polyLine.AddVertex(v7);
							polyLine.AddVertex(v8);
							doc.add(polyLine);
						}
						if(id.getId()==18){//消防栓
							Vertex v1 = new Vertex(x-0.5*d, y, "Vertex");
							Vertex v2 = new Vertex(x-0.5*d, y-d, "Vertex");
							Vertex v3 = new Vertex(x+0.5*d, y-d, "Vertex");
							Vertex v4 = new Vertex(x+0.5*d, y, "Vertex");
							Vertex v5 = new Vertex(x+d, y, "Vertex");
							Vertex v6 = new Vertex(x, y+d, "Vertex");
							Vertex v7 = new Vertex(x-d, y, "Vertex");
							PolyLine polyLine = new PolyLine("XiaoFang",(short)3);
							polyLine.AddVertex(v1);
							polyLine.AddVertex(v2);
							polyLine.AddVertex(v3);
							polyLine.AddVertex(v4);
							polyLine.AddVertex(v5);
							polyLine.AddVertex(v6);
							polyLine.AddVertex(v7);
							doc.add(polyLine);
						}
						if(id.getId()==1){//其它
							Vertex v1 = new Vertex(x-d, y-d, "Vertex");
							Vertex v2 = new Vertex(x+d, y-d, "Vertex");
							Vertex v3 = new Vertex(x+d, y+d, "Vertex");
							Vertex v4 = new Vertex(x-d, y+d, "Vertex");
							PolyLine polyLine = new PolyLine("Other",(short)3);
							polyLine.AddVertex(v1);
							polyLine.AddVertex(v2);
							polyLine.AddVertex(v3);
							polyLine.AddVertex(v4);
							doc.add(polyLine);
						}
						Circle cc = new Circle(x,y, R, "pointTest");
						doc.add(cc);
					}
					if(type.equals("LINESTRING")){
						String aa = geom.substring(11,geom.length()-1);
						Double xi = Double.parseDouble(aa.split(",")[0].split(" ")[0]);
						Double yi = Double.parseDouble(aa.split(",")[0].split(" ")[1]);
						Double xf = Double.parseDouble(aa.split(",")[1].split(" ")[0]);
						Double yf = Double.parseDouble(aa.split(",")[1].split(" ")[1]);
						Line line = new Line("Line",xi, yi,xf ,yf);
						if(name.equals(GISConstants.CALIBER_600)){
							line.setLwidth(6+"");//紫色
						}
						else if(name.equals(GISConstants.CALIBER_400)){
							line.setLwidth(4+"");//浅蓝色
						}
						else if(name.equals(GISConstants.CALIBER_200)){
							line.setLwidth(3+"");//绿色
						}
						else if(name.equals(GISConstants.CALIBER_100)){
							line.setLwidth(5+"");//深蓝
						}
						else if(name.equals(GISConstants.CALIBER_0)){
							line.setLwidth(40+"");//橘色
						}
						else if(name.equals(GISConstants.CALIBER_900)){
							line.setLwidth(1+"");//红色
						}
						doc.add(line);
					}
				}
			}
			String filePath = pathConfig.getDownloadPath() + "/Export.dxf";
			FileOutputStream f1 = new FileOutputStream(new File(filePath));
			Writer.Write(doc, f1);
			f1.close();
			String result = JavaFileToFormUpload.send(pathConfig.getUploadFileUrl(), filePath);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("导出CAD数据失败！");
		}
	}

	/**
	 * 获取管网长度
	 * @return
	 * @throws BizException
	 */
	public Map<String, String> getPipeLengthByDeptPath() throws BizException{
		Map<String,String>map = new HashMap<>();
		List<PipeLengthVO> list =null;
		try {
			boolean dataPermission = switchConfig.getPermission();
			//判断系统是否有权限
			if (dataPermission){
				list = gisDevExtPOMapper.getPipeLengthByAuthId();
				List<DictDetailPO> detailPOS1 = detailService.findDetailsByTypeVal(dictConfig.getAuthId());
				for(PipeLengthVO vo:list){
					for (DictDetailPO po: detailPOS1){
						if (po.getVal().equals(String.valueOf(vo.getAuthId()))){
							map.put(po.getName(),String.valueOf(vo.getLength()));
						}
					}
				}
			}else {
				list = gisDevExtPOMapper.getPipeLengthByAll();
				List<DictDetailPO> detailPOS1 = detailService.findDetailsByTypeVal(dictConfig.getAuthId());
				map.put(detailPOS1.get(0).getName(),list.get(0).getLength().toString());
			}

		}catch (Exception e){
			e.printStackTrace();
			Logger.error(e.getMessage());
			throw new BizException("获取管网长度失败");
		}
		return map;
	}

	/**
	 * 保存书签
	 * @param dto
	 * @return
	 */
	public Integer saveBookmark(BookMarkDTO dto, String deptPath, Long userId, String token) throws BizException{
		try {
			SysOcpUserPo sysOcpUserPo = userRpc.getUserById(userId, token);
			String loginUserName = sysOcpUserPo.getName();
			Long deptId = ocpService.setDeptPath(deptPath).getUserWaterworksDeptId();
			dto.setBelongTo(deptId);
			dto.setCreatBy(loginUserName);
			if(Objects.isNull(dto.getUrl())){
				throw new BizException("未上传书签图片!");
			}
			String imgStr = dto.getUrl().substring(dto.getUrl().indexOf("base64,")+7);
			Long mill = System.currentTimeMillis();
			String contentPath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
			String path = pathConfig.getStaticPath()+File.separator+mill.toString()+".png";
			dto.setUrl(mill.toString()+".png");
			Boolean flag = FileUtil.GenerateImage(imgStr,contentPath+File.separator+path);
			if(flag){
                return bookMarkMapper.insertBookMark(dto);
            }else{
				throw new Exception("保存书签失败!");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("保存书签失败！", e.getMessage());
			throw new BizException(e.getMessage());
		}
	}

	/**
	 * 删除书签
	 * @param
	 * @return
	 */
	public Integer deleteBookmarkById(Long id,  Long userId, String token) throws BizException{
		try {
			SysOcpUserPo sysOcpUserPo = userRpc.getUserById(userId, token);
			String loginUserName = sysOcpUserPo.getName();
			Date date = new Date();
			return bookMarkMapper.deleteBookMarkById(id,loginUserName,date);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("删除书签失败！", e.getMessage());
			throw new BizException(e.getMessage());
		}
	}

	/**
	 * 获得书签列表信息
	 * @param
	 * @return
	 */
	public PageVO<BookMarkPO> findBookMarkList(PageDTO dto) throws BizException {
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy());
		Page<BookMarkPO> list = (Page<BookMarkPO>) bookMarkMapper.findBookMarkList();
		return new PageVO<BookMarkPO>(list);
	}

}