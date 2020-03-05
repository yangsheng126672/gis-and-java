package com.jdrx.gis.service.log;

import com.google.common.collect.Lists;
import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.beans.entity.basic.ShareDevPO;
import com.jdrx.gis.beans.entity.log.GisDevEditLog;
import com.jdrx.gis.beans.entity.log.GisDevVer;
import com.jdrx.gis.beans.entity.log.ShareDevEditLog;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.ShareDevPOMapper;
import com.jdrx.gis.dao.log.GisDevEditLogManualMapper;
import com.jdrx.gis.dao.log.GisDevVerManualMapper;
import com.jdrx.gis.dao.log.autoGenerate.ShareDevEditLogMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * @Author: liaosijun
 * @Time: 2020/2/27 16:04
 */
@Service
public class GisDevVerService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(GisDevVerService.class);

	@Autowired
	private GisDevVerManualMapper gisDevVerManualMapper;

	@Autowired
	private GisDevEditLogManualMapper gisDevEditLogManualMapper;

	@Autowired
	private ShareDevEditLogMapper shareDevEditLogMapper;

	@Autowired
	private ShareDevPOMapper shareDevPOMapper;

	@Autowired
	private GISDevExtPOMapper gisDevExtPOMapper;

	@Autowired
	private ShareDevEditLogService shareDevEditLogService;

	@Autowired
	private GisDevEditLogService gisDevEditLogService;

	/**
	 * 保存单条版本记录
	 * @param gisDevVer
	 * @param shareDevPO
	 * @param gisDevExtPO
	 */
	public void saveDevEditLog(GisDevVer gisDevVer, ShareDevPO shareDevPO, GISDevExtPO gisDevExtPO) {
		gisDevVerManualMapper.insertReturnId(gisDevVer);
		GisDevEditLog gisDevEditLog = new GisDevEditLog();
		gisDevEditLog.setVerNum(gisDevVer.getId());
		BeanUtils.copyProperties(gisDevExtPO, gisDevEditLog);
		gisDevEditLogManualMapper.insertSelective(gisDevEditLog);
		ShareDevEditLog shareDevEditLog = new ShareDevEditLog();
		shareDevEditLog.setVerNum(gisDevVer.getId());
		BeanUtils.copyProperties(shareDevPO, shareDevEditLog);
		shareDevEditLog.setDevId(shareDevPO.getId());
		shareDevEditLogMapper.insertSelective(shareDevEditLog);
	}

	/**
	 * 保存多条
	 * @param gisDevVer
	 * @param devIds
	 */
	public void saveDevEditLogs(GisDevVer gisDevVer, String[] devIds) {
		Long verNum = gisDevVer.getId();
		if (Objects.isNull(verNum)) {
			gisDevVerManualMapper.insertReturnId(gisDevVer);
		}
		saveDevEditLogs(verNum, devIds);
	}

	/**
	 * 保存多条
	 * @param verNum
	 * @param devIds
	 */
	public void saveDevEditLogs(Long verNum, String[] devIds) {
		List<ShareDevPO> shareDevs = shareDevPOMapper.findByDevIds(Arrays.asList(devIds));
		List<ShareDevEditLog> devEditLogs = Lists.newArrayList();
		if (Objects.nonNull(shareDevs)) {
			shareDevs.forEach(shareDevPO -> {
				ShareDevEditLog shareDevEditLog = new ShareDevEditLog();
				shareDevEditLog.setVerNum(verNum);
				BeanUtils.copyProperties(shareDevPO, shareDevEditLog);
				shareDevEditLog.setDevId(shareDevPO.getId());
				devEditLogs.add(shareDevEditLog);
			});
		}
		List<GISDevExtPO> byDevIds = gisDevExtPOMapper.findByDevIds(Arrays.asList(devIds));
		List<GisDevEditLog> gisDevEditLogs = Lists.newArrayList();
		if (Objects.nonNull(byDevIds)) {
			gisDevEditLogs.forEach(gisDevPO -> {
				GisDevEditLog gisDevEditLog = new GisDevEditLog();
				gisDevEditLog.setVerNum(verNum);
				BeanUtils.copyProperties(gisDevPO, gisDevEditLog);
				gisDevEditLogs.add(gisDevEditLog);
			});
		}
		shareDevEditLogService.splitBatchInsert(devEditLogs);
		gisDevEditLogService.splitBatchInsert(gisDevEditLogs);
	}

	/**
	 * 保存单条版本记录，通过devId保存
	 * @param gisDevVer
	 * @param devId
	 */
	public void saveDevEditLog(GisDevVer gisDevVer,String devId) {
		gisDevVerManualMapper.insertReturnId(gisDevVer);
		GisDevEditLog gisDevEditLog = new GisDevEditLog();
		gisDevEditLog.setVerNum(gisDevVer.getId());
		GISDevExtPO gisDevExtPO = gisDevExtPOMapper.getDevExtByDevId(devId);
		BeanUtils.copyProperties(gisDevExtPO, gisDevEditLog);
		gisDevEditLogManualMapper.insertSelective(gisDevEditLog);
		ShareDevEditLog shareDevEditLog = new ShareDevEditLog();
		shareDevEditLog.setVerNum(gisDevVer.getId());
		ShareDevPO shareDevPO = shareDevPOMapper.selectByPrimaryKey(devId);
		BeanUtils.copyProperties(shareDevPO, shareDevEditLog);
		shareDevEditLog.setDevId(shareDevPO.getId());
		shareDevEditLogMapper.insertSelective(shareDevEditLog);
	}

	/**
	 * 保存单条版本记录
	 * @param varNum
	 * @param shareDevPO
	 * @param gisDevExtPO
	 */
	public void saveDevEditLog(Long varNum, ShareDevPO shareDevPO, GISDevExtPO gisDevExtPO) {
		GisDevEditLog gisDevEditLog = new GisDevEditLog();
		gisDevEditLog.setVerNum(varNum);
		BeanUtils.copyProperties(gisDevExtPO, gisDevEditLog);
		gisDevEditLogManualMapper.insertSelective(gisDevEditLog);
		ShareDevEditLog shareDevEditLog = new ShareDevEditLog();
		shareDevEditLog.setVerNum(varNum);
		BeanUtils.copyProperties(shareDevPO, shareDevEditLog);
		shareDevEditLog.setDevId(shareDevPO.getId());
		shareDevEditLogMapper.insertSelective(shareDevEditLog);
	}
}
