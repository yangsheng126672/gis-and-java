package com.jdrx.service.query;

import com.jdrx.beans.entry.query.SpaceInfTotalPO;
import com.jdrx.dao.query.DevQueryDAO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 提供gis的查询服务
 * @Author: liaosijun
 * @Time: 2019/6/12 11:10
 */
@Service
public class QueryDevService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(QueryDevService.class);

	@Autowired
	private DevQueryDAO devQueryDAO;

	public List<SpaceInfTotalPO> queryAllDevNum(){
		List<SpaceInfTotalPO> list  = devQueryDAO.queryAllDevNum();
		return list;
	}
}