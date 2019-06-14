package com.jdrx.service.basic;

import com.jdrx.beans.entry.basic.ShareDevTypePO;
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

	public List<ShareDevTypePO> fiandAllDevType(){
		List<ShareDevTypePO> list  = shareDevTypePOMapper.findAllDevType();
		return list;
	}

}