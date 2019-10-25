package com.jdrx.gis.service.analysis;

import com.google.common.base.Joiner;
import com.jdrx.gis.beans.entry.basic.GISDevExtPO;
import com.jdrx.gis.beans.vo.basic.FeatureVO;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
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



    /**
     * 获取连通性分析结果
     * @param devId
     */
    public List<FeatureVO> getConnectivityAnalysis(Long devId) throws BizException {
        List<FeatureVO> featureVOList = new ArrayList<>();
        List<Long> list = new ArrayList<>();
        try {
            GISDevExtPO gisDevExtPO = gisDevExtPOMapper.getDevExtByDevId(devId);
            //判断设备类型是线的话，返回线两端的点设备和连通的线;如果是点，则返回连通的线
            if(gisDevExtPO.getGeom().contains("POINT")){
                list = netsAnalysisService.getNodeConnectionLine(gisDevExtPO.getDevId());
            }else{
                list = netsAnalysisService.getNodeConnectionPointAndLine(gisDevExtPO.getDevId());
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
