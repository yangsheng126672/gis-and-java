package com.jdrx.gis.service.analysis;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.jdrx.gis.beans.dto.query.DevIDsDTO;
import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.beans.vo.basic.AnalysisVO;
import com.jdrx.gis.beans.vo.basic.FeatureVO;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.service.query.AttrQueryService;
import com.jdrx.gis.util.Neo4jUtil;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.jdbc.beans.vo.PageVO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @Description
 * @Author lr
 * @Time 2019/10/23 0023 上午 10:23
 */

@Service
public class SpatialAnalysisService {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(SpatialAnalysisService.class);

    @Autowired
    private GISDevExtPOMapper gisDevExtPOMapper;

    @Autowired
    private GISDevExtPOMapper getGisDevExtPOMapper;

    @Autowired
    private NetsAnalysisService netsAnalysisService;

    @Autowired
    ShareDevTypePOMapper shareDevTypePOMapper;

    @Autowired
    AttrQueryService attrQueryService;

    @Autowired
    Neo4jUtil neo4jUtil;



    /**
     * 获取连通性分析结果
     * @param devId
     */
    public List<FeatureVO> getConnectivityAnalysis(String devId) throws BizException {
        List<FeatureVO> featureVOList = new ArrayList<>();
        List<String> list = new ArrayList<>();
        try {
            GISDevExtPO gisDevExtPO = gisDevExtPOMapper.getDevExtByDevId(devId);
            //判断设备类型是线的话，返回线两端的点设备和连通的线;如果是点，则返回连通的线
            if(gisDevExtPO.getGeom().contains("POINT")){
                list = neo4jUtil.getNodeConnectionLine(gisDevExtPO.getDevId());
            }else{
                list = neo4jUtil.getNodeConnectionPointAndLine(gisDevExtPO.getDevId());
            }
            featureVOList = getGisDevExtPOMapper.findFeaturesByDevIds(list);

        }catch (Exception e) {
            e.printStackTrace();
            Logger.error("获取连通性分析结果失败!");
            throw new BizException("获取连通性分析结果失败!");
        }
        if (featureVOList.size() == 0){
            throw new BizException("设备连通个数为0");
        }
        return featureVOList;
    }

    /**
     * 获取连通性分析结果
     * @param code
     */
    public List<FeatureVO> getConnectivityByCode(String code) throws BizException {
        List<FeatureVO> featureVOList = new ArrayList<>();
        List<String> list = new ArrayList<>();
        try {
            GISDevExtPO gisDevExtPO = gisDevExtPOMapper.selectByCode(code);
            String geomType = gisDevExtPOMapper.getGeomTypeByGeomStr(gisDevExtPO.getGeom());
            //判断设备类型是线的话，返回线两端的点设备和连通的线;如果是点，则返回连通的线
            if(geomType.contains("POINT")){
                list = neo4jUtil.getNodeConnectionLine(gisDevExtPO.getDevId());
            }else{
                list = neo4jUtil.getNodeConnectionPointAndLine(gisDevExtPO.getDevId());
            }
            featureVOList = getGisDevExtPOMapper.findFeaturesByDevIds(list);

        }catch (Exception e) {
            e.printStackTrace();
            Logger.error("获取连通性分析结果失败!");
            throw new BizException("获取连通性分析结果失败!");
        }
        if (featureVOList.size() == 0){
            throw new BizException("设备连通个数为0");
        }
        return featureVOList;
    }

    /**
     * 孤立点分页查询
     *
     * @param devIDsDTO
     * @return
     * @throws BizException
     */
    public PageVO<AnalysisVO> getLonelyPointsByDevIds(DevIDsDTO devIDsDTO) throws BizException {
        PageHelper.startPage(devIDsDTO.getPageNum(), devIDsDTO.getPageSize(), devIDsDTO.getOrderBy());
        String[] devIDsDTOStr = devIDsDTO.getDevIds();
        List<String> list = new ArrayList<>();//neo4j返回的list
        String s = "";
        //查询全局
        if (devIDsDTOStr == null || devIDsDTOStr.length == 0) {
            list = neo4jUtil.getLonelyPointsByDevIds(s);
        } else {
            for (String ids : devIDsDTOStr) {
                s = s + "\"" + ids + "\"" + ",";
            }
            s = s.substring(0, s.length() - 1);
            list = neo4jUtil.getLonelyPointsByDevIds(s);
        }
        if (list != null && list.size() > 0) {
            Page<AnalysisVO> pageList = (Page<AnalysisVO>) getGisDevExtPOMapper.getLonelyPointsByDevIds(list);
            return new PageVO<AnalysisVO>(pageList);
        } else {
            return null;
        }
    }

