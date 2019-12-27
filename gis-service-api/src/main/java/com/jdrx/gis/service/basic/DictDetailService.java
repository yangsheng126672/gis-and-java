package com.jdrx.gis.service.basic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jdrx.gis.beans.dto.basic.DictDetailDTO;
import com.jdrx.gis.beans.entity.basic.DictDetailPO;
import com.jdrx.gis.beans.vo.basic.DictDetailVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.dao.basic.DictDetailPOMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 数据字典服务
 * @Author: liaosijun
 * @Time: 2019/6/27 13:08
 */
@Service
public class DictDetailService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DictDetailService.class);

	@Autowired
	private DictDetailPOMapper dictDetailPOMapper;

	@Autowired
	private DictConfig dictConfig;

	/**
	 * 根据dict_type中val的值查询配置的dict_detail列表
	 * @param val dict_type中的val字段对应的值
	 * @return
	 * @throws BizException
	 */
	public List<DictDetailPO> findDetailsByTypeVal(String val) throws BizException {
		try {
			return dictDetailPOMapper.selectByVal(val);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("根据dict_type中val的值查询配置的dict_detail列表失败！", e.getMessage());
			throw new BizException("根据dict_type中val的值查询配置的dict_detail列表失败！");
		}
	}

	/**
	 * 新增字典数据
	 * @param dictDetailDTO
	 * @return
	 * @throws BizException
	 */
	public Boolean addDictDetail(DictDetailDTO dictDetailDTO) throws BizException {
		try {
			DictDetailPO dictDetailPO = new DictDetailPO();
			if (Objects.nonNull(dictDetailDTO)) {
				BeanUtils.copyProperties(dictDetailDTO, dictDetailPO);
			}
			int affectedRows = dictDetailPOMapper.insertSelective(dictDetailPO);
			Boolean bool =  affectedRows > 0 ? true : false;
			if (!bool) {
				throw new BizException("新增字典数据失败！");
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("新增字典数据失败！", e.getMessage());
			throw new BizException("新增字典数据失败！");
		}
	}

	/**
	 * 逻辑删除字典详情数据
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public Boolean delDictDetailById(Long id) throws BizException {
		try {
			int affectedRows = dictDetailPOMapper.logicDeleteById(id);
			Boolean bool = affectedRows > 0 ? true : false;
			if (!bool) {
				throw new BizException("删除字典数据失败！");
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("删除字典数据失败！", e.getMessage());
			throw new BizException("删除字典数据失败！");
		}
	}

	/**
	 * 更新字典数据
	 * @param dictDetailDTO
	 * @return
	 * @throws BizException
	 */
	public Boolean updateDictType(DictDetailDTO dictDetailDTO) throws BizException {
		try {
			DictDetailPO dictDetailPO = new DictDetailPO();
			if (Objects.nonNull(dictDetailDTO)) {
				BeanUtils.copyProperties(dictDetailDTO, dictDetailPO);
			}
			dictDetailPO.setUpdateAt(new Date());
			int affectedRows = dictDetailPOMapper.updateByPrimaryKeySelective(dictDetailPO);
			Boolean bool = affectedRows > 0 ? true : false;
			if (!bool) {
				throw new BizException("更新字典数据失败！");
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("更新字典数据失败！", e.getMessage());
			throw new BizException("更新字典数据失败！");
		}
	}

	/**
	 * 根据id查数据
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public DictDetailVO getDictTypeById(Long id) throws BizException {
		try {
			DictDetailPO dictDetailPO = dictDetailPOMapper.selectByPrimaryKey(id);
			DictDetailVO dictDetailVO = new DictDetailVO();
			BeanUtils.copyProperties(dictDetailPO, dictDetailVO);
			return dictDetailVO;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("根据ID{}查询失败！", id);
			throw new BizException("根据ID查询失败！");
		}
	}

	/**
	 * 根据设备类型ID集查询它在数据字典中配置的url列表
	 * @param typeIds
	 * @return
	 * @throws BizException
	 */
	public List<Map<String,String>> findLayerUrlListByTypeIds(Long[] typeIds,int typeStr) throws BizException {
		String layerUrl = null;
		try {
			if (1 == typeStr){
				layerUrl = dictConfig.getLayerUrl();
			}else if(2 == typeStr){
				layerUrl = dictConfig.getIconUrl();
			}
			List<DictDetailPO> detailPOs = findDetailsByTypeVal(layerUrl);
			List<Map<String, String>> urlList = Lists.newArrayList();
			if (Objects.nonNull(detailPOs) && detailPOs.size() > 0) {
				detailPOs.stream().forEach(dictDetailPO -> {
					String val = dictDetailPO.getVal();
					Map<String, String> map = Maps.newHashMap();
					String[] vals = val.split(",");
					map.put(vals[0], vals[1]);
					System.out.println(vals[0] + "," + vals[1]);
					if (Objects.nonNull(typeIds) && typeIds.length > 0) {
						for (int i = 0; i < typeIds.length; i ++) {
							if (map.containsKey(String.valueOf(typeIds[i]))) {
								System.out.println("map=" + map);
								urlList.add(map);
								break;
							}
						}
					}
				});
			}
			return urlList;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("根据设备类型ID集查询它在数据字典中配置的url列表失败！typeIds = {}", typeIds);
			throw new BizException("根据设备类型ID集查询它在数据字典中配置的url列表失败！");
		}
	}

	/**
	 * 根据typeId查字典列表
	 * @param typeId
	 * @return
	 * @throws BizException
	 */
	public List<DictDetailPO> findDictDetailListByTypeId(Long typeId) throws BizException {
		try{
			return dictDetailPOMapper.findDictDetailListByTypeId(typeId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("根据typeId查字典数据失败！");
		}
	}

	/**
	 * 根据数据字典的配置，获取配置项的下拉属性， 配置dict_detail中name对应模板字段英文值，val对应需要查询的
	 * 下拉框配置的值
	 * @return
	 * @throws BizException
	 */
	public Map<String, List<DictDetailPO>> findOptionsByConfig() throws BizException {
		String devEditOptions = dictConfig.getDevEditOptions();
		if (StringUtils.isEmpty(devEditOptions)) {
			throw new BizException("未配置需要查询的下拉框字典值！");
		}
		Map<String, List<DictDetailPO>> map = Maps.newHashMap();
		List<DictDetailPO> detailsByTypeVal = findDetailsByTypeVal(devEditOptions);
		if (Objects.nonNull(detailsByTypeVal) && detailsByTypeVal.size() > 0) {
			detailsByTypeVal.forEach(detail -> {
				List<DictDetailPO> detailPOS = null;
				try {
					detailPOS = findDetailsByTypeVal(detail.getVal());
				} catch (BizException e) {
					e.printStackTrace();
				}
				map.put(detail.getName(), detailPOS);
			});
		}
		return map;
	}
}