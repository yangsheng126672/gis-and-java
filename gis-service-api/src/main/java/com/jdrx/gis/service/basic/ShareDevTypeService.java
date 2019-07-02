package com.jdrx.gis.service.basic;

import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
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
		try {
			return shareDevTypePOMapper.getByPrimaryKey(id);
		} catch (Exception e) {
			Logger.error("根据ID{}查share_dev_type信息失败！", id);
			throw new BizException("根据主键ID查设备类型表失败！");
		}
	}
	/**
	 * 根据设备类型ID查子类，只查询第二层即可，不继续往下递归
	 * @param typeId
	 * @return
	 * @throws BizException
	 */
	public List<ShareDevTypePO> findDevTypeListByTypeId(Long typeId) throws BizException{
		try {
			Logger.debug("查询设备类型ID的第二层子类，TYPE_ID = {}", typeId);
			return shareDevTypePOMapper.findDevTypeListByTypeId(typeId);
		} catch (Exception e) {
			Logger.error("查询设备类型ID{}的第二层子类失败!", typeId);
			throw new BizException("查询设备类型ID的第二层子类失败!");
		}
	}

	/**
	 * 根据父类型ID递归查询所有子类
	 * @param typeId
	 * @return
	 * @throws BizException
	 */
	public List<ShareDevTypePO> findAllDevTypeListByTypePId(Long typeId) throws BizException{
		try {
			Logger.debug("根据父类ID递归查询所有子类，PID = {}", typeId);
			return shareDevTypePOMapper.findAllDevTypeListByTypePId(typeId);
		} catch (Exception e) {
			Logger.error("根据父类ID{}递归查询所有子类失败!", typeId);
			throw new BizException("根据父类ID递归查询所有子类失败!");
		}
	}

	/**
	 * 根据当前类型ID，递归查询所有子类
	 * @param typeId
	 * @return
	 * @throws BizException
	 */
	public List<ShareDevTypePO> findAllDevTypeListByCurrTypeId(Long typeId) throws BizException {
		try {
			Logger.debug("根据当前类型ID，递归查询所有子类，ID = {}", typeId);
			return shareDevTypePOMapper.findAllDevTypeListByCurrTypeId(typeId);
		} catch (Exception e) {
			Logger.error("根据当前类型ID{}，递归查询所有子类失败！",  typeId);
			throw new BizException("根据当前类型ID，递归查询所有子类失败！");
		}
	}
}