package com.jdrx.gis.service.dataManage;

import com.alibaba.fastjson.JSONObject;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.datamanage.ShareAddedNetsDTO;
import com.jdrx.gis.beans.dto.datamanage.ShareAddedPointDTO;
import com.jdrx.gis.beans.dto.datamanage.ShareLineDTO;
import com.jdrx.gis.beans.dto.datamanage.SharePointDTO;
import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.beans.entry.basic.GISDevExtPO;
import com.jdrx.gis.beans.entry.basic.ShareDevPO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.beans.vo.basic.PointVO;
import com.jdrx.gis.beans.vo.query.FieldNameVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.ShareDevPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.dao.query.DevQueryDAO;
import com.jdrx.gis.service.analysis.NetsAnalysisService;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.gis.service.basic.GISDeviceService;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.share.service.SequenceDefineService;
import org.postgresql.util.PGobject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Boolean saveAddedSharePoint(ShareAddedPointDTO dto) throws BizException{
        try {
            if(!savaSharePointOnLine(dto)){
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
            List<FieldNameVO> fieldNameVOS =  devQueryDAO.findFieldNamesByDevTypeId(typeId);
            for (int i = 0;i<fieldNameVOS.size();i++){
                if ((fieldNameVOS.get(i).getFieldName().equals("dev_id"))||(fieldNameVOS.get(i).getFieldName().equals("pipe_length"))){
                    fieldNameVOS.remove(i);
                    i--;
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
    public Boolean savaSharePointOnLine(ShareAddedPointDTO dto){
        try {
            Map<String,Object> map = dto.getMap();
            Long seq = sequenceDefineService.increment(gisDeviceService.sequenceKey());
            String devId = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seq);

            String geom = "POINT("+dto.getX()+" "+dto.getY()+")";
            String srid = netsAnalysisService.getValByDictString(dictConfig.getWaterPipeSrid());
            String transformGeom = gisDevExtPOMapper.transformWgs84ToCustom(geom,Integer.parseInt(srid));

            String jsonStr = JSONObject.toJSONString(map);
            PGobject jsonObject = new PGobject();
            jsonObject.setValue(jsonStr);
            jsonObject.setType("jsonb");

            GISDevExtPO po = new GISDevExtPO();
            po.setDevId(devId);
            po.setCode(map.get("code").toString());
            po.setName(map.get("name").toString());
            po.setGeom(transformGeom);
            po.setDataInfo(jsonObject);
            po.setTplTypeId(dto.getTypeId());

            ShareDevPO shareDevPO = new ShareDevPO();
            shareDevPO.setId(devId);
            shareDevPO.setName(map.get("name").toString());
            shareDevPO.setTypeId(dto.getTypeId());
            shareDevPO.setLng(String.format("%.8f",dto.getX()));
            shareDevPO.setLat(String.format("%.8f",dto.getY()));
            if (map.containsKey("dlm")){
                shareDevPO.setAddr(map.get("dlm").toString());
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

            //构造新的管线GISDevExtPO对象
            GISDevExtPO gisDevExtPOLine1 = gisDevExtPOMapper.getDevExtByDevId(dto.getLineDevId());
            GISDevExtPO gisDevExtPOLine2 = gisDevExtPOMapper.getDevExtByDevId(dto.getLineDevId());

            Long seqLine1 = sequenceDefineService.increment(gisDeviceService.sequenceKey());
            String devIdLine1 = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seqLine1);
            gisDevExtPOLine1.setDevId(devIdLine1);
            Long seqLine2 = sequenceDefineService.increment(gisDeviceService.sequenceKey());
            String devIdLine2 = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seqLine2);
            gisDevExtPOLine2.setDevId(devIdLine2);

            gisDevExtPOLine1.setGeom(lineGeom1);
            gisDevExtPOLine2.setGeom(lineGeom2);
            gisDevExtPOLine1.setId(null);
            gisDevExtPOLine2.setId(null);


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

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存管点
     * @param list
     * @return
     */
    public Boolean saveSharePoint(List<SharePointDTO> list){
        if (list == null||list.size() == 0 ){
            return false;
        }
        try {
            for(SharePointDTO dto:list){
                Long seq = sequenceDefineService.increment(gisDeviceService.sequenceKey());
                String devId = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seq);

                String geom = "POINT("+dto.getX()+" "+dto.getY()+")";
                String srid = netsAnalysisService.getValByDictString(dictConfig.getWaterPipeSrid());
                String transformGeom = gisDevExtPOMapper.transformWgs84ToCustom(geom,Integer.parseInt(srid));

                String jsonStr = JSONObject.toJSONString(dto.getMap());
                PGobject jsonObject = new PGobject();
                jsonObject.setValue(jsonStr);
                jsonObject.setType("jsonb");

                GISDevExtPO po = new GISDevExtPO();
                po.setDevId(devId);
                po.setCode(dto.getMap().get("code").toString());
                po.setName(dto.getMap().get("name").toString());
                po.setGeom(transformGeom);
                po.setDataInfo(jsonObject);
                po.setTplTypeId(dto.getTypeId());

                gisDevExtPOMapper.insertSelective(po);

            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存管线
     * @param list
     * @return
     */
    public Boolean savaShareLine(List<ShareLineDTO> list){
        try {

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
