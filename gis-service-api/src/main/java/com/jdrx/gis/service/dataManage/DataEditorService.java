package com.jdrx.gis.service.dataManage;

import com.alibaba.fastjson.JSONObject;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.dataManage.*;
import com.jdrx.gis.beans.entity.basic.*;
import com.jdrx.gis.beans.vo.basic.FeatureVO;
import com.jdrx.gis.beans.vo.basic.PointVO;
import com.jdrx.gis.beans.vo.datamanage.LineXYVo;
import com.jdrx.gis.beans.vo.query.FieldNameVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.GisDevTplAttrPOMapper;
import com.jdrx.gis.dao.basic.ShareDevPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.dao.query.DevQueryDAO;
import com.jdrx.gis.filter.assist.OcpService;
import com.jdrx.gis.service.analysis.NetsAnalysisService;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.gis.service.basic.GISDeviceService;
import com.jdrx.gis.util.Neo4jUtil;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.share.service.SequenceDefineService;
import org.postgresql.util.PGobject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/6 0006 下午 1:25
 */
@Service
public class DataEditorService {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DataEditorService.class);

    @Autowired
    private DictConfig dictConfig;

    @Autowired
    private DictDetailService detailService;

    @Autowired
    ShareDevTypePOMapper shareDevTypePOMapper;

    @Autowired
    DevQueryDAO devQueryDAO;

    @Autowired
    SequenceDefineService sequenceDefineService;

    @Autowired
    GISDeviceService gisDeviceService;

    @Autowired
    GISDevExtPOMapper gisDevExtPOMapper;

    @Autowired
    ShareDevPOMapper shareDevPOMapper;

    @Autowired
    NetsAnalysisService netsAnalysisService;
    @Autowired
    Neo4jUtil neo4jUtil;

    @Autowired
    GisDevTplAttrPOMapper gisDevTplAttrPOMapper;


    /**
     * 获取所有点类型
     * @return
     */
    public List<ShareDevTypePO> getAllPointType(){
        List<ShareDevTypePO> shareDevTypePOS = new ArrayList<>();
        try {
            String layerUrl = dictConfig.getPointType();
            List<DictDetailPO> detailPOs = detailService.findDetailsByTypeVal(layerUrl);
            if (detailPOs != null){
                String stringIds = detailPOs.get(0).getVal();
                shareDevTypePOS = shareDevTypePOMapper.findPointTypeByIds(stringIds);
            }

        }catch (Exception e){
            Logger.error(e.getMessage());
        }
        return shareDevTypePOS;
    }

    /**
     * 获取所有管线类型
     * @return
     */
    public List<ShareDevTypePO> getAllLineType(){
        List<ShareDevTypePO> shareDevTypePOS = new ArrayList<>();
        try {
            String layerUrl = dictConfig.getLineType();
            List<DictDetailPO> detailPOs = detailService.findDetailsByTypeVal(layerUrl);
            if (detailPOs != null){
                String stringIds = detailPOs.get(0).getVal();
                shareDevTypePOS = shareDevTypePOMapper.findLineTypeByIds(stringIds);
            }

        }catch (Exception e){
            Logger.error(e.getMessage());
        }
        return shareDevTypePOS;
    }

    /**
     * 保存管点及新增管线信息（线上加点）
     * @param dto
     */
    public Boolean saveAddedSharePoint(ShareAddedPointDTO dto,String deptPath) throws BizException{
        try {
            if(!savaSharePointOnLine(dto,deptPath)){
                throw new BizException("保存管点信息失败！");
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通过设备类型id查詢最顶端父id，获取设备属性模板
     * @param typeId
     * @return
     */
    public List<FieldNameVO> getDevExtByTopPid(Long typeId) throws BizException{
        try {
            List<Long> typeIdList = getAllLineTypeIds();
            Boolean bl = typeIdList.contains(typeId);
            List<FieldNameVO> fieldNameVOS =  devQueryDAO.findFieldNamesByDevTypeId(typeId);
            FieldNameVO fieldNameVO = new FieldNameVO();
            Iterator iterator = fieldNameVOS.iterator();
            while (iterator.hasNext()){
                fieldNameVO = (FieldNameVO) iterator.next();
                if(fieldNameVO.getFieldName().equals(GISConstants.GIS_ATTR_DEVID) || fieldNameVO.getFieldName().equals("geom")){
                    iterator.remove();
                }
                //去除管线name字段
                if(bl && fieldNameVO.getFieldName().equals(GISConstants.GIS_ATTR_NAME)){
                    iterator.remove();
                }
            }
            return fieldNameVOS;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BizException(e);
        }

    }

    /**
     * 线上加点保存点信息到gis_dev_ext及share_dev
     * @param dto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean savaSharePointOnLine(ShareAddedPointDTO dto,String deptPath){
        try {
            Long deptId = new OcpService().setDeptPath(deptPath).getUserWaterworksDeptId();
            Map<String,Object> map = dto.getMap();
            Long seq = sequenceDefineService.increment(gisDeviceService.sequenceKey());
            String devId = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seq);

            //对于不在线上的点，计算其到最近线的垂足的点，来代替该点
            LineXYVo lineXYVo = gisDevExtPOMapper.getXYByDevId(dto.getLineDevId());
            Double x1 = lineXYVo.getX1();
            Double y1 = lineXYVo.getY1();
            Double x2 = lineXYVo.getX2();
            Double y2 = lineXYVo.getY2();
            double k1 = (y2-y1)/(x2-x1);
            double k2 = (-1)/k1;
            String geom1 = "POINT("+dto.getX()+" "+dto.getY()+")";
            String srid = netsAnalysisService.getValByDictString(dictConfig.getWaterPipeSrid());
            String transformGeom1 = gisDevExtPOMapper.transformWgs84ToCustom(geom1,Integer.parseInt(srid));
            PointVO transPointVo1 = gisDevExtPOMapper.getPointXYFromGeom(transformGeom1);
            Double x0 = transPointVo1.getX();
            Double y0 = transPointVo1.getY();
            double x = (y0-y2+k1*x2-k2*x0)/(k1-k2);
            double y = k1*x+y2-k1*x2;
            dto.setX(x);
            dto.setY(y);
            String geom = "POINT("+dto.getX()+" "+dto.getY()+")";
            String transformGeom = gisDevExtPOMapper.addGeomWithSrid(geom,Integer.parseInt(srid));
            PointVO transPointVo = gisDevExtPOMapper.getPointXYFromGeom(transformGeom);
            map.put("x",x);
            map.put("y",y);
            map.put(GISConstants.GIS_ATTR_DEVID,devId);

            List<GisDevTplAttrPO> list = gisDevTplAttrPOMapper.selectNameByTqlId(1);//获取管点的全部字段英文名称
            for (GisDevTplAttrPO gis:list) {
                String name = gis.getFieldName();
                //如果map集合的key中没有某些管点属性英文字段，则对其增加key并赋值为""
                if(!map.containsKey(name)){
                    map.put(name,"");
                }
            }
            //增加data_info属性信息的belong_to字段
            if (!map.containsKey("belong_to")) {
                String depId1 = String.valueOf(deptId);
                String belongTo = "";
                List<DictDetailPO> dictDetailPOList = detailService.findDetailsByTypeVal(dictConfig.getAuthId());
                for (DictDetailPO po : dictDetailPOList) {
                    if (po.getVal().equals(depId1)) {
                        belongTo = po.getName();
                        map.put("belong_to", belongTo);
                    }
                }
            }

            String jsonStr = JSONObject.toJSONString(map);
            PGobject jsonObject = new PGobject();
            jsonObject.setValue(jsonStr);
            jsonObject.setType("jsonb");

            GISDevExtPO po = new GISDevExtPO();
            po.setDevId(devId);
            po.setCode(map.get(GISConstants.GIS_ATTR_CODE).toString());
            po.setName(map.get(GISConstants.GIS_ATTR_NAME).toString());
            po.setGeom(transformGeom);
            po.setDataInfo(jsonObject);
            po.setTplTypeId(dto.getTypeId());
            po.setBelongTo(deptId);

            ShareDevPO shareDevPO = new ShareDevPO();
            shareDevPO.setId(devId);
            shareDevPO.setName(map.get(GISConstants.GIS_ATTR_NAME).toString());
            shareDevPO.setTypeId(dto.getTypeId());
            shareDevPO.setLng(String.format("%.3f",transPointVo.getX()));
            shareDevPO.setLat(String.format("%.3f",transPointVo.getY()));
            if (map.containsKey(GISConstants.GIS_ATTR_ADDR)){
                shareDevPO.setAddr(map.get(GISConstants.GIS_ATTR_ADDR).toString());
            }

            //保存管点信息
            gisDevExtPOMapper.insertSelective(po);
            shareDevPOMapper.insertSelective(shareDevPO);

            //获取管线信息
            GISDevExtPO gisDevExtPOLine = gisDevExtPOMapper.getDevExtByDevId(dto.getLineDevId());
            String geomLine =  gisDevExtPOLine.getGeom();
            String geomText = gisDevExtPOMapper.transformGeomAsText(geomLine);

            PointVO pointVO = gisDevExtPOMapper.getPointXYFromGeom(transformGeom);
            String newLineGeomStr1 = geomText.substring(0,geomText.indexOf(',')+1) + pointVO.getX().toString()+" "+pointVO.getY().toString()+")";
            String newLineGeomStr2 = "LINESTRING("+pointVO.getX() + " " +pointVO.getY() +geomText.substring(geomText.indexOf(','));

            String lineGeom1 = gisDevExtPOMapper.addGeomWithSrid(newLineGeomStr1,Integer.parseInt(srid));
            String lineGeom2 = gisDevExtPOMapper.addGeomWithSrid(newLineGeomStr2,Integer.parseInt(srid));
            Double pipeLength1 = gisDevExtPOMapper.getLengthByGeomStr(lineGeom1);//管网长度1
            Double pipeLength2 = gisDevExtPOMapper.getLengthByGeomStr(lineGeom2);//管网长度2

            //构造新的管线GISDevExtPO对象
            GISDevExtPO gisDevExtPOLine1 = gisDevExtPOMapper.getDevExtByDevId(dto.getLineDevId());
            GISDevExtPO gisDevExtPOLine2 = gisDevExtPOMapper.getDevExtByDevId(dto.getLineDevId());

            Long seqLine1 = sequenceDefineService.increment(gisDeviceService.sequenceKey());
            String devIdLine1 = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seqLine1);
            gisDevExtPOLine1.setDevId(devIdLine1);
            Long seqLine2 = sequenceDefineService.increment(gisDeviceService.sequenceKey());
            String devIdLine2 = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seqLine2);
            gisDevExtPOLine2.setDevId(devIdLine2);

            //更新data_info里面的值
            JSONObject jb1 = JSONObject.parseObject(gisDevExtPOLine1.getDataInfo().toString());
            Map<String,Object> map1 = (Map)jb1;
            String lineCode1 = null;
            if (map1.containsKey(GISConstants.GIS_ATTR_QDBM)){
                lineCode1 = map1.get(GISConstants.GIS_ATTR_QDBM).toString()+"-"+po.getCode();
                map1.replace(GISConstants.GIS_ATTR_ZDBM,map1.get(GISConstants.GIS_ATTR_ZDBM),po.getCode());
                if (map1.containsKey(GISConstants.GIS_ATTR_CODE)){
                    map1.replace(GISConstants.GIS_ATTR_CODE,map1.get(GISConstants.GIS_ATTR_CODE),lineCode1);
                }
            }
            map1.replace(GISConstants.GIS_ATTR_DEVID,map1.get(GISConstants.GIS_ATTR_DEVID),devIdLine1);
            map1.replace(GISConstants.GIS_ATTR_PIPE_LENGTH,pipeLength1);//更新管网长度1
            String jsonStr1 = JSONObject.toJSONString(map1);
            PGobject jsonObject1 = new PGobject();
            jsonObject1.setValue(jsonStr1);
            jsonObject1.setType("jsonb");

            JSONObject jb2 = JSONObject.parseObject(gisDevExtPOLine2.getDataInfo().toString());
            Map<String,Object> map2 = (Map)jb2;
            String lineCode2 = null;
            if (map2.containsKey(GISConstants.GIS_ATTR_ZDBM)){
                lineCode2 = po.getCode()+"-"+map2.get(GISConstants.GIS_ATTR_ZDBM).toString();
                map2.replace(GISConstants.GIS_ATTR_QDBM,map2.get(GISConstants.GIS_ATTR_QDBM),po.getCode());
                if (map2.containsKey(GISConstants.GIS_ATTR_CODE)){
                    map2.replace(GISConstants.GIS_ATTR_CODE,map2.get(GISConstants.GIS_ATTR_CODE),lineCode2);
                }
            }
            map2.replace(GISConstants.GIS_ATTR_DEVID,map2.get(GISConstants.GIS_ATTR_DEVID),devIdLine2);
            map2.replace(GISConstants.GIS_ATTR_PIPE_LENGTH,pipeLength2);//更新管网长度2
            String jsonStr2 = JSONObject.toJSONString(map2);
            PGobject jsonObject2 = new PGobject();
            jsonObject2.setValue(jsonStr2);
            jsonObject2.setType("jsonb");


            gisDevExtPOLine1.setGeom(lineGeom1);
            gisDevExtPOLine1.setDataInfo(jsonObject1);
            if (lineCode1 != null){
                gisDevExtPOLine1.setCode(lineCode1);
            }
            if (lineCode2 != null){
                gisDevExtPOLine2.setCode(lineCode2);
            }
            gisDevExtPOLine2.setGeom(lineGeom2);
            gisDevExtPOLine2.setDataInfo(jsonObject2);
            gisDevExtPOLine2.setId(null);
            gisDevExtPOLine1.setId(null);
            gisDevExtPOLine1.setBelongTo(deptId);
            gisDevExtPOLine2.setBelongTo(deptId);

            //构造新的管线--share_dev
            ShareDevPO shareDevPOLine = shareDevPOMapper.selectByPrimaryKey(dto.getLineDevId());
            ShareDevPO shareDevPOLine1 = shareDevPOMapper.selectByPrimaryKey(dto.getLineDevId());
            ShareDevPO shareDevPOLine2 = shareDevPOMapper.selectByPrimaryKey(dto.getLineDevId());

            shareDevPOLine1.setId(gisDevExtPOLine1.getDevId());
            shareDevPOLine1.setTypeId(shareDevPOLine.getTypeId());
            shareDevPOLine2.setId(gisDevExtPOLine2.getDevId());
            shareDevPOLine2.setTypeId(shareDevPOLine.getTypeId());

            //先删除gis_dev_ext原来管线
            gisDevExtPOMapper.deleteDevExtByDevId(dto.getLineDevId());

            //增加的两条管线
            gisDevExtPOMapper.insertSelective(gisDevExtPOLine1);
            gisDevExtPOMapper.insertSelective(gisDevExtPOLine2);

            //先删除share_dev原来管线
            shareDevPOMapper.deleteByPrimaryKey(dto.getLineDevId());

            shareDevPOMapper.insertSelective(shareDevPOLine1);
            shareDevPOMapper.insertSelective(shareDevPOLine2);
            String code = gisDevExtPOLine.getCode();
            if(neo4jUtil.saveToNeo4j(dto,devId,code.substring(0,code.indexOf("-")),devIdLine1,lineCode1,
                    code.substring(code.indexOf("-")+1),devIdLine2,lineCode2,deptId )){
                return true;
            }else{
                Logger.error("neo4j数据库线上加点保存失败");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存添加管网（管点和管线）
     * @param dto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveShareNets(ShareAddedNetsDTO dto,String deptPath) throws BizException{
        try {
            if((!saveSharePoint(dto.getPointList(),deptPath)) ||(!savaShareLine(dto.getLineList(),deptPath))){
                return false;
            }else {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new  BizException("保存管线失败！");
        }
    }
    /**
     * 保存管点
     * @param list
     * @return
     */
    public Boolean saveSharePoint(List<SharePointDTO> list,String deptPath) throws BizException{
        if (list == null||list.size() == 0 ){
            return false;
        }
        try {
            Long deptId = new OcpService().setDeptPath(deptPath).getUserWaterworksDeptId();
            for(SharePointDTO dto:list){
                Long seq = sequenceDefineService.increment(gisDeviceService.sequenceKey());
                String devId = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seq);

                String geom = "POINT("+dto.getX()+" "+dto.getY()+")";
                String srid = netsAnalysisService.getValByDictString(dictConfig.getWaterPipeSrid());
                String transformGeom = gisDevExtPOMapper.transformWgs84ToCustom(geom,Integer.parseInt(srid));
                PointVO pointVO = gisDevExtPOMapper.getPointXYFromGeom(transformGeom);

                Map<String,Object> map = dto.getMapAttr();
                //将前端的4326坐标系的值改为4544的值修改到datainfo中
                map.replace("x",String.format("%.3f",pointVO.getX()));
                map.replace("y",String.format("%.3f",pointVO.getY()));
                map.put(GISConstants.GIS_ATTR_DEVID,devId);
                List<GisDevTplAttrPO> list1 = gisDevTplAttrPOMapper.selectNameByTqlId(1);//获取管点的全部字段英文名称
                for (GisDevTplAttrPO gis:list1) {
                    String name = gis.getFieldName();
                    //如果map集合的key中没有某些管点属性英文字段，则对其增加key并赋值为""
                    if(!map.containsKey(name)){
                        map.put(name,"");
                    }
                }
                //增加data_info属性信息的belong_to字段
                if (!map.containsKey("belong_to")) {
                    String depId1 = String.valueOf(deptId);
                    String belongTo = "";
                    List<DictDetailPO> dictDetailPOList = detailService.findDetailsByTypeVal(dictConfig.getAuthId());
                    for (DictDetailPO po : dictDetailPOList) {
                        if (po.getVal().equals(depId1)) {
                            belongTo = po.getName();
                            map.put("belong_to", belongTo);
                        }
                    }
                }
                String jsonStr = JSONObject.toJSONString(map);
                PGobject jsonObject = new PGobject();
                jsonObject.setValue(jsonStr);
                jsonObject.setType("jsonb");

                GISDevExtPO po = new GISDevExtPO();
                po.setDevId(devId);
                po.setCode(dto.getMapAttr().get(GISConstants.GIS_ATTR_CODE).toString());
                po.setName(dto.getMapAttr().get(GISConstants.GIS_ATTR_NAME).toString());
                po.setGeom(transformGeom);
                po.setDataInfo(jsonObject);
                po.setTplTypeId(dto.getTypeId());
                po.setBelongTo(deptId);

                ShareDevPO shareDevPO = new ShareDevPO();
                shareDevPO.setId(po.getDevId());
                shareDevPO.setName(po.getName());
                shareDevPO.setTypeId(po.getTplTypeId());
                shareDevPO.setLng(String.format("%.3f",pointVO.getX()));
                shareDevPO.setLat(String.format("%.3f",pointVO.getY()));
                if(dto.getMapAttr().containsKey(GISConstants.GIS_ATTR_ADDR)){
                    shareDevPO.setAddr(dto.getMapAttr().get(GISConstants.GIS_ATTR_ADDR).toString());
                }

                gisDevExtPOMapper.insertSelective(po);
                shareDevPOMapper.insertSelective(shareDevPO);
                if(!neo4jUtil.savePointToNeo4j(dto,devId,deptId)){
                    Logger.error("添加管网保存管点失败"+list.toString());
                     return false;
                }

            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("新增管网保存管点失败！"+list.toString());
            throw new BizException("新增管网保存管点失败！");
        }
    }

    /**
     * 保存管线
     * @param list
     * @return
     */
    public Boolean savaShareLine(List<ShareLineDTO> list,String deptPath) throws BizException{
        if (list == null||list.size() == 0 ){
            return false;
        }
        try {
            Long deptId = new OcpService().setDeptPath(deptPath).getUserWaterworksDeptId();
            for (ShareLineDTO dto:list){
                Long seq = sequenceDefineService.increment(gisDeviceService.sequenceKey());
                String devId = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seq);

                GISDevExtPO startPointPO = gisDevExtPOMapper.selectByCode(dto.getStartCode());
                GISDevExtPO endPointPO = gisDevExtPOMapper.selectByCode(dto.getEndCode());

                PointVO startVO = gisDevExtPOMapper.getPointXYFromGeom(startPointPO.getGeom());
                PointVO endVO = gisDevExtPOMapper.getPointXYFromGeom(endPointPO.getGeom());

                String srid = netsAnalysisService.getValByDictString(dictConfig.getWaterPipeSrid());

                String lineGeomStr = "LINESTRING("+startVO.getX()+" "+startVO.getY()+","+endVO.getX()+" "+endVO.getY()+")";
                String transformGeomStr = gisDevExtPOMapper.addGeomWithSrid(lineGeomStr,Integer.parseInt(srid));
                Double pipe_length = gisDevExtPOMapper.getLengthByGeomStr(transformGeomStr);

                Map<String,Object> map = dto.getMapAttr();
                map.put(GISConstants.GIS_ATTR_DEVID,devId);
                map.put(GISConstants.GIS_ATTR_PIPE_LENGTH,pipe_length);
                List<GisDevTplAttrPO> list1 = gisDevTplAttrPOMapper.selectNameByTqlId(2);//获取管线的全部字段英文名称
                for (GisDevTplAttrPO gis:list1) {
                    String name = gis.getFieldName();
                    //如果map集合的key中没有某些管点属性英文字段，则对其增加key并赋值为""
                    if(!map.containsKey(name)){
                        map.put(name,"");
                    }
                }
                //增加data_info属性信息的belong_to字段
                if (!map.containsKey("belong_to")) {
                    String depId1 = String.valueOf(deptId);
                    String belongTo = "";
                    List<DictDetailPO> dictDetailPOList = detailService.findDetailsByTypeVal(dictConfig.getAuthId());
                    for (DictDetailPO po : dictDetailPOList) {
                        if (po.getVal().equals(depId1)) {
                            belongTo = po.getName();
                            map.put("belong_to", belongTo);
                        }
                    }
                }
                String jsonStr = JSONObject.toJSONString(map);
                PGobject jsonObject = new PGobject();
                jsonObject.setValue(jsonStr);
                jsonObject.setType("jsonb");

                GISDevExtPO gisDevExtPO = new GISDevExtPO();
                gisDevExtPO.setDevId(devId);
                gisDevExtPO.setCode(dto.getStartCode()+"-"+dto.getEndCode());
                gisDevExtPO.setCaliber(dto.getCaliber());
                gisDevExtPO.setMaterial(dto.getMaterial());
                gisDevExtPO.setTplTypeId(dto.getTypeId());
                gisDevExtPO.setDataInfo(jsonObject);
                gisDevExtPO.setGeom(transformGeomStr);
                gisDevExtPO.setBelongTo(deptId);
                gisDevExtPO.setName(getNameByCaliber(dto.getCaliber()));

                ShareDevPO shareDevPO = new ShareDevPO();
                shareDevPO.setId(gisDevExtPO.getDevId());
                shareDevPO.setTypeId(gisDevExtPO.getTplTypeId());
                shareDevPO.setName(getNameByCaliber(gisDevExtPO.getCaliber()));

                //保存管线
                gisDevExtPOMapper.insertSelective(gisDevExtPO);
                shareDevPOMapper.insertSelective(shareDevPO);
                if(!neo4jUtil.saveLineToNeo4j(dto,dto.getStartCode()+"-"+dto.getEndCode(),dto.getStartCode(),dto.getEndCode(),devId,deptId)){
                    Logger.error("添加管网保存管线失败"+list.toString());
                    return false;
                }

            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("新增管网保存管线失败！"+list.toString());
            throw new BizException("保存管线失败！");
        }
    }

    /**
     * 根据设备编码查询模板属性
     * @param code
     * @return
     */
    public List<FieldNameVO> getAttrByCode(String code) throws BizException{
        try {
            GISDevExtPO gisDevExtPO = gisDevExtPOMapper.selectByCode(code);
            List<FieldNameVO> fieldNameVOS =  getDevExtByTopPid(gisDevExtPO.getTplTypeId());
            if(fieldNameVOS.size() == 0 || fieldNameVOS == null){
                throw new BizException("没有查询到设备信息！");
            }
            return fieldNameVOS;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("根据设备编码查询模板属性失败"+code);
            throw new BizException("根据设备编码查询模板属性失败!");
        }
    }
    /**
     * 更新设备属性信息
     * @param map
     * @return
     * @throws BizException
     */
    public Boolean updateGISDevExtAttr(Map<String,Object> map) throws BizException{
        try {
            String code = null;
            if(!map.containsKey(GISConstants.GIS_ATTR_CODE)){
                code = map.get(GISConstants.GIS_ATTR_QDBM).toString()+"-"+map.get(GISConstants.GIS_ATTR_ZDBM).toString();
            }else {
                code = map.get(GISConstants.GIS_ATTR_CODE).toString();
            }
            GISDevExtPO gisDevExtPO = gisDevExtPOMapper.selectByCode(code);
            if(gisDevExtPO == null){
                Logger.error("查找无设备信息");
                throw new BizException("查询设备失败！");
            }
            ShareDevPO shareDevPO = shareDevPOMapper.selectByPrimaryKey(gisDevExtPO.getDevId());

            List<DictDetailPO> dictDetailPOS = detailService.findDetailsByTypeVal(dictConfig.getLineType());
            List<Long> list = shareDevTypePOMapper.getAllTypeIdByTopId(Long.valueOf(dictDetailPOS.get(0).getVal()));
            //判断是否是线类型
            if(list.contains(gisDevExtPO.getTplTypeId())){
                if (!(Integer.parseInt(map.get(GISConstants.GIS_ATTR_CALIBER).toString()) == gisDevExtPO.getCaliber())){
                    gisDevExtPO.setCaliber(Integer.parseInt(map.get(GISConstants.GIS_ATTR_CALIBER).toString()));
                    String name = getNameByCaliber(Integer.parseInt(map.get(GISConstants.GIS_ATTR_CALIBER).toString()));
                    gisDevExtPO.setName(name);
                    shareDevPO.setName(name);
                }
                if (!map.get(GISConstants.GIS_ATTR_MATERIAL).toString().equals(gisDevExtPO.getMaterial())){
                    gisDevExtPO.setMaterial(map.get(GISConstants.GIS_ATTR_MATERIAL).toString());
                }
                //判断类型是否改变
                if(!gisDevExtPO.getTplTypeId().equals(Long.valueOf(map.get("typeId").toString()))){
                    gisDevExtPO.setTplTypeId(Long.valueOf(map.get("typeId").toString()));
                    shareDevPO.setTypeId(Long.valueOf(map.get("typeId").toString()));
                }
                //判断街道是否改变
                if(map.containsKey(GISConstants.GIS_ATTR_ADDR)){
                    shareDevPO.setAddr(map.get(GISConstants.GIS_ATTR_ADDR).toString());
                }
            }else {
                //判断类型是否改变
                if(!gisDevExtPO.getTplTypeId().equals(Long.valueOf(map.get("typeId").toString()))){
                    gisDevExtPO.setName(map.get(GISConstants.GIS_ATTR_NAME).toString());
                    gisDevExtPO.setTplTypeId(Long.valueOf(map.get("typeId").toString()));
                    shareDevPO.setTypeId(Long.valueOf(map.get("typeId").toString()));
                }

            }
            String jsonStr = JSONObject.toJSONString(map);
            PGobject jsonObject = new PGobject();
            jsonObject.setValue(jsonStr);
            jsonObject.setType("jsonb");
            gisDevExtPO.setDataInfo(jsonObject);

            gisDevExtPOMapper.updateByPrimaryKeySelective(gisDevExtPO);
            if(!map.containsKey(GISConstants.GIS_ATTR_CODE)){
                if(!neo4jUtil.updateLineToNeo4j(code,map)){
                    Logger.error("neo4j更新属性信息失败！"+map.toString());
                    return false;
                }
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("更新设备属性信息失败！"+map.toString());
            throw new BizException("更新设备属性信息失败！");
        }
    }

    /**
     * 判断管点编号是否重复
     * @param code
     * @return
     */
    public Boolean getCodeExist(String code){
        GISDevExtPO po = gisDevExtPOMapper.selectByCode(code);
        return po == null? false: true;
    }

    /**
     * 根据编码查询设备详细信息
     * @param code
     * @return
     */
    public GISDevExtPO getGISDevExtByCode(String code) throws BizException{
        try {
            GISDevExtPO po =  gisDevExtPOMapper.selectByCode(code);
            if (po == null){
                throw new BizException("根据编码查询无设备!");
            }
            return po;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("根据编码查询无设备！"+code);
            throw new BizException("根据编码查询无设备！");

        }
    }

    /**
     * 移动管点
     * @param dto
     * @return
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean moveShareDevPoint(MovePointDTO dto)throws BizException{
        try {
            GISDevExtPO gisDevExtPO = gisDevExtPOMapper.getDevExtByDevId(dto.getDevId());
            String geom = "POINT("+dto.getX()+" "+dto.getY()+")";
            String srid = detailService.findDetailsByTypeVal(dictConfig.getWaterPipeSrid()).get(0).getVal();
            String transformGeom = gisDevExtPOMapper.addGeomWithSrid(geom,Integer.parseInt(srid));
            PointVO pointVO = gisDevExtPOMapper.getPointXYFromGeom(transformGeom);

            //设置更新新的管点空间信息
            JSONObject pointJson = JSONObject.parseObject(gisDevExtPO.getDataInfo().toString());
            Map<String,Object> pointMap = (Map)pointJson;
            if (pointMap.containsKey("x")&&pointMap.containsKey("y")){
                pointMap.replace("x",pointVO.getX());
                pointMap.replace("y",pointVO.getY());
            }
            String jsonStr = JSONObject.toJSONString(pointMap);
            PGobject jsonObject = new PGobject();
            jsonObject.setValue(jsonStr);
            jsonObject.setType("jsonb");
            gisDevExtPO.setDataInfo(jsonObject);
            gisDevExtPO.setGeom(transformGeom);
            gisDevExtPOMapper.updateByPrimaryKeySelective(gisDevExtPO);
            //同步到share_dev中
            gisDevExtPOMapper.updateShareDev(String.format("%.3f",dto.getX()),String.format("%.3f",dto.getY()),dto.getDevId());

            //查找相关联的管线
            List<GISDevExtPO> gisDevExtPOLines = gisDevExtPOMapper.selectLineByCode(gisDevExtPO.getCode());
            for(GISDevExtPO po: gisDevExtPOLines ){
                Object datainfo = po.getDataInfo();
                JSONObject jb = JSONObject.parseObject(datainfo.toString());
                Map<String,Object> map = (Map)jb;
                String lineGeom = po.getGeom();
                String lineGeomTmp = null;
                String lineGeomSrid = null;
                Double pipeLength = null;
                if((map.containsKey(GISConstants.GIS_ATTR_QDBM)) && (map.containsKey(GISConstants.GIS_ATTR_QDBM))){
                    if (map.get(GISConstants.GIS_ATTR_QDBM).equals(gisDevExtPO.getCode())){
                        lineGeomTmp = "LINESTRING("+pointVO.getX()+" "+pointVO.getY()+lineGeom.substring(lineGeom.indexOf(","));
                        lineGeomSrid = gisDevExtPOMapper.addGeomWithSrid(lineGeomTmp,Integer.parseInt(srid));
                        po.setGeom(lineGeomSrid);
                        pipeLength= gisDevExtPOMapper.getLengthByGeomStr(lineGeomSrid);//管网长度
                    }
                    if (map.get(GISConstants.GIS_ATTR_ZDBM).equals(gisDevExtPO.getCode())){
                        lineGeomTmp = lineGeom.substring(0,lineGeom.indexOf(",")+1)+pointVO.getX()+" "+pointVO.getY()+")";
                        lineGeomSrid = gisDevExtPOMapper.addGeomWithSrid(lineGeomTmp,Integer.parseInt(srid));
                        po.setGeom(lineGeomSrid);
                         pipeLength = gisDevExtPOMapper.getLengthByGeomStr(lineGeomSrid);//管网长度
                    }
                    //更新新的管网长度
                    JSONObject jb1 = JSONObject.parseObject(po.getDataInfo().toString());
                    Map<String,Object> map1 = (Map)jb1;
                    map1.replace(GISConstants.PIPE_LENGTH,pipeLength);
                    String jsonStr1 = JSONObject.toJSONString(map1);
                    PGobject jsonObject1 = new PGobject();
                    jsonObject1.setValue(jsonStr1);
                    jsonObject1.setType("jsonb");
                    po.setDataInfo(jsonObject1);
                    gisDevExtPOMapper.updateByPrimaryKeySelective(po);
                }
            }
            if(!neo4jUtil.updatePointMoveToNeo4j(dto)){
                Logger.error("neo4j移动管点失败！"+dto.toString());
                return false;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("移动管点失败！"+dto.toString());
            throw new BizException("移动管点失败！");
        }
    }

    /**
     * 两点连接
     * @param dto
     * @return
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean connectPoints(ConnectPointsDTO dto,String deptPath)throws BizException {
        try {
            Long deptId = new OcpService().setDeptPath(deptPath).getUserWaterworksDeptId();
            List<String> devIdStr = dto.getDevIds();
            if(2 == devIdStr.size()){
                GISDevExtPO firstPointPO = gisDevExtPOMapper.getDevExtByDevId(devIdStr.get(0));
                GISDevExtPO secondPointPO = gisDevExtPOMapper.getDevExtByDevId(devIdStr.get(1));

                if (!(firstPointPO == null ||secondPointPO == null)) {
                    PointVO firstPointVO = gisDevExtPOMapper.getPointXYFromGeom(firstPointPO.getGeom());
                    PointVO secondPointVO = gisDevExtPOMapper.getPointXYFromGeom(secondPointPO.getGeom());

                    String srid = detailService.findDetailsByTypeVal(dictConfig.getWaterPipeSrid()).get(0).getVal();
                    String geom = "LINESTRING(" + firstPointVO.getX() + " " + firstPointVO.getY() + "," + secondPointVO.getX() + " " + secondPointVO.getY() + ")";
                    String transformGeom = gisDevExtPOMapper.addGeomWithSrid(geom,Integer.parseInt(srid));
                    Long seq = sequenceDefineService.increment(gisDeviceService.sequenceKey());
                    String devId = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seq);
                    Double pipe_length = gisDevExtPOMapper.getLengthByGeomStr(transformGeom);

                    Map<String,Object> mapAttr = dto.getMapAttr();
                    mapAttr.put(GISConstants.GIS_ATTR_DEVID,devId);
                    mapAttr.put(GISConstants.GIS_ATTR_PIPE_LENGTH,pipe_length);
                    //增加data_info属性信息的belong_to字段
                    if (!mapAttr.containsKey("belong_to")) {
                        String depId1 = String.valueOf(deptId);
                        String belongTo = "";
                        List<DictDetailPO> dictDetailPOList = detailService.findDetailsByTypeVal(dictConfig.getAuthId());
                        for (DictDetailPO po : dictDetailPOList) {
                            if (po.getVal().equals(depId1)) {
                                belongTo = po.getName();
                                mapAttr.put("belong_to", belongTo);
                            }
                        }
                    }

                    String jsonStr = JSONObject.toJSONString(mapAttr);
                    PGobject jsonObject = new PGobject();
                    jsonObject.setValue(jsonStr);
                    jsonObject.setType("jsonb");

                    GISDevExtPO gisDevExtPO = new GISDevExtPO();
                    gisDevExtPO.setDevId(devId);
                    gisDevExtPO.setCode(dto.getStartCode()+"-"+dto.getEndCode());
                    gisDevExtPO.setTplTypeId(dto.getTypeId());
                    gisDevExtPO.setCaliber(dto.getCaliber());
                    gisDevExtPO.setMaterial(dto.getMaterial());
                    gisDevExtPO.setGeom(transformGeom);
                    gisDevExtPO.setDataInfo(jsonObject);
                    gisDevExtPO.setBelongTo(deptId);
                    gisDevExtPO.setName(getNameByCaliber(dto.getCaliber()));

                    ShareDevPO shareDevPO = new ShareDevPO();
                    shareDevPO.setId(devId);
                    shareDevPO.setTypeId(gisDevExtPO.getTplTypeId());
                    shareDevPO.setName(getNameByCaliber(gisDevExtPO.getCaliber()));

                    gisDevExtPOMapper.insertSelective(gisDevExtPO);
                    shareDevPOMapper.insertSelective(shareDevPO);
                      if(!neo4jUtil.createTwoPointsConnectionToNeo4j(dto,dto.getStartCode()+"-"+dto.getEndCode(),devIdStr.get(0),devIdStr.get(1),devId,deptId)){
                          Logger.error("neo4j两点连接失败");
                          return false;
                      }
                }

            }else {
                Logger.error("两点连接点位个数不对,当前点位个数为："+devIdStr.size());
                throw new BizException("两点连接点位个数不对!");
            }


        }catch (Exception e){
            Logger.error("两点连接失败！"+e.getMessage());
            throw new BizException("两点连接失败!");
        }
        return true;
    }

    /**
     * 根据管径获取管线设备名称
     * @param caliber
     * @return
     */
    public String getNameByCaliber(Integer caliber){
        if (caliber >= 0 && caliber < 100){
            return GISConstants.CALIBER_0;
        }else if(caliber >=100 && caliber <200){
            return GISConstants.CALIBER_100;
        }else if(caliber >=200 && caliber <400){
            return GISConstants.CALIBER_200;
        }else if(caliber >=400 && caliber <600){
            return GISConstants.CALIBER_400;
        }else if(caliber >=600 && caliber <900){
            return GISConstants.CALIBER_600;
        }else if(caliber >=900){
            return GISConstants.CALIBER_900;
        }
        return null;
    }

    /**
     * 获取所有管线设备类型id
     * @return
     */
    public List<Long> getAllLineTypeIds(){
        List<Long> list = new ArrayList<>();
        try {
            List<DictDetailPO> detailPOS = detailService.findDetailsByTypeVal(dictConfig.getLineType());
            list = shareDevTypePOMapper.getAllTypeIdByTopId(Long.valueOf(detailPOS.get(0).getVal()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 逻辑删除gis_dev_ext 和share_dev设备
     * @param devId
     * @return
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteShareDevByDevId(String devId) throws BizException {
//        try {
            FeatureVO featureVO = gisDevExtPOMapper.findFeaturesByDevId(devId);
            if (("POINT".equals(featureVO.getType()))) {
                if (neo4jUtil.getPointAmount(devId) > 0) {
                    throw new BizException("当前管点连接着管线，不允许删除该管点！");
                } else {
                    neo4jUtil.deletePointById(devId);
                    gisDevExtPOMapper.deleteDevExtByDevId(devId);
                    shareDevPOMapper.deleteByPrimaryKey(devId);
                    return true;
                }
            } else {
                neo4jUtil.deleteLineById(devId);
                gisDevExtPOMapper.deleteDevExtByDevId(devId);
                shareDevPOMapper.deleteByPrimaryKey(devId);
                return true;
            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Logger.error("根据devId删除设备失败！dev_id =" + devId);
//            throw new BizException("根据devId删除设备失败!");
//        }
    }

    /**
     * 经纬度转地方坐标系
     * @param dto
     * @return
     * @throws BizException
     */
    public PointVO transformWgs84ToXY(PointDTO dto) throws BizException{
        PointVO pointVO = null;
        try {
            String geom = "POINT("+dto.getLng()+" "+dto.getLat()+")";
            String srid = netsAnalysisService.getValByDictString(dictConfig.getWaterPipeSrid());
            String transformGeom = gisDevExtPOMapper.transformWgs84ToCustom(geom,Integer.parseInt(srid));
            pointVO = gisDevExtPOMapper.getPointXYFromGeom(transformGeom);
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("根据经纬度转地方坐标系失败" + dto.toString());
            throw new BizException("根据经纬度转地方坐标系失败!");
        }
        return pointVO;
    }


}
