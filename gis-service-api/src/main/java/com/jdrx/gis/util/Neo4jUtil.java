package com.jdrx.gis.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.analysis.NodeDTO;
import com.jdrx.gis.beans.dto.dataManage.*;
import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.beans.vo.datamanage.NeoLineVO;
import com.jdrx.gis.beans.vo.datamanage.NeoPointVO;
import com.jdrx.gis.beans.vo.datamanage.REdge;
import com.jdrx.gis.beans.vo.datamanage.RNode;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * 创建管点node
     * @param lableType
     * lableType = gd or lableType = ljgd
     */
    public  void createNeoNodes(String lableType,String devIds){
        try {
            String nodeType;
            List<NeoPointVO> pointVOList = gisDevExtPOMapper.getPointDevExt(devIds);
            for (NeoPointVO pointVO:pointVOList){
                nodeType = GISConstants.NEO_NODE_NORMAL;
                if(pointVO.getName().contains("阀")){
                    nodeType = GISConstants.NEO_NODE_VALVE;
                }
                // 1.首先创线首节点
                RNode firstNode = new RNode();
                firstNode.setName(pointVO.getName());
                firstNode.setLabel(lableType);
                firstNode.addProperty("dev_id",pointVO.getDev_id());
                firstNode.addProperty("name",pointVO.getName());
                firstNode.addProperty("nodetype",nodeType);
                firstNode.addProperty("x",pointVO.getX());
                firstNode.addProperty("y",pointVO.getY());
                firstNode.addProperty("belong_to",pointVO.getBelong_to());
                createNode(firstNode);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

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
            String cypherSql = String.format("match(a:%s),(b:%s) where a.name=\"%s\" and b.name=\"%s\"  and a.belong_to =%d  and b.belong_to =%d create (a)-[r:%s %s]->(b)",
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

    /**
     * 线上加点
     * @param po
     * @param devId 管线设备id
     * @return
     */
    public  Boolean addNeoPointOnLine(GISDevExtPO po,String devId,String addDevIds){
        try {
            //创建管点
            createNeoNodes(GISConstants.NEO_POINT,po.getDevId());
            //删除管线
            deleteNeoLineById(GISConstants.NEO_LINE,devId);
            //创建新的两条管线
            createNeoLine(GISConstants.NEO_POINT,GISConstants.NEO_LINE,addDevIds);
            //创建逻辑管网
            //......

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据设备编码集合创建图数据库管网数据
     * @param devIds
     * @return
     */
    public  Boolean addNeoNets(String devIds){
        if (StringUtils.isEmpty(devIds)) {
            return false;
        }
        try {
            //创建管点数据
            createNeoNodes(GISConstants.NEO_POINT,devIds);
            //创建管线数据
            createNeoLine(GISConstants.NEO_POINT,GISConstants.NEO_LINE,devIds);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取节点详细信息
     * @return
     */
    public NodeDTO getValveNode(String code){
        NodeDTO node = new NodeDTO();
        String cypherSql = String.format("match (n:gd) where n.name = '%s' return n",code);
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
        List<Record> values = result.list();
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
     * 获取点连通的线
     * @param devId
     * @return
     */
    public List<String> getNodeConnectionLine(String devId){
        List<String> list = new ArrayList<>();
        try {
            String cypherSql = String.format("MATCH (n:%s{dev_id:%d})-[r]-(b) return r",GISConstants.NEO_POINT,devId);
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
            String cypherSql = String.format("MATCH (a)-[r:%s{relationID:%d}]-(b) return a,b",GISConstants.NEO_LINE,devId);
            System.out.println(cypherSql);
            StatementResult result = session.run(cypherSql);
            while (result.hasNext()) {
                Record record = result.next();
                list.add(record.get(0).asMap().get("dev_id").toString());
            }
            //查找关联的线
            String cypherSqlLine = String.format("match (a)-[re:%s{relationID:%d}]-(b)-[re2]-(c) return re2",GISConstants.NEO_LINE,devId);
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
            String cypherSql = String.format("match (a)-[b:%s]-[c] where a.dev_id=\"%s\"  delete b", GISConstants.NEO_LINE, devId);
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
            if (dto.getMap().get(GISConstants.GIS_ATTR_NAME).toString().contains("阀")) {
                nodeType = GISConstants.NEO_NODE_VALVE;
            } else if (dto.getMap().get(GISConstants.GIS_ATTR_NAME).toString().contains("水源")) {
                nodeType = GISConstants.NEO_NODE_WATER;
            } else {
                nodeType = GISConstants.NEO_NODE_NORMAL;
            }
            String cypherSql = String.format("create (a:%s{dev_id:\"%s\",name:\"%s\",nodetype:\"%s\",x:%f,y:%f,belong_to:%d})", GISConstants.NEO_POINT, devid,
                    name, nodeType, dto.getX(), dto.getY(), belongTo);
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
                gj = (Long) record.get(0).asMap().get("gj");
                cztype = record.get(0).asMap().get("cztype").toString();
            }
            //删除原有管线
            String cypherSql2 = String.format("match(a)-[rel:%s]-(b) where rel.relationID = \"%s\"  delete rel", GISConstants.NEO_LINE, dto.getLineDevId());
            session.run(cypherSql2);
            //创建管线1
            String cypherSql3 = String.format("match (a:gd{name:\"%s\"}),(b:gd{dev_id:\"%s\"}) create(a)-[c:gdline{cztype:\"%s\",gj:%d,relationID:\"%s\"," +
                    "belong_to:%d,name:\"%s\"}]->(b)", startCode, devid, cztype, gj, startRelationid, belongTo, code1);
            //创建管线2
            String cypherSql4 = String.format("match  (a:gd{dev_id:\"%s\"}),(b:gd{name:\"%s\"}) create(a)-[c:gdline{cztype:\"%s\",gj:%d,relationID:\"%s\"," +
                    "belong_to:%d,name:\"%s\"}]->(b)", devid, endCode, cztype, gj, endRelationid, belongTo, code2);
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
            String nodeType;
            String name = dto.getMapAttr().get(GISConstants.GIS_ATTR_CODE).toString();
            if (dto.getMapAttr().get(GISConstants.GIS_ATTR_NAME).toString().contains("阀")) {
                nodeType = GISConstants.NEO_NODE_VALVE;
            } else if (dto.getMapAttr().get(GISConstants.GIS_ATTR_NAME).toString().contains("水源")) {
                nodeType = GISConstants.NEO_NODE_WATER;
            } else {
                nodeType = GISConstants.NEO_NODE_NORMAL;
            }
            String cypherSql = String.format("create (a:%s{dev_id:\"%s\",name:\"%s\",nodetype:\"%s\",x:%f,y:%f,belong_to:%d})", GISConstants.NEO_POINT, devid, name, nodeType, dto.getX(), dto.getY(), belongTo);
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
            String cypherSql = String.format("match (a:gd{name:\"%s\"}),(b:gd{name:\"%s\"}) create(a)-[c:gdline{cztype:\"%s\",gj:%d,relationID:\"%s\"," +
                    "belong_to:%d,name:\"%s\"}]->(b)", startCode, endCode, cztype, gj, lineDevid, belongTo, code);
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
            String cypherSql = String.format("match(a)-[rel:gdline]-(b) where rel.name = \"%s\" set rel.cztype=\"%s\",rel.gj=%d",code,
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
            String cypherSql = String.format("match(a:gd) where a.dev_id = \"%s\" set a.x = %f,a.y=%f",dto.getDevId(),dto.getX(),dto.getY());
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
            String cypherSql = String.format("match (a:gd{dev_id:\"%s\"}),(b:gd{dev_id:\"%s\"}) create(a)-[c:gdline{cztype:\"%s\",gj:%d,relationID:\"%s\"," +
                    "belong_to:%d,name:\"%s\"}]->(b)",startDevid,endDevid,dto.getMaterial(),dto.getCaliber(),lineDevid,belongTo,code);
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
	 * @param filePath
	 */
	public void createNodesByCsvPoint(String filePath) {
		String cypherSql = "using periodic commit " + GISConstants.IMPORT_MAX_ROWS + " LOAD CSV WITH HEADERS FROM \" " + filePath + "\" AS point "
				+ "merge (:gd{dev_id:point." + GISConstants.DEV_ID + ", name : point." + GISConstants.POINT_CODE_CHN + ", nodetype : point.nodetype, x : point."
				+ GISConstants.X_CHN + ", y : point." + GISConstants.Y_CHN + ",  belong_to : point." + GISConstants.AUTH_ID_S + "})";
		session.run(cypherSql);
	}

	/**
	 * 创建管线
	 * @param filePath
	 */
	public void createNodesByCsvLine(String filePath) {
		String cypherSql = "using periodic commit " + GISConstants.IMPORT_MAX_ROWS + " LOAD CSV WITH HEADERS FROM \" " + filePath + "\" AS line "
				+ "match (s:gd{name:line." + GISConstants.LINE_START_CODE_CHN + ",belong_to:line." + GISConstants.AUTH_ID_S + "})"
				+ "match (e:gd{name:line." + GISConstants.LINE_END_CODE_CHN + ",belong_to:line." + GISConstants.AUTH_ID_S + "})"
				+ "merge (s) - [:jdline{relationID:line." + GISConstants.DEV_ID + ", name:line." + GISConstants.LINE_START_CODE_CHN
				+ "+\"-\"+line." + GISConstants.LINE_END_CODE_CHN + ", gj:line." + GISConstants.GIS_ATTR_CALIBER + "}]->(e)";
		session.run(cypherSql);
	}
}
