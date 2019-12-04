package com.jdrx.gis.service.basic;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jdrx.gis.beans.dto.basic.DictTypeDTO;
import com.jdrx.gis.beans.dto.basic.DictTypeQueryDTO;
import com.jdrx.gis.beans.entity.basic.DictTypePO;
import com.jdrx.gis.beans.vo.basic.DictTypeVO;
import com.jdrx.gis.dao.basic.DictTypePOMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.jdbc.beans.vo.PageVO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
			Boolean bool =  affectedRows > 0 ? true : false;
			if(!bool) {
				throw new BizException("新增字典类型失败");
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
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
			Boolean bool = affectedRows > 0 ? true : false;
			if(!bool) {
				throw new BizException("删除字典类型数据失败");
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
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
			dictTypePO.setUpdateAt(new Date());
			int affectedRows = dictTypePOMapper.updateByPrimaryKeySelective(dictTypePO);
			Boolean bool = affectedRows > 0 ? true : false;
			if (!bool) {
				throw new BizException("更新字典类型数据失败！");
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
			Logger.error("根据ID{}查类型失败！", id);
			throw new BizException("根据ID查类型失败！");
		}
	}

	/**
	 * 查询所有类型
	 * @return
	 * @throws BizException
	 */
	public PageVO<DictTypePO> findAllDictTypes(DictTypeQueryDTO dto) throws BizException {
		try {
			PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy());
			Page<DictTypePO> list = (Page<DictTypePO>) dictTypePOMapper.findAll();
			return new PageVO<>(list);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("查询所有字典类型失败！");
		}
	}
}