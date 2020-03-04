package com.jdrx.gis.dao.log.autoGenerate;

import com.jdrx.gis.beans.entity.log.ShareDevEditLog;

public interface ShareDevEditLogMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ShareDevEditLog record);

    int insertSelective(ShareDevEditLog record);

    ShareDevEditLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ShareDevEditLog record);

    int updateByPrimaryKey(ShareDevEditLog record);
}