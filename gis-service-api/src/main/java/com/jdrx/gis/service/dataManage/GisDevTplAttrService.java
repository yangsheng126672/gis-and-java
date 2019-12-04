package com.jdrx.gis.service.dataManage;

import com.jdrx.gis.beans.entity.basic.GisDevTplAttrPO;
import com.jdrx.gis.dao.basic.GisDevTplAttrPOMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 模板配置
 * @Author: liaosijun
 * @Time: 2019/11/12 10:36
 */
@Service
public class GisDevTplAttrService {

	// 日志
	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(GisDevTplAttrService.class);

	@Autowired
	private GisDevTplAttrPOMapper gisDevTplAttrPOMapper;

	/**
	 * 保存模板
	 * @param gisDevTplAttrPO
	 * @throws BizException
	 */
	public void saveTplAttr(GisDevTplAttrPO gisDevTplAttrPO) throws BizException {
		try {
			gisDevTplAttrPOMapper.insertSelective(gisDevTplAttrPO);
		} catch (Exception e) {
			Logger.error("保存配置字段模板失败！");
			e.printStackTrace();
			throw new BizException(e);
		}
	}

	/**
	 * 批量插入模板
	 * @param tplAttrPOList
	 * @return
	 * @throws BizException
	 */
	public int batchInsertSelective(List<GisDevTplAttrPO> tplAttrPOList) throws BizException {
		try{
			if (Objects.isNull(tplAttrPOList) | tplAttrPOList.size() == 0) {
				throw new BizException("模板数据为空！");
			}
			return gisDevTplAttrPOMapper.batchInsertSelective(tplAttrPOList);
		} catch (Exception e) {
			Logger.error("批量插入模板数据失败！");
			e.printStackTrace();
			throw new BizException(e);
		}
	}

	/**
	 * 获取所有type_id对应的模板（列转行）
	 * @return
	 * @throws BizException
	 */
	public List<Map<String, String>> selectTypeIdDescMap() throws BizException {
		try {
			return gisDevTplAttrPOMapper.selectTypeIdDescMap();
		} catch (Exception e) {
			Logger.error("获取所有type_id对应的模板（列转行）失败！");
			e.printStackTrace();
			throw new BizException();
		}
	}

	/**
	 * 逻辑删除typeId对应模板
	 * @param typeId
	 * @return
	 * @throws BizException
	 */
	public int delByTypeId(Long typeId) throws BizException {
		try {
			return gisDevTplAttrPOMapper.delByTypeId(typeId);
		}  catch (Exception e) {
			Logger.error("逻辑删除typeId对应模板失败！");
			e.printStackTrace();
			throw new BizException();
		}
	}

	/**
	 * 根据模板名称查模板字段信息
	 * @param tplName
	 * @return
	 * @throws BizException
	 */
	public List<GisDevTplAttrPO> selectTplByTplName(String tplName) throws BizException {
		try {
			return gisDevTplAttrPOMapper.selectTplByTplName(tplName);
		}  catch (Exception e) {
			Logger.error("根据模板名称查模板字段信息失败！");
			e.printStackTrace();
			throw new BizException();
		}
	}
}
