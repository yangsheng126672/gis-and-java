package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.entry.basic.GisAttrConditionRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GisAttrConditionRecordMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GisAttrConditionRecord record);

    int insertSelective(GisAttrConditionRecord record);

    GisAttrConditionRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GisAttrConditionRecord record);

    int updateByPrimaryKey(GisAttrConditionRecord record);

	/**
	 * 查属性查询的筛选条件记录
	 * @param typeId
	 * @param tplId
	 * @param fieldName
	 * @return
	 */
	List<GisAttrConditionRecord> findConditionRecords(@Param("typeId") Long typeId, @Param("tplId") Long tplId, @Param("fieldName") String fieldName);

}