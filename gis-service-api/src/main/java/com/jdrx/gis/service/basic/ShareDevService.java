package com.jdrx.gis.service.basic;

import com.jdrx.gis.beans.entity.basic.ShareDevPO;
import com.jdrx.gis.beans.vo.basic.ShareDevVO;
import com.jdrx.gis.dao.basic.ShareDevPOMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @Author: liaosijun
 * @Time: 2019/11/21 18:19
 */
@Service
public class ShareDevService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ShareDevService.class);

	private static final int PAGE_SIZE = 1000;
	@Autowired
	ShareDevPOMapper shareDevPOMapper;


	public int splitBatchInsert(List<ShareDevPO> shareDevPOList) throws BizException {
		if (Objects.nonNull(shareDevPOList) && shareDevPOList.size() == 0) {
			return 0;
		}
		int total = shareDevPOList.size();
		int loopcnt = total / PAGE_SIZE == 0 ? total / PAGE_SIZE : total / PAGE_SIZE + 1;
		if (total < PAGE_SIZE) {
			loopcnt = 1;
		}
		int step = 0;
		while (loopcnt-- > 0) {
			List<ShareDevPO> subList = shareDevPOList.subList(step * PAGE_SIZE,
					(step + 1) * PAGE_SIZE > total ? total : (step + 1) * PAGE_SIZE);
			shareDevPOMapper.batchInsertSelective(subList);
			step++;
		}
		return total;
	}

	public int splitBatchUpdate(List<ShareDevPO> shareDevPOList) throws BizException {
		if (Objects.nonNull(shareDevPOList) && shareDevPOList.size() == 0) {
			return 0;
		}
		int total = shareDevPOList.size();
		int loopcnt = total / PAGE_SIZE == 0 ? total / PAGE_SIZE : total / PAGE_SIZE + 1;
		if (total < PAGE_SIZE) {
			loopcnt = 1;
		}
		int step = 0;
		while (loopcnt-- > 0) {
			List<ShareDevPO> subList = shareDevPOList.subList(step * PAGE_SIZE,
					(step + 1) * PAGE_SIZE > total ? total : (step + 1) * PAGE_SIZE);
			shareDevPOMapper.batchUpdate(subList);
			step++;
		}
		return total;
	}

	/**
	 * 根据主键获取
	 * @param id
	 * @return
	 */
	public ShareDevVO selectByPrimaryKey(String id) throws BizException {
		try {
			ShareDevPO shareDevPO = shareDevPOMapper.selectByPrimaryKey(id);
			ShareDevVO shareDevVO = new ShareDevVO();
			BeanUtils.copyProperties(shareDevPO, shareDevVO);
			return shareDevVO;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("根据devId获取设备失败！");
		}
	}
}
