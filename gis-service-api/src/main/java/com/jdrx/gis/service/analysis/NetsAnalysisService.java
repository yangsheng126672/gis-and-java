package com.jdrx.gis.service.analysis;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.analysis.*;
import com.jdrx.gis.beans.dto.query.DevIDsForTypeDTO;
import com.jdrx.gis.beans.entry.analysis.*;
import com.jdrx.gis.beans.dto.analysis.AnalysisRecordDTO;
import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.beans.vo.analysis.AnalysisResultVO;
import com.jdrx.gis.beans.vo.analysis.RecondValveVO;
import com.jdrx.gis.beans.vo.query.FieldNameVO;
import com.jdrx.gis.beans.vo.query.SpaceInfoVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.dao.analysis.GisPipeAnalysisPOMapper;
import com.jdrx.gis.dao.analysis.GisPipeAnalysisValvePOMapper;
import com.jdrx.gis.dao.analysis.GisWaterUserInfoPOMapper;
import com.jdrx.gis.dao.basic.MeasurementPOMapper;
import com.jdrx.gis.dao.query.DevQueryDAO;
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
import org.neo4j.driver.v1.StatementResult;
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
                nodetype = tmpValue.asNode().get("nodetype").asString();
                if (GISConstants.NEO_NODE_VALVE.equals(nodetype) &&(!nodeDTOList.contains(nodeName))){
                    //是阀门节点，添加到待返回的阀门队列
                    NodeDTO dto = new NodeDTO();
                    dto.setDev_id(tmpValue.asNode().get("dev_id").asString());
                    dto.setX(tmpValue.asNode().get("x").asDouble());
                    dto.setY(tmpValue.asNode().get("y").asDouble());
                    dto.setCode(nodeName);
                    nodeDTOList.add(dto);
                    continue;
                }else {
                    //不是阀门节点，继续遍历其关联节点
                    tmpList =neo4jUtil.getNextNode(nodeName,GISConstants.NEO_POINT);
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
     * @param code
     * @param fmlist
     * */
    public List<NodeDTO> findSecondAnalysisResult(String code,List<String>fmlist){
        List<Value> values = neo4jUtil.getNextNode(code,GISConstants.NEO_POINT_LJ);
        if(values == null){
            Logger.info("查询失败，无关系数据！"+ code);
            return null;
        }
        String nodetype = null;
        String nodeName = null;
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
                if (fmlist.contains(nodeName)){
                    continue;
                }
                //判断nodetype是否为阀门类型
                nodetype = tmpValue.asNode().get("nodetype").asString();
                if (GISConstants.NEO_NODE_VALVE.equals(nodetype) &&(!nodeDTOList.contains(nodeName))){
                    //是阀门节点，添加到待返回的阀门队列
                    NodeDTO dto = new NodeDTO();
                    dto.setDev_id(tmpValue.asNode().get("dev_id").asString());
                    dto.setX(tmpValue.asNode().get("x").asDouble());
                    dto.setY(tmpValue.asNode().get("y").asDouble());
                    dto.setCode(nodeName);
                    nodeDTOList.add(dto);
                    continue;
                }else {
                    //不是阀门节点，继续遍历其关联节点
                    tmpList =neo4jUtil.getNextNode(nodeName,GISConstants.NEO_POINT);
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
            if (nodeDTO.getCode().equals(s)){
                return true;
            }
        }
        return false;
    }

    /**
     * 从一级关阀的所有阀门中筛选必须关闭的阀门
     * @param list
     */
    public List<NodeDTO> findFinalFamens(List<NodeDTO>list)throws Exception{
        if ((list == null) ){
            return null;
        }
        List<Value> tmpList = new ArrayList<>();
        String tmpNodeName = null;
        Value tmpNode = null;
        //必须关闭的阀门列表
        List<NodeDTO>famenList = new ArrayList<>();
        //已访问列表
        Set<Value> lookedSet = new HashSet<>();
        //待访问列表
        ArrayList<Value> lookingSet = new ArrayList<>();
        Iterator<Value> iterator = null;
        try {
            //获取水源列表
            List<String> waterSourceList = neo4jUtil.getWaterSourceList();
            //循环所有一级关阀的阀门列表,把每个阀门和所有水源地做连通分析和路径分析
            for (NodeDTO dto :list){
                String famenName = dto.getCode();
                //以阀门为起点遍历相邻节点
                tmpList =  neo4jUtil.getNextNode(famenName,GISConstants.NEO_POINT_LJ);
                lookingSet.clear();
                lookedSet.clear();
                lookingSet.addAll(tmpList);
                iterator = lookingSet.listIterator();
                while (iterator.hasNext()){
                    tmpNode = iterator.next();
                    tmpNodeName = tmpNode.get("name").asString();
                    //如果不在已经访问的节点中，添加到已访问列表
                    if(!(lookedSet.contains(tmpNode))){
                        lookedSet.add(tmpNode);
                    }
                    iterator.remove();
                    //首先判断这个节点是不是在阀门列表
                    if (findStringInNodeList(tmpNodeName,list)){
                        continue;
                    }else if ((waterSourceList.contains(tmpNodeName))&&(!famenList.contains(tmpNodeName))){
                        famenList.add(dto);
                        break;
                    }
                    //获取这个节点的相邻节点
                    tmpList = neo4jUtil.getNextNode(tmpNodeName,GISConstants.NEO_POINT_LJ);
                    for (Value nodeValue:tmpList ){
                        tmpNodeName = nodeValue.get("name").asString();
                        //首先判断这个节点是不是在阀门列表
                        if (list.contains(tmpNodeName)){
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
            if (famenList.size() == 0){
                System.out.println("必须关闭的阀门列表：total="+list.size()+","+list.toString());
                return list;
            }else {
                System.out.println("必须关闭的阀门列表：total="+famenList.size()+","+famenList.toString());
                return famenList;
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
    public List<String> findInfluenceArea(String lineID,List<NodeDTO>dtoList) throws Exception{
        if (dtoList == null){
            return null;
        }
        List<String>famenList = new ArrayList<>();
        for (NodeDTO dto:dtoList){
            famenList.add(dto.getCode());
        }
        List<String>lookingNodes = new ArrayList<>();
        List<String>lookedNodes = new ArrayList<>();
        List<String>influenceLines = new ArrayList<>();
        influenceLines.add(lineID);
        String nextPathID ;
        String nextNodeName = null;
        List<Value> valueList = neo4jUtil.getNodesFromRel(lineID,GISConstants.NEO_LINE);
        for(Value value:valueList){
            Node node = value.asNode();
            String nodeName = node.asMap().get("name").toString();
            if (!lookingNodes.contains(nodeName)){
                lookingNodes.add(nodeName);
            }
        }
        ListIterator<String> iterator = lookingNodes.listIterator();
        while (iterator.hasNext()){
            String tmpNodeName = iterator.next();
            //从待访问的节点列表中移除,添加到已访问列表
            iterator.remove();
            if (!lookedNodes.contains(tmpNodeName)){
                lookedNodes.add(tmpNodeName);
            }
            if (!famenList.contains(tmpNodeName)){
                //如果不是必须关闭的阀门，查找相邻的边和点
                List<Record> nextNodeAndPath= neo4jUtil.getNextNodeAndPath(tmpNodeName,GISConstants.NEO_POINT);
                for(Record record:nextNodeAndPath){
                    nextNodeName = record.get(0).asNode().asMap().get("name").toString();
                    nextPathID = record.get(1).asMap().get("relationID").toString();
                    if(!famenList.contains(nextNodeName)){
                        if (!lookingNodes.contains(nextNodeName)&&(!lookedNodes.contains(nextNodeName)))
                            //不是阀门，并且不在待访问和已访问列表，添加
                            iterator.add(nextNodeName);
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
//        String[] devIds = influenceLines.toArray(new String[influenceLines.size()]);
//
//        String devStr = null;
//        List<String> ids = null;
//        if (Objects.nonNull(devIds) && devIds.length > 0) {
//            ids = Objects.nonNull(devIds) ? Arrays.asList(devIds) : Lists.newArrayList();
//            devStr = Joiner.on(",").join(ids);
//        }
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
        //获取管网坐标系srid
        String srid = getValByDictString(dictConfig.getWaterPipeSrid());

//        //yuechi
//        if( "83308".equals(dto.getId())){
//            List<NodeDTO> fmlist_final = new ArrayList<>();
//            NodeDTO nodeDTO = new NodeDTO();
//            nodeDTO.setDev_id(99419L);
//            nodeDTO.setX(106.4596);
//            nodeDTO.setY(30.5338);
//            nodeDTO.setCode("2JS128");
//            fmlist_final.add(nodeDTO);
//            analysisResultVO.setFmlist(fmlist_final);
//            String geom = "POLYGON((106.459593149117 30.5338276457265,106.471855569077 30.5330651051196,106.465779925876 30.5331336171525,106.462296023151 30.5334703970648,106.459593149117 30.5338276457265))";
//            analysisResultVO.setGeom(geom);
//            List<GisWaterUserInfoPO>userInfoPOS = findInfluenceUser();
//            analysisResultVO.setUserInfoPOS( userInfoPOS);
//            analysisResultVO.setTotal(userInfoPOS.size());
//            return analysisResultVO;
//        }
//        //qianfeng
//        if("74587".equals(dto.getId())){
//            List<NodeDTO> fmlist_final = new ArrayList<>();
//            NodeDTO nodeDTO = new NodeDTO();
//            nodeDTO.setDev_id(99419L);
//            nodeDTO.setX(106.7314);
//            nodeDTO.setY(30.3731);
//            nodeDTO.setCode("1JS974");
//            fmlist_final.add(nodeDTO);
//            analysisResultVO.setFmlist(fmlist_final);
//            String geom = "POLYGON((106.731406291511 30.3730975739014,106.731121656797 30.3720717711355,106.730587742321 30.370923110294,106.730150247105 30.37077851258,106.730788000591 30.3719688624995,106.731406291511 30.3730975739014))";
//            analysisResultVO.setGeom(geom);
//            List<GisWaterUserInfoPO>userInfoPOS = findInfluenceUser();
//            analysisResultVO.setUserInfoPOS( userInfoPOS);
//            analysisResultVO.setTotal(userInfoPOS.size());
//            return analysisResultVO;
//        }
//        //wusheng
//        if("80840".equals(dto.getId())){
//            List<NodeDTO> fmlist_final = new ArrayList<>();
//            NodeDTO nodeDTO = new NodeDTO();
//            nodeDTO.setDev_id(98657L);
//            nodeDTO.setX(106.28602739);
//            nodeDTO.setY(30.360879773);
//            nodeDTO.setCode("2J649");
//            fmlist_final.add(nodeDTO);
//            analysisResultVO.setFmlist(fmlist_final);
//            String geom = "POLYGON((106.286031242797 30.3608797537096,106.286027394791 30.3608797735347,106.285262292459 30.3624207846633,106.285107297802 30.3630316042983,106.285359628108 30.3630970658476,106.285997951562 30.361801140805,106.286031316709 30.3616723223547,106.286031242797 30.3608797537096))";
//            analysisResultVO.setGeom(geom);
//            List<GisWaterUserInfoPO>userInfoPOS = findInfluenceUser();
//            analysisResultVO.setUserInfoPOS( userInfoPOS);
//            analysisResultVO.setTotal(userInfoPOS.size());
//            return analysisResultVO;
//        }


        //获取所有阀门列表
        List<NodeDTO> fmlist_all = findAllFamens(dto.getId());
        //获取必须关闭的阀门
        List<NodeDTO> fmlist_final = findFinalFamens(fmlist_all);
        List<String> devIds = findInfluenceArea(dto.getId(),fmlist_final);
        if (devIds == null){
            return analysisResultVO;
        }
        if (fmlist_final != null){
            analysisResultVO.setFmlist(fmlist_final);
            String geom = gisPipeAnalysisPOMapper.getExtendArea(devIds,srid);
            analysisResultVO.setGeom(geom);
        }
        List<GisWaterUserInfoPO>userInfoPOS = findInfluenceUser();
        analysisResultVO.setUserInfoPOS( userInfoPOS);
        analysisResultVO.setTotal(userInfoPOS.size());
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
        List<String>dtoList = secondAnalysisDTO.getFealtureList();
        List<String>fmList = new ArrayList<>();
        List<NodeDTO> fmlistNode = findAllFamens(secondAnalysisDTO.getDev_id());
        List<String> fmlistTmp = secondAnalysisDTO.getFmlist();
        String dev_id =secondAnalysisDTO.getDev_id();
        List<NodeDTO> resultDtoList = new ArrayList<>();
        List<String>tmpList = new ArrayList<>();
       try {
           for (NodeDTO nodeDTO : fmlistNode){
               if ((!StringUtils.isEmpty(nodeDTO.getCode())&&(!fmList.contains(nodeDTO.getCode()))))
               fmList.add(nodeDTO.getCode());
           }
           for(String string:dtoList){
               List<NodeDTO> tmpNodeList= findSecondAnalysisResult(string,fmList);
               for(NodeDTO innerDto:tmpNodeList){
                   if ((!fmList.contains(innerDto.getCode()))&&(!dtoList.contains(innerDto.getCode()))){
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
              if (dtoList.contains(s)||(!fmlistTmp.contains(s))){
                  continue;
              }
              NodeDTO node = new NodeDTO();
              node.setCode(s);
              fmlist_all.add(node);
          }
          fmlist_all.addAll(resultDtoList);
           //获取影响区域范围
           List<String> devIds = findInfluenceArea(dev_id,fmlist_all);
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
     * @return
     * @throws BizException
     */
    public boolean saveAnalysisRecond(AnalysisRecordDTO recordDTO) throws BizException {
        try {
            GisPipeAnalysisPO gisPipeAnalysisPO =new GisPipeAnalysisPO();
            gisPipeAnalysisPO.setCode(recordDTO.getCode()) ;
            gisPipeAnalysisPO.setX(BigDecimal.valueOf(recordDTO.getPoint()[0]));
            gisPipeAnalysisPO.setY(BigDecimal.valueOf(recordDTO.getPoint()[1]));
            gisPipeAnalysisPO.setArea(recordDTO.getArea());
            gisPipeAnalysisPO.setName(recordDTO.getName());

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

    /**
     * 导出某条爆管记录
     * @param dto
     * @return
     * @throws BizException
     */
    public String exportAnalysisResult(ExportValveDTO dto) throws BizException{
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

            //获取设备id
            List<Long> devIdList = valvePOMapper.getDevIdsByCode(Arrays.asList(dto.getValveDevIds())) ;
            List<Long>faileddevIdList = null;
            if (!((dto.getFailedDevIds() == null)||(dto.getFailedDevIds().length == 0))){
                faileddevIdList = valvePOMapper.getDevIdsByCode(Arrays.asList(dto.getFailedDevIds())) ;
            }

            int body_i = 2; // body 行索引
            int pageNum = 1;
            while (pageTotal-- > 0) {
                DevIDsForTypeDTO devIDsForTypeDTO = new DevIDsForTypeDTO();
                DevIDsForTypeDTO devIDsForTypeDTO2 = new DevIDsForTypeDTO();
                devIDsForTypeDTO.setTypeId(valve_typeid);
                devIDsForTypeDTO.setDevIds(devIdList.toArray(new String[devIdList.size()]));
                devIDsForTypeDTO.setPageSize(pageSize);
                devIDsForTypeDTO.setPageNum(pageNum);
                PageVO<SpaceInfoVO> pageVO = queryDevService.findDevListPageByTypeID(devIDsForTypeDTO);
                List<SpaceInfoVO> subDevList = pageVO.getData();

                List<SpaceInfoVO> subDevList2 = new ArrayList<>();
                if (faileddevIdList != null){
                    devIDsForTypeDTO2.setTypeId(valve_typeid);
                    devIDsForTypeDTO2.setDevIds(faileddevIdList.toArray(new String[faileddevIdList.size()]));
                    devIDsForTypeDTO2.setPageSize(pageSize);
                    devIDsForTypeDTO2.setPageNum(pageNum);
                    PageVO<SpaceInfoVO> pageVO2 = queryDevService.findDevListPageByTypeID(devIDsForTypeDTO2);
                    subDevList2 = pageVO2.getData();
                }

                if (Objects.nonNull(subDevList)) {
                    subDevList.stream().map(vo -> {
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
                    //设置第一行  爆管点经纬度
                    Row xssfRow0 = sheet.createRow(0);
                    Cell cell1 = xssfRow0.createCell(0);
                    cell1.setCellValue("爆管点经纬度： "+dto.getPoint()[0]+" , "+dto.getPoint()[1]+" ; 爆管编号："+dto.getLineId());
                    cell1.setCellStyle(style3);
                    for (SpaceInfoVO spaceInfoVO : subDevList) {
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
                                    txt = "可关阀门";
                                }
                            }
                            text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
                            cell.setCellValue(text);
                            cell.setCellStyle(style2);
                        }
                    }
                }
                if (Objects.nonNull(subDevList2)) {
                    subDevList2.stream().map(vo -> {
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

                    for (SpaceInfoVO spaceInfoVO : subDevList2) {
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
                subDevList.clear();
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

            List<Long> firstdevIdList = new ArrayList<>() ;
            List<Long>faileddevIdList = new ArrayList<>() ;
            List<Long>seconddevIdList = new ArrayList<>() ;

            if (!((dto.getFirstDevIds() == null)||(dto.getFirstDevIds().length == 0))){
                firstdevIdList =  valvePOMapper.getDevIdsByCode(Arrays.asList(dto.getFirstDevIds()));
            }
            if (!((dto.getSecondDevIds() == null)||(dto.getSecondDevIds().length == 0))){
                seconddevIdList =  valvePOMapper.getDevIdsByCode(Arrays.asList(dto.getSecondDevIds()));
            }
            if (!((dto.getFailedDevIds() == null)||(dto.getFailedDevIds().length == 0))){
                faileddevIdList = valvePOMapper.getDevIdsByCode(Arrays.asList(dto.getFailedDevIds())) ;
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
