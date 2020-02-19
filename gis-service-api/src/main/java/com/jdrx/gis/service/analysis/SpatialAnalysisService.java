package com.jdrx.gis.service.analysis;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.jdrx.gis.beans.dto.query.DevIDsDTO;
import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.beans.entity.user.SysOcpUserPo;
import com.jdrx.gis.beans.vo.basic.AnalysisVO;
import com.jdrx.gis.beans.vo.basic.FeatureVO;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.ShareDevPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.dubboRpc.UserRpc;
import com.jdrx.gis.service.query.AttrQueryService;
import com.jdrx.gis.util.Neo4jUtil;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.jdbc.beans.vo.PageVO;
import org.postgresql.util.PGobject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    private UserRpc userRpc;

    @Autowired
    ShareDevPOMapper shareDevPOMapper;


    /**
     * 获取连通性分析结果
     *
     * @param devId
     */
    public List<AnalysisVO> getConnectivityAnalysis(String devId) throws BizException {
        List<AnalysisVO> featureVOList = new ArrayList<>();
        List<String> list = new ArrayList<>();
        try {
            GISDevExtPO gisDevExtPO = gisDevExtPOMapper.getDevExtByDevId(devId);
            //判断设备类型是线的话，返回线两端的点设备和连通的线;如果是点，则返回连通的线
            if (gisDevExtPO.getGeom().contains("POINT")) {
                list = neo4jUtil.getNodeConnectionLine(gisDevExtPO.getDevId());
            } else {
                list = neo4jUtil.getNodeConnectionPointAndLine(gisDevExtPO.getDevId());
            }
            if (list.size() > 0) {
                featureVOList = getGisDevExtPOMapper.getLonelyShareDevByDevIds(list);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("获取连通性分析结果失败!");
            throw new BizException("获取连通性分析结果失败!");
        }
        if (featureVOList.size() == 0) {
            throw new BizException("设备连通个数为0");
        }
        return featureVOList;
    }

    /**
     * 获取连通性分析结果
     *
     * @param code
     */
    public List<AnalysisVO> getConnectivityByCode(String code) throws BizException {
        List<AnalysisVO> featureVOList = new ArrayList<>();
        List<String> list = new ArrayList<>();
        try {
            GISDevExtPO gisDevExtPO = gisDevExtPOMapper.selectByCode(code);
            String geomType = gisDevExtPOMapper.getGeomTypeByGeomStr(gisDevExtPO.getGeom());
            //判断设备类型是线的话，返回线两端的点设备和连通的线;如果是点，则返回连通的线
            if (geomType.contains("POINT")) {
                list = neo4jUtil.getNodeConnectionLine(gisDevExtPO.getDevId());
            } else {
                list = neo4jUtil.getNodeConnectionPointAndLine(gisDevExtPO.getDevId());
            }
            if (list.size() > 0) {
                featureVOList = getGisDevExtPOMapper.getLonelyShareDevByDevIds(list);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("获取连通性分析结果失败!");
            throw new BizException("获取连通性分析结果失败!");
        }
        if (featureVOList.size() == 0) {
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
        String[] devIDsDTOStr = devIDsDTO.getDevIds();
        List<String> list = new ArrayList<>();//neo4j返回的list
        Page<AnalysisVO> pageList = null;
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
            PageHelper.startPage(devIDsDTO.getPageNum(), devIDsDTO.getPageSize(), devIDsDTO.getOrderBy());
            pageList = (Page<AnalysisVO>) getGisDevExtPOMapper.getLonelyShareDevByDevIds(list);
            return new PageVO<AnalysisVO>(pageList);
        } else {
            return new PageVO<AnalysisVO>(pageList);
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
        String[] devIDsDTOStr = devIDsDTO.getDevIds();
        List<String> list = new ArrayList<>();//neo4j返回的list
        Page<AnalysisVO> pageList = null;
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
            PageHelper.startPage(devIDsDTO.getPageNum(), devIDsDTO.getPageSize(), devIDsDTO.getOrderBy());
            pageList = (Page<AnalysisVO>) getGisDevExtPOMapper.getLonelyShareDevByDevIds(list);
            return new PageVO<AnalysisVO>(pageList);
        } else {
            return new PageVO<AnalysisVO>(pageList);
        }

    }


    /**
     * 重复点分页查询
     *
     * @param devIDsDTO
     * @return
     * @throws BizException
     */
    public List<AnalysisVO> getRepeatPointsByDevIds(DevIDsDTO devIDsDTO) throws BizException {
//        Page<AnalysisVO> pageList;
        String[] devIDsDTOStr = devIDsDTO.getDevIds();
        List list = Arrays.asList(devIDsDTOStr);
//        PageHelper.startPage(devIDsDTO.getPageNum(), devIDsDTO.getPageSize());
//        pageList = (Page<AnalysisVO>) getGisDevExtPOMapper.getRepeatPointsByDevIds(list);
        List<AnalysisVO> list1 = gisDevExtPOMapper.getRepeatPointsByDevIds(list);
//        return new PageVO<AnalysisVO>(pageList);
        return list1;
    }

    /**
     * 重复线分页查询
     *
     * @param devIDsDTO
     * @return
     * @throws BizException
     */
    public List<AnalysisVO> getRepeatLinesByDevIds(DevIDsDTO devIDsDTO) throws BizException {
//        Page<AnalysisVO> pageList;
        String[] devIDsDTOStr = devIDsDTO.getDevIds();
        List list = Arrays.asList(devIDsDTOStr);
//        PageHelper.startPage(devIDsDTO.getPageNum(), devIDsDTO.getPageSize());
//        pageList = (Page<AnalysisVO>) getGisDevExtPOMapper.getRepeatLinesByDevIds(list);
//        return new PageVO<AnalysisVO>(pageList);
        List<AnalysisVO> list1 = gisDevExtPOMapper.getRepeatLinesByDevIds(list);
        return list1;
    }

    /**
     * 重复点拓扑删除
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRepeatPointByDevIds(String[] devId, Long userId, String token) throws Exception {
        //获得删除人
        SysOcpUserPo sysOcpUserPo = userRpc.getUserById(userId, token);
        String loginUserName = sysOcpUserPo.getName();
        Date date = new Date();
        List list1 = Arrays.asList(devId);
        List list = new ArrayList(list1);
        //找出重复点是孤立的点直接删除
        for (String id : devId) {
            if (neo4jUtil.getPointAmount(id) == 0) {
                neo4jUtil.deletePointById(id);
                gisDevExtPOMapper.deleteDevExtByDevId(id, loginUserName, date);
                shareDevPOMapper.deleteByPrimaryKey(id, loginUserName, date);
                list.remove(id);
            }
        }
        //判断list的数量,如果还为2,那么这些重复点就将重新建立拓扑关系
        if (list.size() == 2) {
            int amount1 = neo4jUtil.getPointAmount(list.get(0).toString());
            int amount2 = neo4jUtil.getPointAmount(list.get(1).toString());
            //优先将管点连接数量少的重复点删除了
            if (amount1 > amount2) {
                //找到删除的点的信息
                GISDevExtPO point = gisDevExtPOMapper.getDevExtByDevId(list.get(1).toString());
                neo4jUtil.deletePointById(list.get(1).toString());
                gisDevExtPOMapper.deleteDevExtByDevId(list.get(1).toString(), loginUserName, date);
                shareDevPOMapper.deleteByPrimaryKey(list.get(1).toString(), loginUserName, date);
                String code = point.getCode();
                List<GISDevExtPO> lineList = gisDevExtPOMapper.findLinesFromCode(code);
                for (GISDevExtPO a : lineList) {
                    JSONObject jb1 = JSONObject.parseObject(a.getDataInfo().toString());
                    Map<String, Object> map1 = (Map) jb1;
                    if (map1.get("startCode").equals(code)) {
                        int index = a.getCode().indexOf("-");
                        //找到未删除的点的编码
                        String startCode = gisDevExtPOMapper.getDevExtByDevId(list.get(0).toString()).getCode();
                        String endCode = a.getCode().substring(index);
                        String lineCode = startCode + endCode;
                        map1.replace("startCode", startCode);
                        String jsonStr1 = JSONObject.toJSONString(map1);
                        PGobject jsonObject1 = new PGobject();
                        jsonObject1.setValue(jsonStr1);
                        jsonObject1.setType("jsonb");
                        a.setCode(lineCode);
                        a.setDataInfo(jsonObject1);
                        a.setUpdateBy(loginUserName);
                        a.setUpdateAt(date);
                        gisDevExtPOMapper.updateByPrimaryKeySelective(a);
                    }
                    if (map1.get("endCode").equals(code)) {
                        int index = a.getCode().indexOf("-");
                        //找到未删除的点的编码
                        String endCode = gisDevExtPOMapper.getDevExtByDevId(list.get(0).toString()).getCode();
                        String startCode = a.getCode().substring(0, index);
                        String lineCode = startCode + "-" + endCode;
                        map1.replace("endCode", endCode);
                        String jsonStr1 = JSONObject.toJSONString(map1);
                        PGobject jsonObject1 = new PGobject();
                        jsonObject1.setValue(jsonStr1);
                        jsonObject1.setType("jsonb");
                        a.setCode(lineCode);
                        a.setDataInfo(jsonObject1);
                        a.setUpdateBy(loginUserName);
                        a.setUpdateAt(date);
                        gisDevExtPOMapper.updateByPrimaryKeySelective(a);
                    }
                }

            } else {
                //找到删除的点的信息
                GISDevExtPO point = gisDevExtPOMapper.getDevExtByDevId(list.get(0).toString());
                neo4jUtil.deletePointById(list.get(0).toString());
                gisDevExtPOMapper.deleteDevExtByDevId(list.get(0).toString(), loginUserName, date);
                shareDevPOMapper.deleteByPrimaryKey(list.get(0).toString(), loginUserName, date);
                String code = point.getCode();
                List<GISDevExtPO> lineList = gisDevExtPOMapper.findLinesFromCode(code);
                for (GISDevExtPO a : lineList) {
                    JSONObject jb1 = JSONObject.parseObject(a.getDataInfo().toString());
                    Map<String, Object> map1 = (Map) jb1;
                    if (map1.get("startCode").equals(code)) {
                        int index = a.getCode().indexOf("-");
                        //找到未删除的点的编码
                        String startCode = gisDevExtPOMapper.getDevExtByDevId(list.get(1).toString()).getCode();
                        String endCode = a.getCode().substring(index);
                        String lineCode = startCode + endCode;
                        map1.replace("startCode", startCode);
                        String jsonStr1 = JSONObject.toJSONString(map1);
                        PGobject jsonObject1 = new PGobject();
                        jsonObject1.setValue(jsonStr1);
                        jsonObject1.setType("jsonb");
                        a.setCode(lineCode);
                        a.setDataInfo(jsonObject1);
                        a.setUpdateBy(loginUserName);
                        a.setUpdateAt(date);
                        gisDevExtPOMapper.updateByPrimaryKeySelective(a);
                    }
                    if (map1.get("endCode").equals(code)) {
                        int index = a.getCode().indexOf("-");
                        //找到未删除的点的编码
                        String endCode = gisDevExtPOMapper.getDevExtByDevId(list.get(1).toString()).getCode();
                        String startCode = a.getCode().substring(0, index);
                        String lineCode = startCode + "-" + endCode;
                        map1.replace("endCode", endCode);
                        String jsonStr1 = JSONObject.toJSONString(map1);
                        PGobject jsonObject1 = new PGobject();
                        jsonObject1.setValue(jsonStr1);
                        jsonObject1.setType("jsonb");
                        a.setCode(lineCode);
                        a.setDataInfo(jsonObject1);
                        a.setUpdateBy(loginUserName);
                        a.setUpdateAt(date);
                        gisDevExtPOMapper.updateByPrimaryKeySelective(a);
                    }
                }
            }
        }
        return true;

    }

    /**
     * 重复线拓扑删除
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRepeatLineByDevIds(String[] devId, Long userId, String token) throws Exception {
        //获得删除人
        SysOcpUserPo sysOcpUserPo = userRpc.getUserById(userId, token);
        String loginUserName = sysOcpUserPo.getName();
        Date date = new Date();
//        if(devId.length<2){
//            throw new BizException("重复线数目少于2！");
//            //如果重复线的数目>2，则删掉重复线，保留一个
//        }else{
            for(int i = 0;i<devId.length-1;i++){
                neo4jUtil.deleteLineById(devId[i]);
                gisDevExtPOMapper.deleteDevExtByDevId(devId[i], loginUserName, date);
                shareDevPOMapper.deleteByPrimaryKey(devId[i], loginUserName, date);
            }
//        }
             return true;

    }

    /**
     * 找到删除的重复点信息
     * @param devId
     * @return
     */
    public List<GISDevExtPO>  findDeletePointByDevIds(String[] devId) throws Exception{
        List<String> list = Arrays.asList(devId);
        List<String> list3 = new ArrayList<>(list);
        List<String> list1 = new ArrayList();
        List<GISDevExtPO> list2 = new ArrayList();
        Iterator<String> iterator = list3.iterator();
        while(iterator.hasNext()){
            String s = iterator.next();
            if(neo4jUtil.getPointAmount(s)==0){
                iterator.remove();
                list1.add(s);
            }
        }
//        for (int i=0; i<devId.length;i++) {
//            if (neo4jUtil.getPointAmount(devId[i]) == 0) {
//                list1.add(devId[i]);
//                list.remove(devId[i]);
//            }
//        }
        if(list3.size()==2){
            int amount1 = neo4jUtil.getPointAmount(list3.get(0).toString());
            int amount2 = neo4jUtil.getPointAmount(list3.get(1).toString());
            if(amount1>amount2){
                list1.add(list3.get(1));
            }else{
                list1.add(list3.get(0));
            }
        }
        for(String devid:list1){
            GISDevExtPO po = gisDevExtPOMapper.getDevExtByDevId(devid);
            list2.add(po);
        }
        return list2;
    }

    /**
     * 找到删除的重复线信息
     * @param devId
     * @return
     */
    public List<GISDevExtPO>  findDeleteLineByDevIds(String[] devId) throws Exception{
        List<GISDevExtPO> list2 = new ArrayList();
        for(int i = 0;i<devId.length-1;i++){
            GISDevExtPO po = gisDevExtPOMapper.getDevExtByDevId(devId[i]);
            list2.add(po);
        }
        return list2;
    }


}
