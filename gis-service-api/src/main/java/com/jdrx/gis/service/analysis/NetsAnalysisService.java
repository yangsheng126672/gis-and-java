package com.jdrx.gis.service.analysis;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdrx.gis.beans.dto.analysis.AnalysisResultDTO;
import com.jdrx.gis.beans.dto.analysis.NodeDTO;
import com.jdrx.gis.beans.dto.analysis.SecondAnalysisDTO;
import com.jdrx.gis.dao.basic.MeasurementPOMapper;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.sql.Connection;
import java.util.*;

/**
 * @Description
 * @Author lr
 * @Time 2019/7/19 0019 下午 2:17
 */

@Service
public class NetsAnalysisService {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(NetsAnalysisService.class);

    final static ObjectMapper mapper = new ObjectMapper();

    static {
        /**
         * 使用neo4j的session执行条件语句statement，一定要使用这个反序列化对象为json字符串
         * 下面的设置的作用是，比如对象属性字段name="李二明"，正常反序列化json为 == "name":"李二明"
         * 如果使用下面的设置后，反序列name就是 == name:"jdrx"
         * 而session执行语句create (:儿子{"name":"李二明","uuid":3330,"age":12,"height":"165cm"})会报错
         * 因此,......etc
         */
        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
    }

    @Autowired
    private Session session;
    @Autowired
    private MeasurementPOMapper measurementPOMapper;
//    @Autowired
//    private Connection connection;
    //逻辑管点标签
    final private String ljgdLable = "ljgd";
    //管点标签
    final private String gdLable = "gd";
    //管线标签
    final private String gdlineLable = "gdline";
    //逻辑管线标签
    final private String ljgdlineLable = "ljgdline";
    //节点类型  1位阀门
    final private String fmType = "1";

    /**
     * 获取关系中起始节点
     * @param relationID
     * @return
     */
    public List<Value> getNodesFromRel(Long relationID,String lineLable) {
        String cypherSql = String.format("MATCH (n)-[:%s{relationID: %d}]-(b) return n,b  ", lineLable,relationID);
        StatementResult result = session.run(cypherSql);
        List<Value> values = null;
        while (result.hasNext()) {
            Record record = result.next();
            values = record.values();
        }
        return values;
    }

    /**
     * 获取关联节点
     * @param nodeName
     * @return
     */
    public List<Value> getNextNode(String nodeName,String nodeLable) {
        String cypherSql = String.format("MATCH (n:%s{name:\"%s\"})-[r]-(b) return b  ", nodeLable,nodeName);
        StatementResult result = session.run(cypherSql);
        List<Value> values = new ArrayList<>();
        while (result.hasNext()) {
            Record record = result.next();
            values.addAll(record.values());
        }
        return values;
    }

    /**
     * 获取关联节点和边
     * @param nodeName
     * @return
     */
    public List<Record> getNextNodeAndPath(String nodeName,String nodeLable) {
        String cypherSql = String.format("MATCH (n:%s{name:\"%s\"})-[r]-(b) return b,r  ", nodeLable,nodeName);
        StatementResult result = session.run(cypherSql);
        List<Record> values = new ArrayList<>();
        values = result.list();
        return values;
    }

    /**
     * 查找水源列表
     * @return
     */
    public List<String> getWaterSourceList(){
        List<String>list = new ArrayList<>();
        String code = null;
        String cypherSql = String.format("match (n:gd) where n.nodetype = '2' return n");
        StatementResult result = session.run(cypherSql);
        while (result.hasNext()) {
            Record record = result.next();
            code = (record.get(0).asMap().get("name").toString());
            list.add(code);
        }
        return list;
    }

    /**
     * 查找一级关阀所有点
     * @param relationID
     * */

