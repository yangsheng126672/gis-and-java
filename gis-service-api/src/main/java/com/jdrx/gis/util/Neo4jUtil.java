package com.jdrx.gis.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdrx.gis.beans.vo.datamanage.NeoLineVO;
import com.jdrx.gis.beans.vo.datamanage.NeoPointVO;
import com.jdrx.gis.beans.vo.datamanage.REdge;
import com.jdrx.gis.beans.vo.datamanage.RNode;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/18 0018 下午 2:41
 */

public class Neo4jUtil {
    @Autowired
    private Session session;

    @Autowired
    GISDevExtPOMapper gisDevExtPOMapper;

    //节点类型  0普通节点  1阀门节点  2水源节点
    final private String NODE_FM = "1";

    final private String NODE_TYPE = "0";

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
    public void createNeoNodes(String lableType){
        try {
            String nodeType;
            List<NeoPointVO> pointVOList = gisDevExtPOMapper.getPointDevExt();
            for (NeoPointVO pointVO:pointVOList){
                nodeType = NODE_TYPE;
                if(pointVO.getName().contains("阀")){
                    nodeType = NODE_FM;
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
     * pointLable = gd or pointLable = ljgd
     * @param lineLable
     * lineLable = gdline or lineLable = ljgdline
     */
    public void createNeoLine(String pointLable,String lineLable){
        String startNodeNme;    //起点编码
        String endNodeName;     //终点编码
        Object dataInfo;
        try {
            List<NeoLineVO> lineVOList = gisDevExtPOMapper.getLineDevExt();
            for (NeoLineVO lineVO: lineVOList){
                dataInfo = lineVO.getData_info();
                JSONObject jb = JSONObject.parseObject(dataInfo.toString());
                Map<String,Object> map = (Map)jb;
                startNodeNme = map.get("qdbm").toString();
                endNodeName = map.get("zdbm").toString();

                REdge edge = new REdge();
                edge.setRelationID( lineVO.getDev_id());
                edge.setName(lineVO.getCode() );
                edge.setGj(lineVO.getCaliber() );
                edge.setCztype(lineVO.getMaterial() );
                edge.setBelong_to(lineVO.getBelong_to());

                edge.addProperty("relationID", lineVO.getDev_id());
                edge.addProperty("name",lineVO.getCode() );
                edge.addProperty("gj",lineVO.getCaliber() );
                edge.addProperty("cztype",lineVO.getMaterial() );
                edge.addProperty("belong_to",lineVO.getBelong_to() );

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
    public Boolean deleteNeoNodes(String lable){
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
    public Boolean deleteNeoLines(String lable){
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

}
