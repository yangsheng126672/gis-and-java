package com.jdrx.gis.beans.entity.neo4j;

import lombok.Data;

/**
 * @ClassName Neo4jGd
 * @Description TODO
 * @Author yangsheng
 * @Date 2020/2/24 16:23
 * @Version 1.0
 */
@Data
public class Neo4jGd {
    private String dev_id;

    private  String name;

    private String belong_to;

    private String nodetype;

    private String x;

    private String y;
}
