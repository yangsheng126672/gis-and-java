package com.jdrx.gis.service.basic;

import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.dao.basic.DictDetailPOMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 数据字典服务
 * @Author: liaosijun
 * @Time: 2019/6/27 13:08
 */
@Service
public class DictService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DictService.class);

	@Autowired
	private DictDetailPOMapper dictDetailPOMapper;

	/**
	 * 根据dict_type中val的值查询配置的dict_detail列表
	 * @param val dict_type中的val字段对应的值
	 * @return
	 * @throws BizException
	 */
	public List<DictDetailPO> findDetailsByTypeVal(String val) throws BizException {
		return dictDetailPOMapper.selectByVal(val);
	}
}