    public List<NodeDTO> findAllFamens(Long relationID){
        Long rid  = relationID;
        List<Value> values = getNodesFromRel(rid,gdlineLable);
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
                if (fmType.equals(nodetype) &&(!nodeDTOList.contains(nodeName))){
                    //是阀门节点，添加到待返回的阀门队列
                    NodeDTO dto = new NodeDTO();
                    dto.setDev_id(tmpValue.asNode().get("dev_id").asLong());
                    dto.setX(tmpValue.asNode().get("x").asDouble());
                    dto.setY(tmpValue.asNode().get("y").asDouble());
                    dto.setCode(nodeName);
                    nodeDTOList.add(dto);
                    continue;
                }else {
                    //不是阀门节点，继续遍历其关联节点
                    tmpList =getNextNode(nodeName,gdLable);
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
     * */
    public List<NodeDTO> findSecondAnalysisResult(String code){
        List<Value> values = getNextNode(code,ljgdLable);
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
                //判断nodetype是否为阀门类型
                nodeName = tmpValue.asNode().get("name").asString();
                if (nodeName.equals(code)){
                    continue;
                }
                nodetype = tmpValue.asNode().get("nodetype").asString();
                if (fmType.equals(nodetype) &&(!nodeDTOList.contains(nodeName))){
                    //是阀门节点，添加到待返回的阀门队列
                    NodeDTO dto = new NodeDTO();
                    dto.setDev_id(tmpValue.asNode().get("dev_id").asLong());
                    dto.setX(tmpValue.asNode().get("x").asDouble());
                    dto.setY(tmpValue.asNode().get("y").asDouble());
                    dto.setCode(nodeName);
                    nodeDTOList.add(dto);
                    continue;
                }else {
                    //不是阀门节点，继续遍历其关联节点
                    tmpList =getNextNode(nodeName,gdLable);
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
            List<String> waterSourceList = getWaterSourceList();
            //循环所有一级关阀的阀门列表,把每个阀门和所有水源地做连通分析和路径分析
            for (NodeDTO dto :list){
                String famenName = dto.getCode();
                //以阀门为起点遍历相邻节点
                tmpList =  getNextNode(famenName,ljgdLable);
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
                    if (list.contains(tmpNodeName)){
                        continue;
                    }else if ((waterSourceList.contains(tmpNodeName))&&(!famenList.contains(tmpNodeName))){
                        famenList.add(dto);
                        break;
                    }
                    //获取这个节点的相邻节点
                    tmpList = getNextNode(tmpNodeName,ljgdLable);
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
    public List<Long> findInfluenceArea(Long lineID,List<NodeDTO>dtoList) throws Exception{
        if (dtoList == null){
            return null;
        }
        List<String>famenList = new ArrayList<>();
        for (NodeDTO dto:dtoList){
            famenList.add(dto.getCode());
        }
        List<String>lookingNodes = new ArrayList<>();
        List<String>lookedNodes = new ArrayList<>();
        List<Long>influenceLines = new ArrayList<>();
        influenceLines.add(lineID);
        Long nextPathID ;
        String nextNodeName = null;
        List<Value> valueList = getNodesFromRel(lineID,gdlineLable);
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
                List<Record> nextNodeAndPath= getNextNodeAndPath(tmpNodeName,gdLable);
                for(Record record:nextNodeAndPath){
                    nextNodeName = record.get(0).asNode().asMap().get("name").toString();
                    nextPathID = Long.valueOf(record.get(1).asMap().get("relationID").toString());
                    if(!famenList.contains(nextNodeName)){
                        if (!lookingNodes.contains(nextNodeName)&&(!lookedNodes.contains(nextNodeName)))
                            //不是阀门，并且不在待访问和已访问列表，添加
                            iterator.add(nextNodeName);
                    }
                    if (!influenceLines.contains(Long.valueOf(nextPathID))){
                        influenceLines.add(Long.valueOf(nextPathID));
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
     * 获取爆管分析结果
     * @param id
     * @return
     * @throws Exception
     */
    public AnalysisResultDTO getAnalysisResult(Long id) throws Exception{
        AnalysisResultDTO analysisResultDTO = new AnalysisResultDTO();
        List<NodeDTO> fmlist_all = findAllFamens(id);
        List<NodeDTO> fmlist_final = findFinalFamens(fmlist_all);
        List<Long>idList = findInfluenceArea(id,fmlist_final);
        if (idList == null){
            return analysisResultDTO;
        }
        if (fmlist_final != null){
            analysisResultDTO.setFmlist(fmlist_final);
            String geom = measurementPOMapper.getExtendArea(idList);
            analysisResultDTO.setGeom(geom);
        }
        return analysisResultDTO;
    }

    /**
     * 获取二次关阀结果
     * @param secondAnalysisDTO
     * @return
     */
    public List<NodeDTO>getSecondAnalysisResult(SecondAnalysisDTO secondAnalysisDTO) throws BizException {
        List<String>dtoList = secondAnalysisDTO.getFealtureList();
        List<String>fmList = secondAnalysisDTO.getFmlist();
        List<NodeDTO> resultDtoList = new ArrayList<>();
       try {
           for(String string:dtoList){
               List<NodeDTO> tmpNodeList= findSecondAnalysisResult(string);
               for(NodeDTO innerDto:tmpNodeList){
                   if ((!fmList.contains(innerDto.getCode()))){
                        resultDtoList.add(innerDto);
                   }
               }
           }
       }catch (Exception e){
           Logger.error("二次关阀分析失败： "+e.getMessage());
           throw new BizException("二次关阀分析失败!");
       }
        System.out.println("二级关阀所有阀门：total= "+resultDtoList.size()+","+resultDtoList);
        return resultDtoList;
    }

}
