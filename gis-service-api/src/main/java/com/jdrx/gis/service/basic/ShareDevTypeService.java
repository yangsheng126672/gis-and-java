package com.jdrx.gis.service.basic;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jdrx.gis.beans.dto.query.DevIDsAndTypeDTO;
import com.jdrx.gis.beans.entity.basic.ShareDevTypePO;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
			Logger.error("根据当前类型ID{}，递归查询所有子类失败！",  typeId);
			throw new BizException("根据当前类型ID，递归查询所有子类失败！");
		}
	}

	/**
	 * 根据多个类型ID（枝干）获取它们下属的子类型（叶子）
	 * @param typeIds
	 * @return
	 * @throws BizException
	 */
	public List<ShareDevTypePO> findLeafTypesByLimbTypeIds(List<Long> typeIds) throws BizException {
		try {
			Logger.debug("据多个类型ID（枝干）获取它们下属的子类型（叶子）, typeIds = {}", typeIds);
			return shareDevTypePOMapper.findLeafTypesByLimbTypeIds(typeIds);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("据多个类型ID（枝干）获取它们下属的子类型（叶子）, 查询失败！");
			throw new BizException("据多个类型ID（枝干）获取它们下属的子类型（叶子）, 查询失败！");
		}
	}

	/**
	 * 根据传入的DevIDs和typeId, 查它们的父类集合
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public List<ShareDevTypePO> findLeafTypesByDevIds(DevIDsAndTypeDTO dto) throws BizException {
		List<ShareDevTypePO> shareDevTypePOList;
		try{
			// 根据type_id查它所有子类型
			List<ShareDevTypePO> list1 = shareDevTypePOMapper.findAllDevTypeListByTypePId(dto.getTypeId());
			List<Long> typeIdsList1 = null;
			if (Objects.nonNull(list1)) {
				typeIdsList1 = list1.stream().map(ShareDevTypePO :: getId).collect(Collectors.toList());
			}


			String devStr = null;
			String[] devIds = dto.getDevIds();
			List<String> ids = Objects.nonNull(devIds) ? Arrays.asList(devIds) : Lists.newArrayList();
			if (Objects.nonNull(devIds) && devIds.length > 0) {
				devStr = Joiner.on(",").join(ids);
			}

			// 根据devIds 获取它们的父类IDs
			List<Long> typeIdsList2 = shareDevTypePOMapper.findTypeIdsByDevIds(devStr);

			if (Objects.nonNull(typeIdsList2)) {
				typeIdsList2.retainAll(typeIdsList1);
			}

			shareDevTypePOList = shareDevTypePOMapper.findDevTypeListByIds(typeIdsList2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("根据传入的DevIDs和typeId, 查它们的父类集合失败！");
		}
		return shareDevTypePOList;
	}
}