package com.jdrx.gis.service.analysis;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.analysis.*;
import com.jdrx.gis.beans.dto.query.DevIDsForTypeDTO;
import com.jdrx.gis.beans.entity.analysis.*;
import com.jdrx.gis.beans.dto.analysis.AnalysisRecordDTO;
import com.jdrx.gis.beans.entity.basic.DictDetailPO;
import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.beans.vo.analysis.AnalysisResultVO;
import com.jdrx.gis.beans.vo.analysis.RecondValveVO;
import com.jdrx.gis.beans.vo.query.FieldNameVO;
import com.jdrx.gis.beans.vo.query.SpaceInfoVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.dao.analysis.GisPipeAnalysisPOMapper;
import com.jdrx.gis.dao.analysis.GisPipeAnalysisValvePOMapper;
import com.jdrx.gis.dao.analysis.GisWaterUserInfoPOMapper;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.MeasurementPOMapper;
import com.jdrx.gis.dao.query.DevQueryDAO;
import com.jdrx.gis.filter.assist.OcpService;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.gis.service.query.QueryDevService;
import com.jdrx.gis.util.ComUtil;
import com.jdrx.gis.util.ExcelStyleUtil;
import com.jdrx.gis.util.JavaFileToFormUpload;
import com.jdrx.gis.util.Neo4jUtil;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.jdbc.beans.vo.PageVO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.jdrx.gis.util.ExcelStyleUtil.createBodyStyle;

/**
 * @Description
 * @Author lr
 * @Time 2019/7/19 0019 下午 2:17
 */

@Service
public class NetsAnalysisService {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(NetsAnalysisService.class);

//    final static ObjectMapper mapper = new ObjectMapper();
//
//    static {
//        /**
//         * 使用neo4j的session执行条件语句statement，一定要使用这个反序列化对象为json字符串
//         * 下面的设置的作用是，比如对象属性字段name="李二明"，正常反序列化json为 == "name":"李二明"
//         * 如果使用下面的设置后，反序列name就是 == name:"jdrx"
//         * 而session执行语句create (:儿子{"name":"李二明","uuid":3330,"age":12,"height":"165cm"})会报错
//         * 因此,......etc
//         */
//        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
//    }

    @Autowired
    private Session session;
    @Autowired
    private MeasurementPOMapper measurementPOMapper;
    @Autowired
    private GisPipeAnalysisPOMapper gisPipeAnalysisPOMapper;
    @Autowired
    private GisPipeAnalysisValvePOMapper valvePOMapper;
    @Autowired
    private GisWaterUserInfoPOMapper waterUserInfoPOMapper;
    @Autowired
    private QueryDevService queryDevService;
    @Autowired
    private DevQueryDAO devQueryDAO;
    @Autowired
    private PathConfig pathConfig;
    @Autowired
    private DictConfig dictConfig;
    @Autowired
    private Neo4jUtil neo4jUtil;
    @Autowired
    private DictDetailService detailService;
    @Autowired
    private GISDevExtPOMapper gisDevExtPOMapper;

    /**
     * 查找一级关阀所有点
     * @param relationID
     * */

