package com.jdrx.gis.service.basic;

import com.jdrx.gis.beans.dto.basic.DictTypeDTO;
import com.jdrx.gis.beans.entry.basic.DictTypePO;
import com.jdrx.gis.beans.vo.basic.DictTypeVO;
import com.jdrx.gis.dao.basic.DictTypePOMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description: 字典类型服务类
 * @Author: liaosijun
 * @Time: 2019/6/27 15:20
 */
@Service
public class DictTypeService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DictTypeService.class);

	@Autowired
	DictTypePOMapper dictTypePOMapper;

	/**
	 * 新增字典类型
	 * @param dictTypeDTO
	 * @return
	 * @throws BizException
	 */
	public Boolean addDictType(DictTypeDTO dictTypeDTO) throws BizException {
		try {
			DictTypePO dictTypePO = new DictTypePO();
			if (Objects.nonNull(dictTypeDTO)) {
				BeanUtils.copyProperties(dictTypeDTO, dictTypePO);
			}
			int affectedRows = dictTypePOMapper.insertSelective(dictTypePO);
			return affectedRows > 0 ? true : false;
		} catch (Exception e) {
			Logger.error("新增字典类型失败！", e.getMessage());
			throw new BizException("新增字典类型失败");
		}
	}

	/**
	 * 逻辑删除字典类型数据
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public Boolean delDictTypeById(Long id) throws BizException {
		try {
			int affectedRows = dictTypePOMapper.logicDeleteById(id);
			return affectedRows > 0 ? true : false;
		} catch (Exception e) {
			Logger.error("删除字典类型数据失败！", e.getMessage());
			throw new BizException("删除字典类型数据失败！");
		}
	}

	/**
	 * 更新字典类型数据
	 * @param dictTypeDTO
	 * @return
	 * @throws BizException
	 */
	public Boolean updateDictType(DictTypeDTO dictTypeDTO) throws BizException {
		try {
			DictTypePO dictTypePO = new DictTypePO();
			if (Objects.nonNull(dictTypeDTO)) {
				BeanUtils.copyProperties(dictTypeDTO, dictTypePO);
			}
			int affectedRows = dictTypePOMapper.updateByPrimaryKeySelective(dictTypePO);
			return affectedRows > 0 ? true : false;
		} catch (Exception e) {
			Logger.error("更新字典类型数据失败！", e.getMessage());
			throw new BizException("更新字典类型数据失败！");
		}
	}

	/**
	 * 根据id查类型
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public DictTypeVO getDictTypeById(Long id) throws BizException {
		try {
			DictTypePO dictTypePO = dictTypePOMapper.selectByPrimaryKey(id);
			DictTypeVO dictTypeVO = new DictTypeVO();
			BeanUtils.copyProperties(dictTypePO, dictTypeVO);
			return dictTypeVO;
		} catch (Exception e) {
			Logger.error("根据ID{}查类型失败！", id);
			throw new BizException("根据ID查类型失败！");
		}
	}
}