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
	 *  获取某个类型信息
	 */
	public ShareDevTypePO getByPrimaryKey(Long id) throws BizException {
		return shareDevTypePOMapper.getByPrimaryKey(id);
	}
	/**
	 * 根据设备类型ID查子类，只查询第二层即可，不继续往下递归
	 * @param typeId
	 * @return
	 * @throws BizException
	 */
	public List<ShareDevTypePO> findDevTypeListByTypeId(Long typeId) throws BizException{
		Logger.debug("查询设备类型ID的第二层子类，TYPE_ID = {}", typeId);
		return shareDevTypePOMapper.findDevTypeListByTypeId(typeId);
	}

	/**
	 * 根据父类型ID递归查询所有子类
	 * @param typeId
	 * @return
	 * @throws BizException
	 */
	public List<ShareDevTypePO> selectAllDevByTypeId(Long typeId) throws BizException{
		Logger.debug("根据父类ID递归查询所有子类，PID = {}", typeId);
		return shareDevTypePOMapper.selectAllDevByTypeId(typeId);
	}

	/**
	 * 根据当前类型ID，递归查询所有子类
	 * @param typeId
	 * @return
	 * @throws BizException
	 */
	public List<ShareDevTypePO> selectAllDevByCurrTypeId(Long typeId) throws BizException {
		Logger.debug("根据当前类型ID，递归查询所有子类，ID = {}", typeId);
		return shareDevTypePOMapper.selectAllDevByCurrTypeId(typeId);
	}
}