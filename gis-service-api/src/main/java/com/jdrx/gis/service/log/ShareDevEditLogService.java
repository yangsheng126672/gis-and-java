package com.jdrx.gis.service.log;

import com.jdrx.gis.beans.entity.log.ShareDevEditLog;
import com.jdrx.gis.dao.log.ShareDevEditLogManualMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Author: liaosijun
 * @Time: 2020/2/18 16:32
 */
@Service
public class ShareDevEditLogService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ShareDevEditLogService.class);

	@Autowired
	ShareDevEditLogManualMapper shareDevEditLogManualMapper;


	private static final int PAGE_SIZE = 1000;


	public int splitBatchInsert(List<ShareDevEditLog> shareDevEditLogList) {
		try {
			if (Objects.nonNull(shareDevEditLogList) && shareDevEditLogList.size() == 0) {
				return 0;
			}
			int total = shareDevEditLogList.size();
			int loopcnt = total % PAGE_SIZE == 0 ? total / PAGE_SIZE : total / PAGE_SIZE + 1;
			if (total < PAGE_SIZE) {
				loopcnt = 1;
			}
			int step = 0;
			while (loopcnt-- > 0) {
				List<ShareDevEditLog> subList = shareDevEditLogList.subList(step * PAGE_SIZE,
						(step + 1) * PAGE_SIZE > total ? total : (step + 1) * PAGE_SIZE);
				shareDevEditLogManualMapper.batchInsertSelective(subList);
				step++;
			}
			return total;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			return 0;
		}
	}
}
