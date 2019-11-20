package com.jdrx.gis.service.analysis;

import com.google.common.base.Joiner;
import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.beans.entry.basic.GISDevExtPO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.beans.vo.basic.FeatureVO;
import com.jdrx.gis.beans.vo.query.FieldNameVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.service.basic.BasicDevQuery;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.gis.service.query.AttrQueryService;
import com.jdrx.gis.util.Neo4jUtil;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
            if(list.size() == 0){
                return null;
            }
            String devIds = Joiner.on(",").join(list);
            featureVOList = getGisDevExtPOMapper.findFeaturesByDevIds(devIds);

        }catch (Exception e) {
            e.printStackTrace();
            Logger.error("获取连通性分析结果失败!");
            throw new BizException("获取连通性分析结果失败!");
        }
        return featureVOList;
    }



}