    /**
     * 孤立线分页查询
     *
     * @param devIDsDTO
     * @return
     * @throws BizException
     */
    public PageVO<AnalysisVO> getLonelyLinesByDevIds(DevIDsDTO devIDsDTO) throws BizException {
        PageHelper.startPage(devIDsDTO.getPageNum(), devIDsDTO.getPageSize(), devIDsDTO.getOrderBy());
        String[] devIDsDTOStr = devIDsDTO.getDevIds();
        List<String> list = new ArrayList<>();//neo4j返回的list
        String s = "";
        if (devIDsDTOStr == null || devIDsDTOStr.length == 0) {
            list = neo4jUtil.getLonelyLinesByDevIds(s);
        } else {
            for (String ids : devIDsDTOStr) {
                s = s + "\"" + ids + "\"" + ",";
            }
            s = s.substring(0, s.length() - 1);
            list = neo4jUtil.getLonelyLinesByDevIds(s);
        }
        //集合去重 查找的数据可能存在数据重复的问题
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        if (list != null && list.size() > 0) {
            Page<AnalysisVO> pageList = (Page<AnalysisVO>) getGisDevExtPOMapper.getLonelyLinesByDevIds(list);
            return new PageVO<AnalysisVO>(pageList);
        } else {
            return null;
        }
    }

    /**
     * 逻辑删除gis_dev_ext 和share_dev 点
     *
     * @param devId
     * @return
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteLonelyPointByDevId(String devId) throws BizException {
        try {
            getGisDevExtPOMapper.deleteDevExtByDevId(devId);
            getGisDevExtPOMapper.deleteShareDevByDevId(devId);
            if (neo4jUtil.deletePointById(devId)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("根据devId删除点失败！dev_id =" + devId);
            throw new BizException("根据devId删除点失败!");
        }
    }

    /**
     * 逻辑删除gis_dev_ext 和share_dev线
     *
     * @param devId
     * @return
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteLonelyLineByDevId(String devId) throws BizException {
        try {
            getGisDevExtPOMapper.deleteDevExtByDevId(devId);
            getGisDevExtPOMapper.deleteShareDevByDevId(devId);
            if (neo4jUtil.deleteLineById(devId)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("根据devId删除线失败！dev_id =" + devId);
            throw new BizException("根据devId删除线失败!");
        }
    }

    /**
     * 重复点分页查询
     *
     * @param devIDsDTO
     * @return
     * @throws BizException
     */
    public PageVO<AnalysisVO> getRepeatPointsByDevIds(DevIDsDTO devIDsDTO) throws BizException {
        PageHelper.startPage(devIDsDTO.getPageNum(), devIDsDTO.getPageSize(), devIDsDTO.getOrderBy());
        Page<AnalysisVO> pageList;
        String[] devIDsDTOStr = devIDsDTO.getDevIds();
        List list = Arrays.asList(devIDsDTOStr);
        pageList = (Page<AnalysisVO>) getGisDevExtPOMapper.getRepeatPointsByDevIds(list);
        return new PageVO<AnalysisVO>(pageList);
    }
    /**
     * 重复线分页查询
     *
     * @param devIDsDTO
     * @return
     * @throws BizException
     */
    public PageVO<AnalysisVO> getRepeatLinesByDevIds(DevIDsDTO devIDsDTO) throws BizException {
        PageHelper.startPage(devIDsDTO.getPageNum(), devIDsDTO.getPageSize(), devIDsDTO.getOrderBy());
        Page<AnalysisVO> pageList;
        String[] devIDsDTOStr = devIDsDTO.getDevIds();
        List list = Arrays.asList(devIDsDTOStr);
        pageList = (Page<AnalysisVO>) getGisDevExtPOMapper.getRepeatLinesByDevIds(list);
        return new PageVO<AnalysisVO>(pageList);
    }

}
