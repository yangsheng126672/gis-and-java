package com.jdrx.service.basic;

import com.jdrx.beans.entry.basic.ShareDevTypePO;
import com.jdrx.dao.basic.ShareDevTypePOMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 设备类型服务类
 * @Author: liaosijun
 * @Time: 2019/6/18 16:33
 */
@Service
public class ShareDevTypeService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ShareDevTypeService.class);

	@Autowired
	ShareDevTypePOMapper shareDevTypePOMapper;

	/**
	 * 查询图层分类为“其他”的子类型，只查询第二层即可，不继续往下递归
	 * @param typeId
	 * @return
	 * @throws BizException
	 */
	public List<ShareDevTypePO> selectByTypeId(Long typeId) throws BizException{
		Logger.debug("查询其他分类的第二层子类，PID = {}", typeId);
		return shareDevTypePOMapper.selectByTypeId(typeId);
	}
}