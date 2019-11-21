package com.jdrx.gis.service.basic;

import com.google.common.base.Joiner;
import com.jdrx.gis.beans.entry.basic.GISDevExtPO;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
