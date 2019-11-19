package com.jdrx.gis.service.dataManage;

import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.beans.vo.query.FieldNameVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.dao.query.DevQueryDAO;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.gis.service.query.AttrQueryService;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/6 0006 下午 1:25
 */
@Service
public class DataEditorService {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DataEditorService.class);

    @Autowired
    private DictConfig dictConfig;

    @Autowired
    private DictDetailService detailService;

    @Autowired
    ShareDevTypePOMapper shareDevTypePOMapper;

    @Autowired
    DevQueryDAO devQueryDAO;
    /**
     * 获取所有点类型
     * @return
     */
    public List<ShareDevTypePO> getAllPointType(){
        List<ShareDevTypePO> shareDevTypePOS = new ArrayList<>();
        try {
            String layerUrl = dictConfig.getPointType();
            List<DictDetailPO> detailPOs = detailService.findDetailsByTypeVal(layerUrl);
            if (detailPOs != null){
                String stringIds = detailPOs.get(0).getVal();
                shareDevTypePOS = shareDevTypePOMapper.findPointTypeByIds(stringIds);
            }

        }catch (Exception e){
            Logger.error(e.getMessage());
        }
        return shareDevTypePOS;
    }

    /**
     * 获取所有管线类型
     * @return
     */
    public List<ShareDevTypePO> getAllLineType(){
        List<ShareDevTypePO> shareDevTypePOS = new ArrayList<>();
        try {
            String layerUrl = dictConfig.getLineType();
            List<DictDetailPO> detailPOs = detailService.findDetailsByTypeVal(layerUrl);
            if (detailPOs != null){
                String stringIds = detailPOs.get(0).getVal();
                shareDevTypePOS = shareDevTypePOMapper.findLineTypeByIds(stringIds);
            }

        }catch (Exception e){
            Logger.error(e.getMessage());
        }
        return shareDevTypePOS;
    }

    /**
     * 通过设备id查詢最顶端父id，获取设备属性模板
     * @param typeId
     * @return
     */
    public List<FieldNameVO> getDevExtByTopPid(String typeId) throws BizException{
        try {
            return devQueryDAO.findFieldNamesByDevID(typeId);
        }catch (Exception e) {
            e.printStackTrace();
            throw new BizException(e);
         }

    }

    /**
     * 获取类型PID
     * @return
     */
    public Long getShareDevTypePid(Long id){
        Long pid = null;
        try {
            ShareDevTypePO shareDevTypePO  = shareDevTypePOMapper.getByPrimaryKey(id);
            pid = shareDevTypePO.getPId();
        }catch (Exception e){
            Logger.error(e.getMessage());
        }
        return pid;
    }

    /**
     * 创建逻辑管网
     */
    public void createLogicNets(){

    }
}
