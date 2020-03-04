package com.jdrx.gis.service.log;

import com.jdrx.gis.beans.entity.log.GisDevEditLog;
import com.jdrx.gis.dao.log.GisDevEditLogManualMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Author: liaosijun
 * @Time: 2020/2/18 16:27
 */
@Service
public class GisDevEditLogService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(GisDevEditLogService.class);

	@Autowired
	GisDevEditLogManualMapper gisDevEditLogManualMapper;


	private static final int PAGE_SIZE = 1000;


	public int splitBatchInsert(List<GisDevEditLog> gisDevExtPOS) {
		try {
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
				List<GisDevEditLog> subList = gisDevExtPOS.subList(step * PAGE_SIZE,
						(step + 1) * PAGE_SIZE > total ? total : (step + 1) * PAGE_SIZE);
				gisDevEditLogManualMapper.batchInsertSelective(subList);
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
