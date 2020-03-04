package com.jdrx.gis.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.analysis.NodeDTO;
import com.jdrx.gis.beans.dto.dataManage.*;
import com.jdrx.gis.beans.entity.basic.DictDetailPO;
import com.jdrx.gis.beans.entity.neo4j.Neo4jGdline;
import com.jdrx.gis.beans.vo.basic.NeoNodeVO;
import com.jdrx.gis.beans.vo.basic.NeoRelVO;
import com.jdrx.gis.beans.vo.datamanage.NeoLineVO;
import com.jdrx.gis.beans.vo.datamanage.REdge;
import com.jdrx.gis.beans.vo.datamanage.RNode;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.service.basic.DictDetailService;
import au.com.bytecode.opencsv.CSVWriter;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/18 0018 下午 2:41
 */

@Service
public class Neo4jUtil {
    @Autowired
    private Session session;

    @Autowired
    GISDevExtPOMapper gisDevExtPOMapper;

    @Autowired
    DictDetailService dictDetailService;

    @Autowired
    private DictConfig dictConfig;

    final static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PathConfig pathConfig;

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(Neo4jUtil.class);

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

//    /**
//     * 创建管点node
//     * @param lableType
//     * lableType = gd or lableType = ljgd
//     */
//    public  void createNeoNodes(String lableType,String devIds){
//        try {
//            String nodeType;
//            List<NeoPointVO> pointVOList = gisDevExtPOMapper.getPointDevExt(devIds);
//            for (NeoPointVO pointVO:pointVOList){
//                nodeType = GISConstants.NEO_NODE_NORMAL;
//                if(pointVO.getName().contains("阀")){
//                    nodeType = GISConstants.NEO_NODE_VALVE;
//                }
//                // 1.首先创线首节点
//                RNode firstNode = new RNode();
//                firstNode.setName(pointVO.getName());
//                firstNode.setLabel(lableType);
//                firstNode.addProperty("dev_id",pointVO.getDev_id());
//                firstNode.addProperty("name",pointVO.getName());
//                firstNode.addProperty("nodetype",nodeType);
//                firstNode.addProperty("x",pointVO.getX());
//                firstNode.addProperty("y",pointVO.getY());
//                firstNode.addProperty("belong_to",pointVO.getBelong_to());
//                createNode(firstNode);
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    /**
     * 创建管线
     * @param pointLable
     * @param lineLable
     */
    public  void createNeoLine(String pointLable,String lineLable,String devIds){
        String startNodeNme;    //起点编码
        String endNodeName;     //终点编码
        Object dataInfo;
        try {
            List<NeoLineVO> lineVOList = gisDevExtPOMapper.getLineDevExt(devIds);
            for (NeoLineVO lineVO: lineVOList){
                dataInfo = lineVO.getData_info();
                JSONObject jb = JSONObject.parseObject(dataInfo.toString());
                Map<String,Object> map = (Map)jb;
                startNodeNme = map.get(GISConstants.GIS_ATTR_QDBM).toString();
                endNodeName = map.get(GISConstants.GIS_ATTR_ZDBM).toString();

                REdge edge = new REdge();
                edge.setRelationID( lineVO.getDev_id());
                edge.setName(lineVO.getCode());
                edge.setGj(lineVO.getCaliber());
                edge.setCztype(lineVO.getMaterial());
                edge.setBelong_to(lineVO.getBelong_to());

                edge.addProperty("relationID", lineVO.getDev_id());
                edge.addProperty("name",lineVO.getCode());
                edge.addProperty("gj",lineVO.getCaliber());
                edge.addProperty("cztype",lineVO.getMaterial());
                edge.addProperty("belong_to",lineVO.getBelong_to());

                //创建管线
                createRelation(edge,startNodeNme,endNodeName,pointLable,lineLable);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 创建节点
     * @param rNode
     * @throws Exception
     */
    public void createNode(RNode rNode) throws Exception{
        RNode srcNode = queryNode(rNode);
        //查node是否已經存在了，不存在則創建
        if(srcNode == null){
            String propertiesString = mapper.writeValueAsString(rNode.getProperties());
            String cypherSql = String.format("create (:%s%s)", rNode.getLabel(), propertiesString);
            System.out.println(cypherSql);
            session.run(cypherSql);
            System.err.println("创建节点："+rNode.getLabel()+"成功！");
        }else{
            System.err.println("节点已存在，跳过创建");
        }
    }

    /**
     * 查询节点
     * @param rNode
     * @return
     */
    public RNode queryNode(RNode rNode) {
        RNode node = null;
        String cypherSql = String.format("match(n:%s) where  n.name =\"%s\" return n", rNode.getLabel(),rNode.getName());
        StatementResult result = session.run(cypherSql);
        if (result.hasNext()) {
            Record record = result.next();
            for (Value value : record.values()) {
                //结果里面只要类型为节点的值
                if ("NODE".equals(value.type().name())) {
                    Node noe4jNode = value.asNode();
                    node = new RNode();
                    node.setLabel(rNode.getLabel());
                    node.setProperties(noe4jNode.asMap());

                }
            }
        }
        return node;
    }

    /**
     * 创建关系
     * @param edge
     * @param startNodeName
     * @param endNodeName
     * @param nodeLable
     * @param lineLable
     * @throws Exception
     */

    public void createRelation(REdge edge,String startNodeName,String endNodeName,String nodeLable,String lineLable) throws Exception{
        boolean bl = getLineEixts(edge,lineLable);
        if(bl == false){
            String propertiesString = mapper.writeValueAsString(edge.getProperties());
            String cypherSql = String.format("match(a:%s),(b:%s) where a.name=\"%s\" and b.name=\"%s\"  and a.belong_to ='%d'  and b.belong_to ='%d' create (a)-[r:%s %s]->(b)",
                    nodeLable,nodeLable,startNodeName,endNodeName,edge.getBelong_to(),lineLable,propertiesString);
            System.out.println(cypherSql);
            session.run(cypherSql);
        }else{
            System.out.println("关系已存在，跳过创建");
        }
    }

    /**
     * 查询关系是否存在
     * @param edge
     * @param lable
     * @return
     */
    public Boolean getLineEixts(REdge edge,String lable){
        String cypherSql =String.format("match (n)-[r:%s]-(b) where r.name=\"%s\" return r",
                lable,edge.getName());
        StatementResult result = session.run(cypherSql);
        if(result.hasNext()){
            return true;
        }
        return false;
    }

    /**
     * 删除所有管点
     * @param lable
     * @return
     */
    public  Boolean deleteNeoAllNodes(String lable){
        try {
            String cypherSql =String.format("match (n:%s)  delete n",
                    lable);
            session.run(cypherSql);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除所有管点
     * @param lable
     * @return
     */
    public  Boolean deleteNeoLineById(String lable,String rid){
        try {
            String cypherSql =String.format("match (n)-[r:%s]-(b) where r.relationID='%s'  delete r",
                    lable,rid);
            session.run(cypherSql);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除所有管点
     * @param lable
     * @return
     */
    public  Boolean deleteNeoAllLines(String lable){
        try {
            String cypherSql =String.format("match (n)-[r:%s]-(b)  delete r",
                    lable);
            session.run(cypherSql);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

//    /**
//     * 线上加点
//     * @param po
//     * @param devId 管线设备id
//     * @return
//     */
//    public  Boolean addNeoPointOnLine(GISDevExtPO po,String devId,String addDevIds){
//        try {
//            //创建管点
//            createNeoNodes(GISConstants.NEO_POINT,po.getDevId());
//            //删除管线
//            deleteNeoLineById(GISConstants.NEO_LINE,devId);
//            //创建新的两条管线
//            createNeoLine(GISConstants.NEO_POINT,GISConstants.NEO_LINE,addDevIds);
//            //创建逻辑管网
//            //......
//
//            return true;
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 根据设备编码集合创建图数据库管网数据
//     * @param devIds
//     * @return
//     */
//    public  Boolean addNeoNets(String devIds){
//        if (StringUtils.isEmpty(devIds)) {
//            return false;
//        }
//        try {
//            //创建管点数据
//            createNeoNodes(GISConstants.NEO_POINT,devIds);
//            //创建管线数据
//            createNeoLine(GISConstants.NEO_POINT,GISConstants.NEO_LINE,devIds);
//            return true;
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//    }

    /**
     * 获取节点详细信息
     * @return
     */
    public NodeDTO getValveNode(String devId){
        NodeDTO node = new NodeDTO();
        String cypherSql = String.format("match (n:gd) where n.dev_id = '%s' return n",devId);
        StatementResult result = session.run(cypherSql);
        while (result.hasNext()) {
            Record record = result.next();
            node.setCode((record.get(0).asMap().get("name").toString()));
            node.setX(Double.valueOf(record.get(0).asMap().get("x").toString()));
            node.setY(Double.valueOf(record.get(0).asMap().get("y").toString()));
            node.setDev_id(record.get(0).asMap().get("dev_id").toString());
        }
        return node;
    }
    /**
     * 获取关系中起始节点
     * @param relationID
     * @return
     */
    public List<Value> getNodesFromRel(String relationID,String lineLable) {
        String cypherSql = String.format("MATCH (n)-[:%s{relationID: '%s'}]-(b) return n,b  ", lineLable,relationID);
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
     * @param dev_id
     * @return
     */
    public List<Value> getNextNode(String dev_id,String nodeLable) {
        String cypherSql = String.format("MATCH (n:%s{dev_id:\"%s\"})-[r]-(b) return b  ", nodeLable,dev_id);
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
     * @param devId
     * @return
     */
    public List<Record> getNextNodeAndPath(String devId,String nodeLable) {
        String cypherSql = String.format("MATCH (n:%s{dev_id:\"%s\"})-[r]-(b) return b,r  ", nodeLable,devId);
        StatementResult result = session.run(cypherSql);
        List<Record> values = result.list();
        return values;
    }

    /**
     * 查找水源列表
     * @return
     */
    public List<String> getWaterSourceList(String belongTo){
        List<String>list = new ArrayList<>();
        String code = null;
        String cypherSql = String.format("match (n:gd) where n.nodetype = \'%s\' and n.belong_to = \'%s\' return n",GISConstants.NEO_NODE_WATER,belongTo);
        StatementResult result = session.run(cypherSql);
        while (result.hasNext()) {
            Record record = result.next();
            code = (record.get(0).asMap().get("dev_id").toString());
            list.add(code);
        }
        return list;
    }

    /**
     * 获取点连通的线
     * @param devId
     * @return
     */
    public List<String> getNodeConnectionLine(String devId){
        List<String> list = new ArrayList<>();
        try {
            String cypherSql = String.format("MATCH (n:%s{dev_id:'%s'})-[r]-(b) return r",GISConstants.NEO_POINT,devId);
            StatementResult result = session.run(cypherSql);
            while (result.hasNext()) {
                Record record = result.next();
                list.add(record.get(0).asMap().get("relationID").toString());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取线连通的点和线
     * @param devId
     * @return
     */
    public List<String> getNodeConnectionPointAndLine(String devId){
        List<String> list = new ArrayList<>();
        try {
            //查找关联的点
            String cypherSql = String.format("MATCH (a)-[r:%s{relationID:'%s'}]-(b) return a,b",GISConstants.NEO_LINE,devId);
            System.out.println(cypherSql);
            StatementResult result = session.run(cypherSql);
            while (result.hasNext()) {
                Record record = result.next();
                list.add(record.get(0).asMap().get("dev_id").toString());
            }
            //查找关联的线
            String cypherSqlLine = String.format("match (a)-[re:%s{relationID:'%s'}]-(b)-[re2]-(c) return re2",GISConstants.NEO_LINE,devId);
            System.out.println(cypherSqlLine);
            StatementResult result1 = session.run(cypherSqlLine);
            while (result1.hasNext()) {
                Record record = result1.next();
                System.out.println(record.get(0).asMap().toString());
                list.add(record.get(0).asMap().get("relationID").toString());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据dev_id获取相邻节点的详细信息
     * @return
     */
    public NodeDTO getValveNodeByDevId(String devId){
        NodeDTO node = new NodeDTO();
        String cypherSql = String.format("MATCH (n)-[r:gdline]-(b) where n.dev_id='%s' RETURN b LIMIT 1",devId);
        StatementResult result = session.run(cypherSql);
        while (result.hasNext()) {
            Record record = result.next();
            node.setDev_id(record.get(0).asMap().get("dev_id").toString());
        }
        return node;
    }

    /**
     * 孤立点查询
     */
    public List<String> getLonelyPointsByDevIds(String devIds){
        List<String> list = new ArrayList<>();
        String cypherSql;
        StatementResult result;
        try {
            if ("".equals(devIds) || devIds == null) {
                cypherSql = String.format("match(a:%s)  where size((a)-[]-()) = 0 return a.dev_id", GISConstants.NEO_POINT);
                result  = session.run(cypherSql);
                while (result.hasNext()) {
                    Record record = result.next();
                    list.add(record.get(0).asString());
                }
            }else{
                cypherSql = String.format("match(a:%s) where a.dev_id in [%s]  match (a) where size((a)-[]-()) = 0 return a.dev_id", GISConstants.NEO_POINT, devIds);
                result = session.run(cypherSql);
                while (result.hasNext()) {
                    Record record = result.next();
                    list.add(record.get(0).asString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return list;
        }
    }

    /**
     * 孤立线查询
     */
    public List<String> getLonelyLinesByDevIds(String devIds) {
        List<String> list = new ArrayList<>();
        String cypherSql;
        StatementResult result;
        try {
            if ("".equals(devIds) || devIds == null) {
                cypherSql = String.format("match(a)-[rel:%s]-(b) where  size((a)-[]-())=1 and size((b)-[]-())=1 return rel.relationID", GISConstants.NEO_LINE);
                result = session.run(cypherSql);
                while (result.hasNext()) {
                    Record record = result.next();
                    list.add(record.get(0).asString());
                }
            } else {
                cypherSql = String.format("match(a)-[rel:%s]-(b) where rel.relationID in [%s] and size((a)-[]-())=1 and size((b)-[]-())=1 return rel.relationID", GISConstants.NEO_LINE, devIds);
                result = session.run(cypherSql);
                while (result.hasNext()) {
                    Record record = result.next();
                    list.add(record.get(0).asString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return list;
        }
    }


    /**
     * 根据devID删除某一管点
     * @param devId
     * @return
     */
    public  Boolean deletePointById(String devId){
        try {
            String cypherSql =String.format("match (a:%s) where a.dev_id=\"%s\"  delete a",GISConstants.NEO_POINT,devId);
            session.run(cypherSql);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据devID删除某一管线
     * @param devId
     * @return
     */
    public Boolean deleteLineById(String devId) {
        try {
            String cypherSql = String.format("match (a)-[b:%s]-(c) where b.relationID=\"%s\"  delete b", GISConstants.NEO_LINE, devId);
            session.run(cypherSql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 线上加点同步到neo4j数据库中
     * @param
     * @return
     */
    public Boolean saveToNeo4j(ShareAddedPointDTO dto, String devid, String startCode, String startRelationid, String code1, String endCode, String endRelationid, String code2, Long belongTo) {
        //参数依次为前端dto对象,创建的管点的devID,第一条管线起始管点的code,管线的devid,第一条管线编码,第二条管线的结束code,管线的devid,第二条管线的编码,类型
        try {
            //同步管点
            String nodeType;
            String name = dto.getMap().get(GISConstants.GIS_ATTR_CODE).toString();

            List<DictDetailPO> details = dictDetailService.findDetailsByTypeVal(dictConfig.getCloseableValveTypeIds());
            List<Long> typeIds = null;
            if (Objects.nonNull(details)) {
                typeIds = Lists.newArrayList();
                for (DictDetailPO dictDetailPO : details) {
                    typeIds.add(Long.parseLong(dictDetailPO.getVal()));
                }
            }
            if (typeIds.contains(dto.getTypeId())) {
                nodeType = GISConstants.NEO_NODE_VALVE;
            } else if (dto.getMap().get(GISConstants.GIS_ATTR_NAME).toString().contains("水源")) {
                nodeType = GISConstants.NEO_NODE_WATER;
            } else {
                nodeType = GISConstants.NEO_NODE_NORMAL;
            }
            String cypherSql = String.format("create (a:%s{dev_id:\"%s\",name:\"%s\",nodetype:\"%s\",x:'%f',y:'%f',belong_to:'%d',typeid:'%d'})", GISConstants.NEO_POINT, devid,
                    name, nodeType, dto.getX(), dto.getY(), belongTo,dto.getTypeId());
            session.run(cypherSql);
            //获取管线及管点的属性值
            String relationId = dto.getLineDevId();
            String cypherSql1 = String.format("match(a)-[rel:%s]-(b) where rel.relationID = \"%s\" return rel", GISConstants.NEO_LINE, dto.getLineDevId());
            session.run(cypherSql1);
            StatementResult result = session.run(cypherSql1);
            String cztype = null;
            Long gj = null;
            while (result.hasNext()) {
                Record record = result.next();
                gj = Long.valueOf(record.get(0).asMap().get("gj").toString());
                cztype = record.get(0).asMap().get("cztype").toString();
            }
            //删除原有管线
            String cypherSql2 = String.format("match(a)-[rel:%s]-(b) where rel.relationID = \"%s\"  delete rel", GISConstants.NEO_LINE, dto.getLineDevId());
            session.run(cypherSql2);
            //创建管线1
            String cypherSql3 = String.format("match (a:gd{name:\"%s\"}),(b:gd{dev_id:\"%s\"}) create(a)-[c:gdline{cztype:\"%s\",gj:'%d',relationID:\"%s\"," +
                    "belong_to:'%d',name:\"%s\"}]->(b)", startCode, devid, cztype, gj, startRelationid, belongTo, code1);
            //创建管线2
            String cypherSql4 = String.format("match  (a:gd{dev_id:\"%s\"}),(b:gd{name:\"%s\"}) create(a)-[c:gdline{cztype:\"%s\",gj:'%d',relationID:\"%s\"," +
                    "belong_to:'%d',name:\"%s\"}]->(b)", devid, endCode, cztype, gj, endRelationid, belongTo, code2);
            session.run(cypherSql3);
            session.run(cypherSql4);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 管网保存中管点信息的保存
     */
    public Boolean savePointToNeo4j(SharePointDTO dto,String devid,Long belongTo){
        try{
            List<DictDetailPO> details = dictDetailService.findDetailsByTypeVal(dictConfig.getCloseableValveTypeIds());
            List<Long> typeIds = null;
            if (Objects.nonNull(details)) {
                typeIds = Lists.newArrayList();
                for (DictDetailPO dictDetailPO : details) {
                    typeIds.add(Long.parseLong(dictDetailPO.getVal()));
                }
            }
            String nodeType;
            String name = dto.getMapAttr().get(GISConstants.GIS_ATTR_CODE).toString();
            if (typeIds.contains(dto.getTypeId())) {
                nodeType = GISConstants.NEO_NODE_VALVE;
            } else if (dto.getMapAttr().get(GISConstants.GIS_ATTR_NAME).toString().contains("水源")) {
                nodeType = GISConstants.NEO_NODE_WATER;
            } else {
                nodeType = GISConstants.NEO_NODE_NORMAL;
            }
            String cypherSql = String.format("create (a:%s{dev_id:\"%s\",name:\"%s\",nodetype:\"%s\",x:'%f',y:'%f',belong_to:'%d',typeid:'%d'})", GISConstants.NEO_POINT, devid, name, nodeType, dto.getX(), dto.getY(), belongTo,dto.getTypeId());
            session.run(cypherSql);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    /**
     * 管网保存中管线信息的保存
     */
    public Boolean saveLineToNeo4j(ShareLineDTO dto, String code, String startCode, String endCode, String lineDevid, Long belongTo) {
        try {
            String cztype = dto.getMaterial();
            Integer gj = dto.getCaliber();
            String cypherSql = String.format("match (a:gd{name:\"%s\"}),(b:gd{name:\"%s\"}) create(a)-[c:gdline{cztype:\"%s\",gj:'%d',relationID:\"%s\"," +
                    "belong_to:'%d',name:\"%s\"}]->(b)", startCode, endCode, cztype, gj, lineDevid, belongTo, code);
            session.run(cypherSql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 管线属性信息更改
     */
    public Boolean updateLineToNeo4j(String code,Map map){
        try{
            String cypherSql = String.format("match(a)-[rel:gdline]-(b) where rel.name = \"%s\" set rel.cztype=\"%s\",rel.gj='%d'",code,
                    map.get(GISConstants.GIS_ATTR_MATERIAL).toString(),Integer.parseInt(map.get(GISConstants.GIS_ATTR_CALIBER).toString()));
            session.run(cypherSql);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 管点移动同步
     */
    public Boolean updatePointMoveToNeo4j(MovePointDTO dto){
        try{
            //同步管点移动 更改管点移动的坐标属性信息
            String cypherSql = String.format("match(a:gd) where a.dev_id = \"%s\" set a.x = '%f',a.y='%f'",dto.getDevId(),dto.getX(),dto.getY());
            session.run(cypherSql);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 两点连接功能同步
     */
    public  Boolean createTwoPointsConnectionToNeo4j(ConnectPointsDTO dto ,String code,String startDevid,String endDevid,String lineDevid,Long belongTo){
        try{
            String cypherSql = String.format("match (a:gd{dev_id:\"%s\"}),(b:gd{dev_id:\"%s\"}) create(a)-[c:gdline{cztype:\"%s\",gj:'%d',relationID:\"%s\"," +
                    "belong_to:'%d',name:\"%s\"}]->(b)",startDevid,endDevid,dto.getMaterial(),dto.getCaliber(),lineDevid,belongTo,code);
            session.run(cypherSql);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

	/**
	 * 创建管点
	 *
	 * @param filePath
	 */
	public void createNodesByCsvPoint(String filePath) {
		StringBuilder sb = new StringBuilder();
		sb.append("using periodic commit ")
			.append(GISConstants.IMPORT_MAX_ROWS)
			.append(" LOAD CSV WITH HEADERS FROM \" ")
			.append(filePath)
			.append("\" AS point ")
			.append("merge (gd:gd{name:point.").append(GISConstants.POINT_CODE_CHN)
			.append("})")
			.append(" SET gd.nodetype=point.nodetype")
			.append(",gd.x=point.").append(GISConstants.X_CHN)
			.append(",gd.y=point.").append(GISConstants.Y_CHN)
			.append(",gd.dev_id=point.").append(GISConstants.DEV_ID)
			.append(",gd.belong_to=point.").append(GISConstants.AUTH_ID_S);
		session.run(String.valueOf(sb));
	}

	/**
	 * 创建管线
	 * @param filePath
	 */
	public void createNodesByCsvLine(String filePath) {
		StringBuilder sb = new StringBuilder();
		sb.append("using periodic commit ")
			.append(GISConstants.IMPORT_MAX_ROWS)
			.append(" LOAD CSV WITH HEADERS FROM \" ")
			.append(filePath)
			.append("\" AS line ")
			.append(" match (s:gd{name:line.").append(GISConstants.LINE_START_CODE_CHN).append("})")
			.append(" match (e:gd{name:line.").append(GISConstants.LINE_END_CODE_CHN).append("})")
			.append(" merge (s) - [gdline:gdline{name:line.").append(GISConstants.LINE_START_CODE_CHN)
			.append("+\"-\"+")
			.append("line.").append(GISConstants.LINE_END_CODE_CHN).append("}]->(e)")
			.append(" SET gdline.relationID=").append("line.").append(GISConstants.DEV_ID)
			.append(",gdline.gj=").append("line.").append(GISConstants.GIS_ATTR_CALIBER)
            .append(",gdline.belong_to=").append("line.").append(GISConstants.AUTH_ID_S)
			.append(",gdline.cztype=").append("line.").append(GISConstants.MATERIAL_CHN);
		session.run(String.valueOf(sb));
	}

    /**
     * neo4j删除功能同步  如果是线可直接删除  否则的话需要判断这个点  是孤立点的话就能删除 不是的话就不能删除
     * 该方法判断点的数量有多少
     */
    public int getPointAmount(String devId){
        Integer amount = null;
        String cypherSql = String.format("match(a:gd)-[rel:gdline]-(b:gd) where a.dev_id = '%s' return count(*)",devId);
        StatementResult result = session.run(cypherSql);
        while(result.hasNext()){
            Record record = result.next();
             amount = record.get(0).asInt();
        }
        return amount;

    }

    /**
     * 图数据库重复点删除 拓扑重建
     * 参数一为删除的点的dev_id,参数二为删除的点的编码,参数三为未删除的点的id,参数四为未删除的点的编码
     */
    public void deleteRepeatPoint(String deletePointId,String deleteCode,String id,String code){
        try{
            List<Neo4jGdline> list = new ArrayList<>();
            List<String> list1 = new ArrayList<>();
            String cypherSql = String.format("match(a:gd)-[rel:gdline]-(b:gd) where a.dev_id = '%s' return rel,b",deletePointId);
            StatementResult result = session.run(cypherSql);
            while(result.hasNext()){
                Record record = result.next();
                Map map1 = record.get(0).asMap();
                Map map2 = record.get(1).asMap();
                Neo4jGdline gdline = new Neo4jGdline();
                gdline.setBelong_to(map1.get("belong_to").toString());
                gdline.setCztype(map1.get("cztype").toString());
                gdline.setGj(map1.get("gj").toString());
                gdline.setName(map1.get("name").toString());
                gdline.setRelationID(map1.get("relationID").toString());
                list.add(gdline);
                list1.add(map2.get("dev_id").toString());
            }
            //删除与点相连的线
            for(Neo4jGdline line:list){
                String cypherSql1 =String.format("match(a)-[rel:gdline]-(b) where rel.relationID = '%s' delete rel",line.getRelationID());
                session.run(cypherSql1);
            }
            //删除重复点
            String cypherSql2 = String.format("match (a:gd) where a.dev_id= '%s'  delete a",deletePointId);
            session.run(cypherSql2);
            for(int i=0;i<list.size();i++){
                //将之前的管线编码替换为新的管线编码
                String lineCode = list.get(i).getName().replace(deleteCode,code);
             String cypherSql3 = String.format("match (a:gd{dev_id:'%s'}),(b:gd{dev_id:'%s'}) create(a)-[c:gdline{cztype:'%s',gj:'%s',relationID:'%s'," +
                     "belong_to:'%s',name:'%s'}]->(b)",id,list1.get(i),list.get(i).getCztype(),list.get(i).getGj(),list.get(i).getRelationID(),list.get(i).getBelong_to(),lineCode) ;
             session.run(cypherSql3);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
	/**
     * 更新图数据库管点类型
     * @param devId
     * @param typeId
     * @param lable
     * @return
     */
    public boolean updateNodeType(String devId,Long typeId,String lable){
        try {
            String nodeType;
            List<DictDetailPO> details = dictDetailService.findDetailsByTypeVal(dictConfig.getCloseableValveTypeIds());
            List<Long> typeIds = null;
            if (Objects.nonNull(details)) {
                typeIds = Lists.newArrayList();
                for (DictDetailPO dictDetailPO : details) {
                    typeIds.add(Long.parseLong(dictDetailPO.getVal()));
                }
            }
            if (typeIds.contains(typeId)) {
                nodeType = GISConstants.NEO_NODE_VALVE;
            } else {
                nodeType = GISConstants.NEO_NODE_NORMAL;
            }
            String cypherSql = String.format("match (n:%s{dev_id:\"%s\"}) set n.nodetype=\"%s\" return n", lable,devId,nodeType);
            session.run(cypherSql);

            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建逻辑管网
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean mergeLJNets(){
        String name = null;
        String startCode1;
        String startCode2;
        String endCode1;
        String endCode2;
        String code;
        String line1;
        String line2;
        List<String> typeNameList = new ArrayList<>();
        List<Value> values = new ArrayList<>();
        try {
            //删除原来的逻辑管网数据
            deleteNeoNets(GISConstants.NEO_POINT_LJ,GISConstants.NEO_LINE_LJ);
            //创建最新的逻辑管网
            createNeoNets(GISConstants.NEO_POINT_LJ,GISConstants.NEO_LINE_LJ);
            //获取爆管阀门类型
            List<DictDetailPO> details = dictDetailService.findDetailsByTypeVal(dictConfig.getCloseableValveTypeIds());
            for (DictDetailPO dictDetailPO : details) {
                typeNameList.add(dictDetailPO.getName());
            }
            List<NeoNodeVO> points = gisDevExtPOMapper.getAllPoints();
            for (NeoNodeVO po : points) {
                name = po.getName();
                code = po.getCode();
                //判断当前节点是否是阀门，如果是阀门，跳过
                if (typeNameList.contains(name)) {
                    continue;
                }
                //查询这个节点附带的关系有几条  如果是两条，合并这两条关系
                values = getAllRelFromNode(GISConstants.NEO_POINT_LJ, code);
                if (values.size() == 2) {
                    line1 = values.get(0).asMap().get(GISConstants.NEO_POINT_NAME).toString();
                    line2 = values.get(1).asMap().get(GISConstants.NEO_POINT_NAME).toString();
                    startCode1 = line1.substring(0,line1.indexOf("-"));
                    endCode1 = line1.substring(line1.indexOf("-")+1);
                    startCode2 = line2.substring(0,line2.indexOf("-"));
                    endCode2 = line2.substring(line2.indexOf("-")+1);

                    String repeatNM = null;
                    StringBuffer nm_new = null;
                    RNode firstNode = new RNode();
                    RNode secondNode = new RNode();
                    RNode commonNode = new RNode();
                    //设置管点lable
                    firstNode.setLabel(GISConstants.NEO_POINT_LJ);
                    secondNode.setLabel(GISConstants.NEO_POINT_LJ);
                    commonNode.setLabel(GISConstants.NEO_POINT_LJ);
                    //创建关系并设置属性
                    REdge edge = new REdge();
                    edge.setLabel(GISConstants.NEO_LINE_LJ);
                    //判断重复的节点
                    if (startCode1.equals(startCode2)) {
                        repeatNM = startCode1;
                        nm_new = new StringBuffer().append(endCode1)
                                .append("-")
                                .append(endCode2);
                        edge.setName(String.valueOf(nm_new));
                        firstNode.setName(endCode1);
                        secondNode.setName(endCode2);

                    } else if (startCode1.equals(endCode2)) {
                        repeatNM = startCode1;
                        nm_new = new StringBuffer().append(endCode1)
                                .append("-")
                                .append(startCode2);
                        edge.setName(String.valueOf(nm_new));
                        firstNode.setName(endCode1);
                        secondNode.setName(startCode2);
                    } else if (endCode1.equals(startCode2)) {
                        repeatNM = endCode1;
                        nm_new = new StringBuffer().append(startCode1)
                                .append("-")
                                .append(endCode2);
                        edge.setName(String.valueOf(nm_new));
                        firstNode.setName(startCode1);
                        secondNode.setName(endCode2);

                    } else if (endCode1.equals(endCode2)) {
                        repeatNM = endCode1;
                        nm_new = new StringBuffer().append(startCode1)
                                .append("-")
                                .append(startCode2);
                        edge.setName(String.valueOf(nm_new));
                        firstNode.setName(startCode1);
                        secondNode.setName(startCode2);
                    }
                    //设置edg边
                    edge.addProperty(GISConstants.NEO_LINE_NAME, edge.getName());
                    //合并管线
                    commonNode.setName(repeatNM);
                    mergeRelation(commonNode, firstNode, secondNode, edge);
                }
            }
            return true;

        }catch (Exception e){
            Logger.info(e.getMessage());
            return false;
        }
    }

    /**
     * 获取节点附属所有关系
     * @param lable
     * @param name
     * @return
     */
    public List<Value> getAllRelFromNode(String lable , String name){
        String cypherSql = String.format("MATCH (n:%s{name:\"%s\"})-[r]-(b) return r  ", lable,name);
        StatementResult result = session.run(cypherSql);
        List<Value> values = new ArrayList<>();
        while (result.hasNext()){
            Record record = result.next();
            values.addAll(record.values());
        }
        return values;
    }

    /**
     * 根据公共节点，合并两边关系
     * @param commonNode
     * @param firstNode
     * @param secondNode
     * @param edge
     * 先删除两边关系，再删除节点，最后连接新的起始节点
     */
    public void mergeRelation(RNode commonNode,RNode firstNode,RNode secondNode,REdge edge){
        try {
            //删除两边关系
            String cypherSql = String.format("match (n:%s{name:\"%s\"})-[r]-(b) delete r" ,commonNode.getLabel(),commonNode.getName());
            session.run(cypherSql);
            //删除公共节点
            String delSql = String.format("match (n:%s{name:\"%s\"}) delete n",commonNode.getLabel(),commonNode.getName());
            session.run(delSql);
            //创建新的起始节点关系
            String addLineSql = String.format("MATCH (n:%s{name:\"%s\"}),(b:%s{name:\"%s\"})\n" +
                    "CREATE (n)-[r:%s]->(b) RETURN r",firstNode.getLabel(),firstNode.getName(),secondNode.getLabel(),secondNode.getName(),edge.getLabel()+mapper.writeValueAsString(edge.getProperties()));
            StatementResult result = session.run(addLineSql);
            if (!result.hasNext()){
                System.out.println(firstNode.getName()+"-"+secondNode.getName()+"关系创建失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 删除图数据库管网
     * @param nodeLable
     * @param lineLable
     * @return
     */
    public Boolean deleteNeoNets(String nodeLable,String lineLable){
        try {
            //先删除管线
            String delSql = String.format("MATCH p=()-[r:%s]->() delete r",lineLable);
            StatementResult result = session.run(delSql);
            //再删除管点
            String delNodeSql = String.format("MATCH (n:%s) delete n",nodeLable);
            StatementResult result2 = session.run(delNodeSql);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 创建图数据库管网
     * @param nodeLable
     * @param lineLable
     * @return
     */
    public Boolean createNeoNets(String nodeLable,String lineLable){
        List<Map<String,Object>> pointMapList = new ArrayList<>();
        Map<String,Object> pointMap;
        List<Map<String,Object>> lineMapList = new ArrayList<>();
        Map<String,Object> lineMap;
        try {
            //创建管点csv
            List<DictDetailPO> dictDetailPOS = dictDetailService.findDetailsByTypeVal(dictConfig.getCloseableValveTypeIds());
            List<NeoNodeVO> points = gisDevExtPOMapper.getAllPoints();
            for (NeoNodeVO po: points){
                pointMap = new HashMap<>();
                pointMap.put(GISConstants.NEO_POINT_NAME,po.getCode());
                pointMap.put(GISConstants.NEO_POINT_DEVID,po.getDevId());
                pointMap.put(GISConstants.NEO_POINT_X,po.getX());
                pointMap.put(GISConstants.NEO_POINT_Y,po.getY());
                pointMap.put(GISConstants.NODE_TYPE,getNodeType(po.getName(),dictDetailPOS));
                pointMap.put(GISConstants.NEO_BELONGTO,po.getBelongTo());
                pointMapList.add(pointMap);
            }
            String pointFilePath = writeNeoCSV(pointMapList,"point");
            createNodesByCsv(pointFilePath,nodeLable);

            List<NeoRelVO> lines = gisDevExtPOMapper.getAllLines();
            for (NeoRelVO  relVO:lines){
                lineMap = new HashMap<>();
                lineMap.put("startCode",relVO.getStartCode());
                lineMap.put("endCode",relVO.getEndCode());
                lineMap.put(GISConstants.NEO_LINE_ID,relVO.getRelationId());
                lineMap.put(GISConstants.NEO_BELONGTO,relVO.getBelongTo());
                lineMap.put(GISConstants.NEO_LINE_CALIBER,relVO.getCaliber());
                lineMap.put(GISConstants.NEO_LINE_MATERIAL,relVO.getMaterial());
                lineMapList.add(lineMap);
            }
            String lineFilePath = writeNeoCSV(lineMapList,"line");
            createLineByCsv(lineFilePath,nodeLable,lineLable);

        }catch (Exception e){
            Logger.error(e.getMessage());
        }
        return  true;
    }

    /**
     * 创建上传图数据库的csv
     * @param pointDataList
     * @param title
     * @return
     */
    public String writeNeoCSV(List<Map<String,Object>>pointDataList,String title){
        String path = "";
        try {
            Set<String> headerSet = Sets.newHashSet();
            if (Objects.nonNull(pointDataList) && pointDataList.size() > 0) {
                Set<String> headers = pointDataList.get(0).keySet();
                headerSet.addAll(headers);
            }
            String filePath = pathConfig.getDownloadPath() + "/" + title + ".csv";
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8),
                    CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
            writer.writeNext(StringUtils.toStringArray(headerSet));
            int total = pointDataList.size();
            int pageSize = GISConstants.EXPORT_PAGESIZE;
            int pageTotal;
            if (total <= pageSize) {
                pageTotal = 1;
            } else {
                pageTotal = total / pageSize == 0 ? total / pageSize : total / pageSize + 1;
            }
            int pageNum = 0;
            while (pageTotal-- > 0) {
                List<Map<String, Object>> subList = pointDataList.subList(pageNum * pageSize,
                        (pageNum + 1) * pageSize > total ? total : (pageNum + 1) * pageSize);
                for (Map<String, Object> map : subList) {
                    Iterator<String> iterator = headerSet.iterator();
                    String[] rows = new String[map.size()];
                    for (int i = 0; i < headerSet.size(); i++) {
                        String header = iterator.hasNext() ? iterator.next() : "";
                        String txt = Objects.nonNull(map.get(header)) ? String.valueOf(map.get(header)) : "";
                        rows[i] = txt;
                    }
                    writer.writeNext(rows);
                }
                pageNum++;
            }
            writer.flush();
            writer.close();
            path = JavaFileToFormUpload.send(pathConfig.getUploadFileUrl(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 根据管点名称获取管点类型
     * @param name
     * @param dictDetailPOS
     * @return
     */
   public String getNodeType(String name,List<DictDetailPO> dictDetailPOS){
        String type = GISConstants.NEO_NODE_NORMAL;
        try {
            List<String> typeNameList = new ArrayList<>();
            for(DictDetailPO po:dictDetailPOS){
                typeNameList.add(po.getName());
            }
            if (typeNameList.contains(name)){
                type = GISConstants.NEO_NODE_VALVE;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return type;
   }

    /**
     * 创建管点
     * @param filePath
     * @param lable
     */
    public void createNodesByCsv(String filePath,String lable) {
        StringBuilder sb = new StringBuilder();
        sb.append("using periodic commit ")
                .append(GISConstants.IMPORT_MAX_ROWS)
                .append(" LOAD CSV WITH HEADERS FROM \" ")
                .append(filePath)
                .append("\" AS point ")
                .append("merge (n:").append(lable)
                .append("{name:point.name})")
                .append(" SET n.dev_id=point.dev_id")
                .append(",n.nodetype=point.nodetype")
                .append(",n.x=point.x")
                .append(",n.y=point.y")
                .append(",n.belong_to=point.belong_to");
        session.run(String.valueOf(sb));
    }

    /**
     * 创建管线
     * @param filePath
     * @param nodeLable
     * @param lineLable
     */
    public void createLineByCsv(String filePath,String nodeLable,String lineLable) {
        StringBuilder sb = new StringBuilder();
        sb.append("using periodic commit ")
                .append(GISConstants.IMPORT_MAX_ROWS)
                .append(" LOAD CSV WITH HEADERS FROM \" ")
                .append(filePath)
                .append("\" AS line ")
                .append(" match (s:").append(nodeLable)
                .append("{name:line.startCode})")
                .append(" match (e:").append(nodeLable)
                .append("{name:line.endCode})")
                .append(" merge (s) - [r:").append(lineLable)
                .append("{name:line.startCode")
                .append("+\"-\"+")
                .append("line.endCode").append("}]->(e)")
                .append(" SET r.relationID=line.relationID")
                .append(",r.belong_to = line.belong_to")
                .append(",r.gj=").append("line.").append(GISConstants.GIS_ATTR_CALIBER)
                .append(",r.cztype=").append("line.").append(GISConstants.GIS_ATTR_MATERIAL);
        session.run(String.valueOf(sb));
    }
}
