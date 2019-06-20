package com.jdrx.service.basic;

import com.jdrx.beans.entry.basic.GISDevExtPO;
import com.jdrx.beans.entry.basic.ShareDevTypePO;
import com.jdrx.dao.basic.GISDevExtPOMapper;
import com.jdrx.dao.basic.ShareDevTypePOMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 基本功能中的图层服务
 * @Author: liaosijun
 * @Time: 2019/6/14 11:30
 */
@Service
public class BasicDevQuery {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(BasicDevQuery.class);

	@Autowired
	private ShareDevTypePOMapper shareDevTypePOMapper;

	@Autowired
	private GISDevExtPOMapper gisDevExtPOMapper;

	/**
	 * 查询所有设备类型
	 * @return
	 */
	public List<ShareDevTypePO> findDevTypeList(){
		List<ShareDevTypePO> list  = shareDevTypePOMapper.findDevTypeList();
		return list;
	}

	/**
	 * 根据设备ID查当前设备的属性信息
	 * @param devId
	 */
	public GISDevExtPO getDevExtByDevId(Long devId){
		GISDevExtPO gisDevExtPO = gisDevExtPOMapper.getDevExtByDevId(devId);
		return gisDevExtPO;
	}

}