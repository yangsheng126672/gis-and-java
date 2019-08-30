package com.jdrx.gis.service.basic;

import com.jdrx.gis.beans.dto.basic.CriteriaDTO;
import com.jdrx.gis.beans.dto.basic.CriteriaQueryDTO;
import com.jdrx.gis.beans.entry.basic.GisAttrConditionRecord;
import com.jdrx.gis.dao.basic.GisAttrConditionRecordMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description: 属性的筛选条件
 * @Author: liaosijun
 * @Time: 2019/8/21 17:18
 */
@Service
public class AttCriteriaService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(AttCriteriaService.class);

	@Autowired
	private GisAttrConditionRecordMapper gisAttrConditionRecordMapper;

	/**
	 * 保存属性查询的筛选条件
	 * @param criteriaDTO
	 * @return
	 * @throws BizException
	 */
	public Boolean saveCriteriaRecord(CriteriaDTO criteriaDTO) throws BizException {
		try {
			GisAttrConditionRecord gisAttrConditionRecord = new GisAttrConditionRecord();
			BeanUtils.copyProperties(criteriaDTO, gisAttrConditionRecord);
			int affectRows = gisAttrConditionRecordMapper.insertSelective(gisAttrConditionRecord);
			if (affectRows > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("保存属性查询的筛选条件失败！");
		}

	}

	/**
	 * 根据ID删除属性查询的条件，这里物理删除
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public Boolean deleteCriteriaRecordById(Long id) throws BizException {
		try {
			int affectRows = gisAttrConditionRecordMapper.deleteByPrimaryKey(id);
			if (affectRows > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("根据ID={}删除记录失败！", id);
			throw new BizException("删除属性筛选条件失败！");
		}
	}

	/**
	 * 查询属性筛选条件记录
	 * @param criteriaQueryDTO
	 * @return
	 * @throws BizException
	 */
	public List<GisAttrConditionRecord> findConditionRecords(CriteriaQueryDTO criteriaQueryDTO) throws BizException {
		try {
			return gisAttrConditionRecordMapper.findConditionRecords(criteriaQueryDTO.getTypeId(), criteriaQueryDTO.getTplId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("查询属性筛选条件记录失败！");
		}
	}

	/**
	 * 更新属性筛选条件记录
	 * @param criteriaDTO
	 * @return
	 * @throws BizException
	 */
	public Boolean updateCriteriaRecord(CriteriaDTO criteriaDTO) throws BizException {
		try{
			GisAttrConditionRecord gisAttrConditionRecord = new GisAttrConditionRecord();
			BeanUtils.copyProperties(criteriaDTO, gisAttrConditionRecord);
			gisAttrConditionRecord.setUpdateAt(new Date());
			int affect = gisAttrConditionRecordMapper.updateByPrimaryKeySelective(gisAttrConditionRecord);
			if (affect > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("更新ID={}的属性筛选条件记录失败！", criteriaDTO.getId());
			throw new BizException("更新属性筛选条件记录失败！");
		}
	}
}