package com.jdrx.gis.service.basic;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jdrx.gis.beans.entity.basic.CodeXYPO;
import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @Author: liaosijun
 * @Time: 2019/11/20 21:31
 */
@Service
public class GisDevExtService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(GisDevExtService.class);

	@Autowired
	GISDevExtPOMapper gisDevExtPOMapper;


	private static final int PAGE_SIZE = 1000;


	/**
	 *  根据code列表获取设备扩展信息
	 */
	public List<GISDevExtPO> selectByCodes(Set<String> codesSet) throws BizException {
		try {
			String codes = null;
			if (Objects.nonNull(codesSet) && codesSet.size() > 0) {
				codes = Joiner.on(",").join(codesSet);
			}
			return gisDevExtPOMapper.selectByCodes(codes);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("根据code列表获取设备扩展信息失败！", codesSet);
			throw new BizException("根据code列表获取设备扩展信息！");
		}
	}



	public int splitBatchInsert(List<GISDevExtPO> gisDevExtPOS) throws BizException {
		if (Objects.nonNull(gisDevExtPOS) && gisDevExtPOS.size() == 0) {
			return 0;
		}
		int total = gisDevExtPOS.size();
		int loopcnt = total % PAGE_SIZE == 0 ? total / PAGE_SIZE : total / PAGE_SIZE + 1;
		if (total < PAGE_SIZE) {
			loopcnt = 1;
		}
		int step = 0;
		while (loopcnt-- > 0) {
			List<GISDevExtPO> subList = gisDevExtPOS.subList(step * PAGE_SIZE,
					(step + 1) * PAGE_SIZE > total ? total : (step + 1) * PAGE_SIZE);
			gisDevExtPOMapper.batchInsertSelective(subList);
			step++;
		}
		return total;
	}

	public int splitBatchUpdate(List<GISDevExtPO> gisDevExtPOS) throws BizException {
		if (Objects.nonNull(gisDevExtPOS) && gisDevExtPOS.size() == 0) {
			return 0;
		}
		int total = gisDevExtPOS.size();
		int loopcnt = total % PAGE_SIZE == 0 ? total / PAGE_SIZE : total / PAGE_SIZE + 1;
		if (total < PAGE_SIZE) {
			loopcnt = 1;
		}
		int step = 0;
		while (loopcnt-- > 0) {
			List<GISDevExtPO> subList = gisDevExtPOS.subList(step * PAGE_SIZE,
					(step + 1) * PAGE_SIZE > total ? total : (step + 1) * PAGE_SIZE);
			gisDevExtPOMapper.batchUpdate(subList);
			step++;
		}
		return total;
	}

	public List<Map<String,Object>> splitFindGeomMapByPointCode(List<CodeXYPO> codeXYPOs, int srid) throws BizException {
		List<Map<String,Object>> list = Lists.newArrayList();
		if (Objects.nonNull(codeXYPOs) && codeXYPOs.size() == 0) {
			return list;
		}
		int total = codeXYPOs.size();
		int loopcnt = total % PAGE_SIZE == 0 ? total / PAGE_SIZE : total / PAGE_SIZE + 1;
		if (total < PAGE_SIZE) {
			loopcnt = 1;
		}
		int step = 0;
		while (loopcnt-- > 0) {
			List<CodeXYPO> subList = codeXYPOs.subList(step * PAGE_SIZE,
					(step + 1) * PAGE_SIZE > total ? total : (step + 1) * PAGE_SIZE);
			List<Map<String, Object>> splitList = gisDevExtPOMapper.findGeomMapByPointCode(subList, srid);
			list.addAll(splitList);
			step++;
		}
		return list;
	}

	/**
	 * 获取经纬度
	 * @param devId
	 * @return
	 * @throws BizException
	 */
	public String getLngLatByDevId(String devId) {
		return gisDevExtPOMapper.getLngLatByDevId(devId);
	}
	
		/**
	 * 获取经空间位置信息
	 * @param devId
	 * @return
	 * @throws BizException
	 */
	public String getGeomByDevId(String devId) {
		return gisDevExtPOMapper.getGeomByDevIdToWGS(devId);
	}
}
