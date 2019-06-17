package com.jdrx.service.query;

import com.jdrx.beans.entry.basic.ShareDevTypePO;
import com.jdrx.beans.entry.query.SpaceInfTotalPO;
import com.jdrx.beans.vo.SpaceInfoVO;
import com.jdrx.dao.query.DevQueryDAO;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
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

	/**
	 * 获取第一级图层对应的设备个数
	 * @return
	 */
	public List<SpaceInfTotalPO> getFirstHierarchyCount() throws BizException{
		List<SpaceInfTotalPO> list  = new ArrayList<>();
		List<ShareDevTypePO> devTypePOs = devQueryDAO.findFirstierarchy();
		devTypePOs.stream().forEach(devTypePO ->{
			SpaceInfTotalPO spaceInfTotalPO = new SpaceInfTotalPO();
			spaceInfTotalPO.setCoverageName(devTypePO.getName());
			spaceInfTotalPO.setNumber(0);
			spaceInfTotalPO.setId(devTypePO.getId());
			list.add(spaceInfTotalPO);
		});
		list.stream().forEach(spaceInfTotalPO -> {
			List<ShareDevTypePO> shareDevTypePOs = devQueryDAO.findDevTypeByPID(spaceInfTotalPO.getId());
			List<Long> ids = new ArrayList<>();
			if (!ObjectUtils.isEmpty(shareDevTypePOs)){
				shareDevTypePOs.stream().forEach(shareDevTypePO ->{
					ids.add(shareDevTypePO.getId());
				});
			}
			Integer cnt = devQueryDAO.getCountByTypeIds(ids);
			spaceInfTotalPO.setNumber(cnt);
		});
		return list;
	}

	/**
	 * 根据类型ID查询所属的设备信息
	 * @param pid
	 * @return
	 * @throws BizException
	 */
	public List<SpaceInfoVO> getDevByPid(Long pid) throws BizException{
		List<SpaceInfoVO> list = devQueryDAO.findDevByTypeId(pid);
		return list;
	}
}