    public List<NodeDTO> findAllFamens(String relationID){
        String rid  = relationID;
        List<Value> values = neo4jUtil.getNodesFromRel(rid,GISConstants.NEO_LINE);
        if(values == null){
            Logger.info("查询失败，无关系数据！"+ relationID);
            return null;
        }
        String nodetype = null;
        String nodeName = null;
        String dev_id = null;
        //阀门列表
        List<NodeDTO> nodeDTOList = new ArrayList<>();
        //已访问列表
        Set<Value> lookedSet = new HashSet<>();
        //待访问列表
        ArrayList<Value> lookingSet = new ArrayList<>();
        List<Value> tmpList = new ArrayList<>();
        Value tmpValue = null;
        try {
            //初始化待访问列表  第一次，关系中的起始点
            lookingSet.addAll(values);
            ListIterator<Value> iterator = lookingSet.listIterator();
            while (iterator.hasNext()){
                tmpValue = iterator.next();
                //添加到已经检查的阀门队列，避免重复检查
                if (!(lookedSet.contains(tmpValue))){
                    lookedSet.add(tmpValue);
                }
                //从待访问列表中移除节点
                iterator.remove();
                //判断nodetype是否为阀门类型
                nodeName = tmpValue.asNode().get("name").asString();
                dev_id = tmpValue.asNode().get("dev_id").asString();
                nodetype = tmpValue.asNode().get("nodetype").asString();
                if (GISConstants.NEO_NODE_VALVE.equals(nodetype) &&(!nodeDTOList.contains(nodeName))){
                    //是阀门节点，添加到待返回的阀门队列
                    NodeDTO dto = new NodeDTO();
                    dto.setX(Double.valueOf(tmpValue.asNode().get("x").asString()));
                    dto.setY(Double.valueOf(tmpValue.asNode().get("y").asString()));
                    dto.setCode(nodeName);
                    dto.setDev_id(dev_id);
                    nodeDTOList.add(dto);
                    continue;
                }else {
                    //不是阀门节点，继续遍历其关联节点
                    tmpList =neo4jUtil.getNextNode(dev_id,GISConstants.NEO_POINT);
                    for (Value invalue:tmpList){
                        //不在待访问和访问过的节点，添加
                        if(!(lookedSet.contains(invalue))&&(!(lookingSet.contains(invalue)))){
                            iterator.add(invalue);
                        }
                    }
                }
                iterator = lookingSet.listIterator();
            }
            System.out.println("一级关阀所有阀门：total= "+nodeDTOList.size()+","+nodeDTOList.toString());
            return nodeDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查找二级关阀列表
     * @param failedValve
     * @param valveList
     * */
    public List<NodeDTO> findSecondAnalysisResult(String failedValve,List<String> valveList) throws BizException{
        List<Value> values = neo4jUtil.getNextNode(failedValve,GISConstants.NEO_POINT_LJ);
        if(values.size() == 0){
            throw new BizException("二级关阀查询失败，无关系设备id ："+ failedValve);
        }
        String nodetype = null;
        String nodeName = null;
        String devId = null;
        //阀门列表
        List<NodeDTO> nodeDTOList = new ArrayList<>();
        //已访问列表
        Set<Value> lookedSet = new HashSet<>();
        //待访问列表
        ArrayList<Value> lookingSet = new ArrayList<>();
        List<Value> tmpList = new ArrayList<>();
        Value tmpValue = null;
        try {
            //初始化待访问列表  第一次，关系中的起始点
            lookingSet.addAll(values);
            ListIterator<Value> iterator = lookingSet.listIterator();
            while (iterator.hasNext()){
                tmpValue = iterator.next();
                //添加到已经检查的阀门队列，避免重复检查
                if (!(lookedSet.contains(tmpValue))){
                    lookedSet.add(tmpValue);
                }
                //从待访问列表中移除节点
                iterator.remove();
                //判断是不是在第一次关阀列表中
                nodeName = tmpValue.asNode().get("name").asString();
                devId = tmpValue.asNode().get("dev_id").asString();
                if (valveList.contains(devId)){
                    continue;
                }
                //判断nodetype是否为阀门类型
                nodetype = tmpValue.asNode().get("nodetype").asString();
                if (GISConstants.NEO_NODE_VALVE.equals(nodetype) &&(!nodeDTOList.contains(devId))){
                    //是阀门节点，添加到待返回的阀门队列
                    NodeDTO dto = new NodeDTO();
                    dto.setDev_id(tmpValue.asNode().get("dev_id").asString());
                    dto.setX(Double.valueOf(tmpValue.asNode().get("x").asString()));
                    dto.setY(Double.valueOf(tmpValue.asNode().get("y").asString()));
                    dto.setCode(nodeName);
                    nodeDTOList.add(dto);
                    continue;
                }else {
                    //不是阀门节点，继续遍历其关联节点
                    tmpList =neo4jUtil.getNextNode(devId,GISConstants.NEO_POINT);
                    for (Value invalue:tmpList){
                        //不在待访问和访问过的节点，添加
                        if(!(lookedSet.contains(invalue))&&(!(lookingSet.contains(invalue)))){
                            iterator.add(invalue);
                        }
                    }
                }
                iterator = lookingSet.listIterator();
            }

            return nodeDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断list列表中是否有某个字符串
     * @param s
     * @param list
     * @return
     */
    public boolean findStringInNodeList(String s,List<NodeDTO> list){
        if (StringUtils.isEmpty(s) ||list ==null){
            return false;
        }
        for (NodeDTO nodeDTO:list){
            if (nodeDTO.getDev_id().equals(s)){
                return true;
            }
        }
        return false;
    }

    /**
     * 从一级关阀的所有阀门中筛选必须关闭的阀门
     * @param nodeDTOS
     */
    public List<NodeDTO> findFinalFamens(List<NodeDTO> nodeDTOS,String belongTo) throws Exception{
        if ((nodeDTOS == null) ){
            return null;
        }
        List<NodeDTO> valves = new ArrayList<>();
        List<String> valveList = new ArrayList<>();
        for (NodeDTO dto: nodeDTOS){
            valveList.add(dto.getDev_id());
        }
        List<Value> tmpList = new ArrayList<>();
        String devId = null;
        Value tmpNode = null;
        //必须关闭的阀门列表
        List<String>famenList = new ArrayList<>();
        //已访问列表
        Set<Value> lookedSet = new HashSet<>();
        //待访问列表
        ArrayList<Value> lookingSet = new ArrayList<>();
        Iterator<Value> iterator = null;
        try {
            //获取水源列表
            List<String> waterSourceList = neo4jUtil.getWaterSourceList(belongTo);
            //循环所有一级关阀的阀门列表,把每个阀门和所有水源地做连通分析和路径分析
            for (String tmpDevId :valveList){
                //以阀门为起点遍历相邻节点
                tmpList =  neo4jUtil.getNextNode(tmpDevId,GISConstants.NEO_POINT_LJ);
                lookingSet.clear();
                lookedSet.clear();
                lookingSet.addAll(tmpList);
                iterator = lookingSet.listIterator();
                while (iterator.hasNext()){
                    tmpNode = iterator.next();
                    devId = tmpNode.get("dev_id").asString();
                    //如果不在已经访问的节点中，添加到已访问列表
                    if(!(lookedSet.contains(tmpNode))){
                        lookedSet.add(tmpNode);
                    }
                    iterator.remove();
                    //首先判断这个节点是不是在阀门列表
                    if (valveList.contains(devId)){
                        continue;
                    }else if ((waterSourceList.contains(devId))&&(!famenList.contains(devId))){
                        famenList.add(tmpDevId);
                        break;
                    }
                    //获取这个节点的相邻节点
                    tmpList = neo4jUtil.getNextNode(devId,GISConstants.NEO_POINT_LJ);
                    for (Value nodeValue:tmpList ){
                        devId = nodeValue.get("dev_id").asString();
                        //首先判断这个节点是不是在阀门列表
                        if (valveList.contains(devId)){
                            continue;
                        }
                        //不在待访问、访问过的节点
                        if(!(lookedSet.contains(nodeValue))&&(!(lookingSet.contains(nodeValue)))){
                            lookingSet.add(nodeValue);
                        }
                        iterator = lookingSet.listIterator();
                    }
                }

            }

           for (NodeDTO nodeDTO : nodeDTOS ){
                if (famenList.contains(nodeDTO.getDev_id())){
                    valves.add(nodeDTO);
                }
           }

            if (famenList.size() == 0){
                System.out.println("必须关闭的阀门列表：total="+valveList.size()+","+valveList.toString());
                return nodeDTOS;
            }else {
                System.out.println("必须关闭的阀门列表：total="+famenList.size()+","+famenList.toString());
                return valves;
            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取爆管影响区域范围
     * @param lineID
     * @param dtoList
     */
    public List<String> findInfluenceArea(String lineID,List<NodeDTO> dtoList) throws Exception{
        if (dtoList == null){
            return null;
        }
        List<String>famenList = new ArrayList<>();
        for (NodeDTO dto:dtoList){
            famenList.add(dto.getDev_id());
        }
        List<String>lookingNodes = new ArrayList<>();
        List<String>lookedNodes = new ArrayList<>();
        List<String>influenceLines = new ArrayList<>();
        influenceLines.add(lineID);
        String nextPathID ;
        String nextDevId = null;
        List<Value> valueList = neo4jUtil.getNodesFromRel(lineID,GISConstants.NEO_LINE);
        for(Value value:valueList){
            Node node = value.asNode();
            String devId = node.asMap().get("dev_id").toString();
            if (!lookingNodes.contains(devId)){
                lookingNodes.add(devId);
            }
        }
        ListIterator<String> iterator = lookingNodes.listIterator();
        while (iterator.hasNext()){
            String tmpDevId = iterator.next();
            //从待访问的节点列表中移除,添加到已访问列表
            iterator.remove();
            if (!lookedNodes.contains(tmpDevId)){
                lookedNodes.add(tmpDevId);
            }
            if (!famenList.contains(tmpDevId)){
                //如果不是必须关闭的阀门，查找相邻的边和点
                List<Record> nextNodeAndPath= neo4jUtil.getNextNodeAndPath(tmpDevId,GISConstants.NEO_POINT);
                for(Record record:nextNodeAndPath){
                    nextDevId = record.get(0).asNode().asMap().get("dev_id").toString();
                    nextPathID = record.get(1).asMap().get("relationID").toString();
                    if(!famenList.contains(nextDevId)){
                        if (!lookingNodes.contains(nextDevId)&&(!lookedNodes.contains(nextDevId)))
                            //不是阀门，并且不在待访问和已访问列表，添加
                            iterator.add(nextDevId);
                    }
                    if (!influenceLines.contains(nextPathID)){
                        influenceLines.add(nextPathID);
                    }
                }
                iterator = lookingNodes.listIterator();
            }else {
                continue;
            }
        }
        System.out.println("影响区域范围：total = "+influenceLines.size()+","+influenceLines.toString());
        return influenceLines;

    }

    /**
     * 获取爆管影响用户列表
     * @return
     * @throws Exception
     */
    public List<GisWaterUserInfoPO>findInfluenceUser() throws Exception{
        List<GisWaterUserInfoPO> list =  waterUserInfoPOMapper.selectAll();
        return list;
    }

    /**
     * 获取爆管分析结果
     * @param dto
     * @return
     * @throws Exception
     */
    public AnalysisResultVO getAnalysisResult(IdDTO<String> dto) throws Exception{
        AnalysisResultVO analysisResultVO = new AnalysisResultVO();
        try {
            GISDevExtPO gisDevExtPO = gisDevExtPOMapper.getDevExtByDevId(dto.getId());
            //获取管网坐标系srid
            String srid = getValByDictString(dictConfig.getWaterPipeSrid());
            //获取所有阀门列表
            List<NodeDTO> valve_all = findAllFamens(dto.getId());
            //获取必须关闭的阀门
            List<NodeDTO> valve_final = findFinalFamens(valve_all,String.valueOf(gisDevExtPO.getBelongTo()));
            //获取影响范围
            List<String> devIds = findInfluenceArea(dto.getId(),valve_final);
            if (devIds == null){
                return analysisResultVO;
            }
            if (valve_final != null){
                analysisResultVO.setFmlist(valve_final);
                String geom = gisPipeAnalysisPOMapper.getExtendArea(devIds,srid);
                analysisResultVO.setGeom(geom);
            }
            List<GisWaterUserInfoPO>userInfoPOS = findInfluenceUser();
            analysisResultVO.setUserInfoPOS( userInfoPOS);
            analysisResultVO.setTotal(userInfoPOS.size());

        }catch (Exception e){
            e.printStackTrace();
            throw new BizException("获取爆管分析失败！");
        }

        return analysisResultVO;
    }

    /**
     * 获取二次关阀结果
     * @param secondAnalysisDTO
     * @return
     */
    public AnalysisResultVO getSecondAnalysisResult(SecondAnalysisDTO secondAnalysisDTO) throws BizException {
        //获取管网坐标系srid
        String srid = getValByDictString(dictConfig.getWaterPipeSrid());
        AnalysisResultVO vo = new AnalysisResultVO();
        List<String>failedList = secondAnalysisDTO.getFealtureList();
        List<String>fmList = new ArrayList<>();
        List<NodeDTO> fmlistNode = findAllFamens(secondAnalysisDTO.getDev_id());
        List<String> fmlistTmp = secondAnalysisDTO.getFmlist();
        String devId =secondAnalysisDTO.getDev_id();
        List<NodeDTO> resultDtoList = new ArrayList<>();
        List<String>tmpList = new ArrayList<>();
       try {
           for (NodeDTO nodeDTO : fmlistNode){
               if ((!StringUtils.isEmpty(nodeDTO.getDev_id())&&(!fmList.contains(nodeDTO.getDev_id()))))
               fmList.add(nodeDTO.getDev_id());
           }
           for(String string:failedList){
               List<NodeDTO> tmpNodeList= findSecondAnalysisResult(string,fmList);
               for(NodeDTO innerDto:tmpNodeList){
                   if ((!fmList.contains(innerDto.getCode()))&&(!failedList.contains(innerDto.getCode()))){
                       if (!tmpList.contains(innerDto.getCode())){
                           tmpList.add(innerDto.getCode());
                           resultDtoList.add(innerDto);
                       }
                   }
               }
           }
           vo.setFmlist(resultDtoList);
           List<NodeDTO> fmlist_all = new ArrayList<>();
          for (String s:fmList){
              if (failedList.contains(s)||(!fmlistTmp.contains(s))){
                  continue;
              }
              NodeDTO node = new NodeDTO();
              node.setCode(s);
              fmlist_all.add(node);
          }
          fmlist_all.addAll(resultDtoList);
           //获取影响区域范围
           List<String> devIds = findInfluenceArea(devId,fmlist_all);
           if (devIds != null){
               String area = gisPipeAnalysisPOMapper.getExtendArea(devIds,srid);
               vo.setGeom(area);
           }
           //添加影响用户
           List<GisWaterUserInfoPO> userInfoDTOS = findInfluenceUser();
           vo.setUserInfoPOS(userInfoDTOS);
           vo.setTotal(userInfoDTOS.size());
       }catch (Exception e){
           Logger.error("二次关阀分析失败： "+e.getMessage());
           throw new BizException("二次关阀分析失败!");
       }
        System.out.println("二级关阀所有阀门：total= "+resultDtoList.size()+","+resultDtoList);
        return vo;
    }

    /**
     * 保存爆管记录
     * @param recordDTO
     * @param deptPath
     * @return
     * @throws BizException
     */
    public boolean saveAnalysisRecond(AnalysisRecordDTO recordDTO,String deptPath) throws BizException {
        try {
            Long deptId = new OcpService().setDeptPath(deptPath).getUserWaterworksDeptId();
            GisPipeAnalysisPO gisPipeAnalysisPO =new GisPipeAnalysisPO();
            gisPipeAnalysisPO.setCode(recordDTO.getCode()) ;
            gisPipeAnalysisPO.setX(BigDecimal.valueOf(recordDTO.getPoint()[0]));
            gisPipeAnalysisPO.setY(BigDecimal.valueOf(recordDTO.getPoint()[1]));
            gisPipeAnalysisPO.setArea(recordDTO.getArea());
            gisPipeAnalysisPO.setName(recordDTO.getName());
            gisPipeAnalysisPO.setBelongTo(deptId);

            gisPipeAnalysisPOMapper.insertSelective(gisPipeAnalysisPO);
            //回填id
            Long id = gisPipeAnalysisPO.getId();

            //获取一次关阀列表
            List<String> valveFirst = recordDTO.getValveFirst();
            for (String code : valveFirst){
                GisPipeAnalysisValvePO valvePO = new GisPipeAnalysisValvePO();
                valvePO.setValveFirst(code);
                valvePO.setRid(id);
                //保存
                valvePOMapper.insertSelective(valvePO);
            }
            //获取二次关阀列表
            List<String> valveSecond = recordDTO.getValveSecond();
            for (String code : valveSecond){
                GisPipeAnalysisValvePO valvePO = new GisPipeAnalysisValvePO();
                valvePO.setValveSecond(code);
                valvePO.setRid(id);
                //保存
                valvePOMapper.insertSelective(valvePO);
            }
            //判断是否有关阀失败的 有就保存
            if(!(recordDTO.getValveFailed() ==null ||(recordDTO.getValveFailed().size() ==0))){
                List<String> failedValveList = recordDTO.getValveFailed();
                for (String str:failedValveList){
                    if (str.equals("")){
                        continue;
                    }
                    GisPipeAnalysisValvePO valvePO = new GisPipeAnalysisValvePO();
                    valvePO.setValveFailed(str);
                    valvePO.setRid(id);
                    //保存
                    valvePOMapper.insertSelective(valvePO);
                }
            }

        }catch (Exception e){
            Logger.error("保存爆管记录失败： "+e.getMessage());
            throw new BizException("保存爆管记录!");
        }
       return true;
    }

    /**
     * 获取爆管记录列表
     * @return
     */
    public PageVO<GisPipeAnalysisPO> getAnalysisRecondList(RecondParamasDTO dto) throws BizException{
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy());
        Page<GisPipeAnalysisPO> recordVOList = (Page<GisPipeAnalysisPO>)gisPipeAnalysisPOMapper.selectByParamas(dto);
        return new PageVO<>(recordVOList);
    }

    /**
     * 获取某条详细爆管记录
     * @param idDTO
     * @return
     */
    public RecondValveVO getValveById(IdDTO<Long> idDTO){
        RecondValveVO valveVOS = new RecondValveVO();
        List<NodeDTO>valveFirst = new ArrayList<>();
        List<NodeDTO>valveSecond = new ArrayList<>();
        List<NodeDTO>valveFailed = new ArrayList<>();
        Long id = idDTO.getId();
        //获取主记录爆管信息
        GisPipeAnalysisPO gisPipeAnalysisPO = gisPipeAnalysisPOMapper.selectById(id);
        if(gisPipeAnalysisPO != null){
            valveVOS.setCode(gisPipeAnalysisPO.getCode());
            BigDecimal[] point = {gisPipeAnalysisPO.getX(),gisPipeAnalysisPO.getY()};
            valveVOS.setPoint(point);
            valveVOS.setArea(gisPipeAnalysisPO.getArea().toString());
        }
        List<GisPipeAnalysisValvePO> valvePOS = valvePOMapper.selectByPrimaryKey(id);
        for(GisPipeAnalysisValvePO po:valvePOS){
            if (!StringUtils.isEmpty(po.getValveFirst())){
                NodeDTO node = neo4jUtil.getValveNode(po.getValveFirst());
                valveFirst.add(node);
            }else if(!StringUtils.isEmpty(po.getValveFailed())){
                NodeDTO node = neo4jUtil.getValveNode(po.getValveFailed());
                valveFailed.add(node);
            }else if(!StringUtils.isEmpty(po.getValveSecond())){
                NodeDTO node = neo4jUtil.getValveNode(po.getValveSecond());
                valveSecond.add(node);
            }
        }
        valveVOS.setName(gisPipeAnalysisPO.getName());
        valveVOS.setValveFailed(valveFailed);
        valveVOS.setValveFirst(valveFirst);
        valveVOS.setValveSecond(valveSecond);
        return valveVOS;

    }

//    /**
//     * 导出某条爆管记录
//     * @param dto
//     * @return
//     * @throws BizException
//     */
//    public String exportAnalysisResult(ExportValveDTO dto) throws BizException{
//        Long valve_typeid = -1L;
//        OutputStream os = null;
//        String title = null;
//        try {
//            String typeIdDictStr =dictConfig.getValveTypeId();
//            valve_typeid = Long.valueOf(getValByDictString(typeIdDictStr));
//            SXSSFWorkbook workbook;
//            workbook = new SXSSFWorkbook(1000); // 超过1000写入硬盘
//            if (dto.getName() == null || StringUtils.isEmpty(dto.getName())){
//                title = "爆管详细记录";
//            }else {
//                title = dto.getName();
//            }
//            SXSSFSheet sheet = workbook.createSheet(title);
//            sheet.setDefaultColumnWidth((short) 12); // 设置列宽
//            CellStyle style = ExcelStyleUtil.createHeaderStyle(workbook);
//            CellStyle style2 = createBodyStyle(workbook);
//            CellStyle style3 = createBodyStyle(workbook);
//            Font font = workbook.createFont();
//            font.setFontHeightInPoints((short) 12);//设置字体大小
//            style3.setFont(font);
//
//            Row row = sheet.createRow(1);
//            List<FieldNameVO> headerList =new ArrayList<>();
//            FieldNameVO tmpVO = new FieldNameVO();
//            tmpVO.setFieldDesc("阀门状态");
//            tmpVO.setFieldName("fmstatu");
//            headerList.add(tmpVO);
//            headerList.addAll(queryDevService.findFieldNamesByTypeID(valve_typeid));
//            if (Objects.isNull(headerList)) {
//                Logger.error("空间查询的表头信息为空");
//                throw new BizException("设备列表的title为空");
//            }
//            for (int i = 0; i < headerList.size(); i++) {
//                FieldNameVO fieldNameVO = headerList.get(i);
//                Cell cell = row.createCell(i);
//                cell.setCellStyle(style);
//                String txt = fieldNameVO.getFieldDesc();
//                XSSFRichTextString text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
//                cell.setCellValue(text);
//            }
//
//            int pageSize = GISConstants.EXPORT_PAGESIZE;
//            int pageTotal = 1;
//            String[] filedNames = headerList.stream().map(FieldNameVO::getFieldName).toArray(String[]::new);
//
//            //获取设备id
//            List<Long> devIdList = valvePOMapper.getDevIdsByCode(Arrays.asList(dto.getValveDevIds())) ;
//            List<Long>faileddevIdList = null;
//            if (!((dto.getFailedDevIds() == null)||(dto.getFailedDevIds().length == 0))){
//                faileddevIdList = valvePOMapper.getDevIdsByCode(Arrays.asList(dto.getFailedDevIds())) ;
//            }
//
//            int body_i = 2; // body 行索引
//            int pageNum = 1;
//            while (pageTotal-- > 0) {
//                DevIDsForTypeDTO devIDsForTypeDTO = new DevIDsForTypeDTO();
//                DevIDsForTypeDTO devIDsForTypeDTO2 = new DevIDsForTypeDTO();
//                devIDsForTypeDTO.setTypeId(valve_typeid);
//                devIDsForTypeDTO.setDevIds(devIdList.toArray(new String[devIdList.size()]));
//                devIDsForTypeDTO.setPageSize(pageSize);
//                devIDsForTypeDTO.setPageNum(pageNum);
//                PageVO<SpaceInfoVO> pageVO = queryDevService.findDevListPageByTypeID(devIDsForTypeDTO);
//                List<SpaceInfoVO> subDevList = pageVO.getData();
//
//                List<SpaceInfoVO> subDevList2 = new ArrayList<>();
//                if (faileddevIdList != null){
//                    devIDsForTypeDTO2.setTypeId(valve_typeid);
//                    devIDsForTypeDTO2.setDevIds(faileddevIdList.toArray(new String[faileddevIdList.size()]));
//                    devIDsForTypeDTO2.setPageSize(pageSize);
//                    devIDsForTypeDTO2.setPageNum(pageNum);
//                    PageVO<SpaceInfoVO> pageVO2 = queryDevService.findDevListPageByTypeID(devIDsForTypeDTO2);
//                    subDevList2 = pageVO2.getData();
//                }
//
//                if (Objects.nonNull(subDevList)) {
//                    subDevList.stream().map(vo -> {
//                        Object obj = vo.getDataInfo();
//                        if (Objects.isNull(obj)) {
//                            return vo;
//                        }
//                        try {
//                            Map<String, String> map = ComUtil.parseDataInfo(obj, filedNames);
//                            vo.setDataMap(map);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        return vo;
//                    }).collect(Collectors.toList());
//                    //设置第一行  爆管点经纬度
//                    Row xssfRow0 = sheet.createRow(0);
//                    Cell cell1 = xssfRow0.createCell(0);
//                    cell1.setCellValue("爆管点经纬度： "+dto.getPoint()[0]+" , "+dto.getPoint()[1]+" ; 爆管编号："+dto.getLineId());
//                    cell1.setCellStyle(style3);
//                    for (SpaceInfoVO spaceInfoVO : subDevList) {
//                        Map<String, String> map = spaceInfoVO.getDataMap();
//                        if (Objects.isNull(map)) {
//                            continue;
//                        }
//                        Row xssfRow = sheet.createRow(body_i++);
//                        for (int i = 0; i < headerList.size(); i++) {
//                            Cell cell = xssfRow.createCell(i);
//                            XSSFRichTextString text;
//                            FieldNameVO fieldNameVO = headerList.get(i);
//                            String txt = null;
//                            if (Objects.nonNull(fieldNameVO)) {
//                                String fieldName = fieldNameVO.getFieldName();
//                                if (GISConstants.DEV_TYPE_NAME.equals(fieldName)) {
//                                    txt = spaceInfoVO.getTypeName();
//                                } else {
//                                    txt = map.get(fieldName);
//                                }
//                                if (fieldName.equals("fmstatu")){
//                                    txt = "可关阀门";
//                                }
//                            }
//                            text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
//                            cell.setCellValue(text);
//                            cell.setCellStyle(style2);
//                        }
//                    }
//                }
//                if (Objects.nonNull(subDevList2)) {
//                    subDevList2.stream().map(vo -> {
//                        Object obj = vo.getDataInfo();
//                        if (Objects.isNull(obj)) {
//                            return vo;
//                        }
//                        try {
//                            Map<String, String> map = ComUtil.parseDataInfo(obj, filedNames);
//                            vo.setDataMap(map);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        return vo;
//                    }).collect(Collectors.toList());
//
//                    for (SpaceInfoVO spaceInfoVO : subDevList2) {
//                        Map<String, String> map = spaceInfoVO.getDataMap();
//                        if (Objects.isNull(map)) {
//                            continue;
//                        }
//                        Row xssfRow = sheet.createRow(body_i++);
//                        for (int i = 0; i < headerList.size(); i++) {
//                            Cell cell = xssfRow.createCell(i);
//                            XSSFRichTextString text;
//                            FieldNameVO fieldNameVO = headerList.get(i);
//                            String txt = null;
//                            if (Objects.nonNull(fieldNameVO)) {
//                                String fieldName = fieldNameVO.getFieldName();
//                                if (GISConstants.DEV_TYPE_NAME.equals(fieldName)) {
//                                    txt = spaceInfoVO.getTypeName();
//                                } else {
//                                    txt = map.get(fieldName);
//                                }
//                                if (fieldName.equals("fmstatu")){
//                                    txt = "关阀失败";
//                                }
//                            }
//                            text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
//                            cell.setCellValue(text);
//                            cell.setCellStyle(style2);
//                        }
//                    }
//                }
//                subDevList.clear();
//                pageNum ++;
//            }
//            String filePath = pathConfig.getDownloadPath() + "/" + title + ".xls";
//            os = new FileOutputStream(new File(filePath));
//            workbook.write(os);
//            String result = JavaFileToFormUpload.send(pathConfig.getUploadFileUrl(), filePath);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BizException("导出空间数据信息失败！");
//        } finally {
//            if (os != null) {
//                try {
//                    os.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }

    /**
     * 导出某条爆管记录
     * @param dto
     * @return
     * @throws BizException
     */
    public String exportAnalysisRecond(ExportValveRecondDTO dto) throws BizException{
        Long valve_typeid = -1L;
        OutputStream os = null;
        String title = null;
        try {
            String typeIdDictStr =dictConfig.getValveTypeId();
            valve_typeid = Long.valueOf(getValByDictString(typeIdDictStr));
            SXSSFWorkbook workbook;
            workbook = new SXSSFWorkbook(1000); // 超过1000写入硬盘
            if (dto.getName() == null || StringUtils.isEmpty(dto.getName())){
                title = "爆管详细记录";
            }else {
                title = dto.getName();
            }
            SXSSFSheet sheet = workbook.createSheet(title);
            sheet.setDefaultColumnWidth((short) 12); // 设置列宽
            CellStyle style = ExcelStyleUtil.createHeaderStyle(workbook);
            CellStyle style2 = createBodyStyle(workbook);
            CellStyle style3 = createBodyStyle(workbook);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 12);//设置字体大小
            style3.setFont(font);

            Row row = sheet.createRow(1);
            List<FieldNameVO> headerList =new ArrayList<>();
            FieldNameVO tmpVO = new FieldNameVO();
            tmpVO.setFieldDesc("阀门状态");
            tmpVO.setFieldName("fmstatu");
            headerList.add(tmpVO);
            headerList.addAll(queryDevService.findFieldNamesByTypeID(valve_typeid));
            if (Objects.isNull(headerList)) {
                Logger.error("空间查询的表头信息为空");
                throw new BizException("设备列表的title为空");
            }
            for (int i = 0; i < headerList.size(); i++) {
                FieldNameVO fieldNameVO = headerList.get(i);
                Cell cell = row.createCell(i);
                cell.setCellStyle(style);
                String txt = fieldNameVO.getFieldDesc();
                XSSFRichTextString text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
                cell.setCellValue(text);
            }

            int pageSize = GISConstants.EXPORT_PAGESIZE;
            int pageTotal = 1;
            String[] filedNames = headerList.stream().map(FieldNameVO::getFieldName).toArray(String[]::new);

            List<String> firstdevIdList = new ArrayList<>() ;
            List<String>faileddevIdList = new ArrayList<>() ;
            List<String>seconddevIdList = new ArrayList<>() ;

            if (!((dto.getFirstDevIds() == null)||(dto.getFirstDevIds().length == 0))){
                firstdevIdList =  valvePOMapper.getDevIdsByDevId(Arrays.asList(dto.getFirstDevIds()));
            }
            if (!((dto.getSecondDevIds() == null)||(dto.getSecondDevIds().length == 0))){
                seconddevIdList =  valvePOMapper.getDevIdsByDevId(Arrays.asList(dto.getSecondDevIds()));
            }
            if (!((dto.getFailedDevIds() == null)||(dto.getFailedDevIds().length == 0))){
                faileddevIdList = valvePOMapper.getDevIdsByDevId(Arrays.asList(dto.getFailedDevIds())) ;
            }

            int body_i = 2; // body 行索引
            int pageNum = 1;
            while (pageTotal-- > 0) {
                DevIDsForTypeDTO devIDsFirst = new DevIDsForTypeDTO();
                DevIDsForTypeDTO devIDsSecond = new DevIDsForTypeDTO();
                DevIDsForTypeDTO devIDsFailed = new DevIDsForTypeDTO();

                List<SpaceInfoVO> firstList = new ArrayList<>();
                List<SpaceInfoVO> failedList = new ArrayList<>();
                List<SpaceInfoVO> secondList = new ArrayList<>();

                if ((firstdevIdList != null) && (firstdevIdList.size()>0)){
                    devIDsFirst.setTypeId(valve_typeid);
                    devIDsFirst.setDevIds(firstdevIdList.toArray(new String[firstdevIdList.size()]));
                    devIDsFirst.setPageSize(pageSize);
                    devIDsFirst.setPageNum(pageNum);
                    PageVO<SpaceInfoVO> firstPageVO = queryDevService.findDevListPageByTypeID(devIDsFirst);
                    firstList = firstPageVO.getData();
                }
                if ((seconddevIdList != null) && (seconddevIdList.size()>0)){
                    devIDsSecond.setTypeId(valve_typeid);
                    devIDsSecond.setDevIds(seconddevIdList.toArray(new String[seconddevIdList.size()]));
                    devIDsSecond.setPageSize(pageSize);
                    devIDsSecond.setPageNum(pageNum);
                    PageVO<SpaceInfoVO> secondPageVO = queryDevService.findDevListPageByTypeID(devIDsSecond);
                    secondList = secondPageVO.getData();
                }
                if ((faileddevIdList != null) && (faileddevIdList.size()>0)){
                    devIDsFailed.setTypeId(valve_typeid);
                    devIDsFailed.setDevIds(faileddevIdList.toArray(new String[faileddevIdList.size()]));
                    devIDsFailed.setPageSize(pageSize);
                    devIDsFailed.setPageNum(pageNum);
                    PageVO<SpaceInfoVO> failedPageVO = queryDevService.findDevListPageByTypeID(devIDsFailed);
                    failedList = failedPageVO.getData();
                }

                //设置第一行  爆管点经纬度
                Row xssfRow0 = sheet.createRow(0);
                Cell cell1 = xssfRow0.createCell(0);
                cell1.setCellValue("爆管点经纬度： "+dto.getPoint()[0]+" , "+dto.getPoint()[1]+" ; 爆管编号："+dto.getLineId());
                cell1.setCellStyle(style3);

                if ((Objects.nonNull(firstList)) && (firstList.size()>0)) {
                    firstList.stream().map(vo -> {
                        Object obj = vo.getDataInfo();
                        if (Objects.isNull(obj)) {
                            return vo;
                        }
                        try {
                            Map<String, String> map = ComUtil.parseDataInfo(obj, filedNames);
                            vo.setDataMap(map);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return vo;
                    }).collect(Collectors.toList());

                    for (SpaceInfoVO spaceInfoVO : firstList) {
                        Map<String, String> map = spaceInfoVO.getDataMap();
                        if (Objects.isNull(map)) {
                            continue;
                        }
                        Row xssfRow = sheet.createRow(body_i++);
                        for (int i = 0; i < headerList.size(); i++) {
                            Cell cell = xssfRow.createCell(i);
                            XSSFRichTextString text;
                            FieldNameVO fieldNameVO = headerList.get(i);
                            String txt = null;
                            if (Objects.nonNull(fieldNameVO)) {
                                String fieldName = fieldNameVO.getFieldName();
                                if (GISConstants.DEV_TYPE_NAME.equals(fieldName)) {
                                    txt = spaceInfoVO.getTypeName();
                                } else {
                                    txt = map.get(fieldName);
                                }
                                if (fieldName.equals("fmstatu")){
                                    txt = "可关阀门（一次）";
                                }
                            }
                            text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
                            cell.setCellValue(text);
                            cell.setCellStyle(style2);
                        }
                    }
                }
                if ((Objects.nonNull(secondList)) && (secondList.size()>0)) {
                    secondList.stream().map(vo -> {
                        Object obj = vo.getDataInfo();
                        if (Objects.isNull(obj)) {
                            return vo;
                        }
                        try {
                            Map<String, String> map = ComUtil.parseDataInfo(obj, filedNames);
                            vo.setDataMap(map);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return vo;
                    }).collect(Collectors.toList());

                    for (SpaceInfoVO spaceInfoVO : secondList) {
                        Map<String, String> map = spaceInfoVO.getDataMap();
                        if (Objects.isNull(map)) {
                            continue;
                        }
                        Row xssfRow = sheet.createRow(body_i++);
                        for (int i = 0; i < headerList.size(); i++) {
                            Cell cell = xssfRow.createCell(i);
                            XSSFRichTextString text;
                            FieldNameVO fieldNameVO = headerList.get(i);
                            String txt = null;
                            if (Objects.nonNull(fieldNameVO)) {
                                String fieldName = fieldNameVO.getFieldName();
                                if (GISConstants.DEV_TYPE_NAME.equals(fieldName)) {
                                    txt = spaceInfoVO.getTypeName();
                                } else {
                                    txt = map.get(fieldName);
                                }
                                if (fieldName.equals("fmstatu")){
                                    txt = "可关阀门（二次）";
                                }
                            }
                            text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
                            cell.setCellValue(text);
                            cell.setCellStyle(style2);
                        }
                    }
                }
                if ((Objects.nonNull(failedList)) && (failedList.size()>0)) {
                    failedList.stream().map(vo -> {
                        Object obj = vo.getDataInfo();
                        if (Objects.isNull(obj)) {
                            return vo;
                        }
                        try {
                            Map<String, String> map = ComUtil.parseDataInfo(obj, filedNames);
                            vo.setDataMap(map);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return vo;
                    }).collect(Collectors.toList());

                    for (SpaceInfoVO spaceInfoVO : failedList) {
                        Map<String, String> map = spaceInfoVO.getDataMap();
                        if (Objects.isNull(map)) {
                            continue;
                        }
                        Row xssfRow = sheet.createRow(body_i++);
                        for (int i = 0; i < headerList.size(); i++) {
                            Cell cell = xssfRow.createCell(i);
                            XSSFRichTextString text;
                            FieldNameVO fieldNameVO = headerList.get(i);
                            String txt = null;
                            if (Objects.nonNull(fieldNameVO)) {
                                String fieldName = fieldNameVO.getFieldName();
                                if (GISConstants.DEV_TYPE_NAME.equals(fieldName)) {
                                    txt = spaceInfoVO.getTypeName();
                                } else {
                                    txt = map.get(fieldName);
                                }
                                if (fieldName.equals("fmstatu")){
                                    txt = "关阀失败";
                                }
                            }
                            text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
                            cell.setCellValue(text);
                            cell.setCellStyle(style2);
                        }
                    }
                }

                firstList.clear();
                pageNum ++;
            }
            String filePath = pathConfig.getDownloadPath() + "/" + title + ".xls";
            os = new FileOutputStream(new File(filePath));
            workbook.write(os);
            String result = JavaFileToFormUpload.send(pathConfig.getUploadFileUrl(), filePath);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("导出空间数据信息失败！");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 根据数据字典获取内容
     * @param dictStr
     * @return
     */
    public String getValByDictString(String dictStr){
        if (StringUtils.isEmpty(dictStr)){
            return null;
        }
        String caliberLyerUrl = null;
        try {
            List<DictDetailPO> detailPOs = detailService.findDetailsByTypeVal(dictStr);
            for (DictDetailPO dictDetail:detailPOs){
                caliberLyerUrl = dictDetail.getVal();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caliberLyerUrl;
    }

}
