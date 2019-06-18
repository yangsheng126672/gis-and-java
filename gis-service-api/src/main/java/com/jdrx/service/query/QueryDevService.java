package com.jdrx.service.query;

import com.jdrx.beans.constants.basic.ECaliber;
import com.jdrx.beans.entry.basic.ShareDevTypePO;
import com.jdrx.beans.entry.query.SpaceInfTotalPO;
import com.jdrx.beans.vo.query.FieldNameVO;
import com.jdrx.beans.vo.query.SonsNumVO;
import com.jdrx.beans.vo.query.SpaceInfoVO;
import com.jdrx.beans.vo.query.WaterPipeTypeNumVO;
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

	/**
	 * 根据类型ID查表头，递归该ID下面所有子类，让所有子类的模板配置的字段都展示出来，
	 * 若字段名称一样就合并成一个。
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public List<FieldNameVO> findFieldNameByTypeID(Long id) throws BizException{
		List<FieldNameVO> list = devQueryDAO.findFieldNameByTypeID(id);
		return list;
	}

	/**
	 * 水管口径数量统计，当前按照我们自己定义的大小分类
	 * @return
	 * @throws BizException
	 */
	public List<WaterPipeTypeNumVO> findWaterPipeCaliberSum() throws BizException{
		List<WaterPipeTypeNumVO> list = new ArrayList<>();
		for (ECaliber ec : ECaliber.values()){
			WaterPipeTypeNumVO vo = new WaterPipeTypeNumVO();
			vo.setTypeName(ec.getName());
			long num;
			switch (ec.getCode()){
				case "D1" :
					num = devQueryDAO.findWaterPipeCaliberSum(null,100);
					vo.setNum(num);
					list.add(vo);
					break;
				case "D2" :
					num = devQueryDAO.findWaterPipeCaliberSum(100,200);
					vo.setNum(num);
					list.add(vo);
					break;
				case "D3" :
					num = devQueryDAO.findWaterPipeCaliberSum(200,400);
					vo.setNum(num);
					list.add(vo);
					break;
				case "D4" :
					num = devQueryDAO.findWaterPipeCaliberSum(400,600);
					vo.setNum(num);
					list.add(vo);
					break;
				case "D5" :
					num = devQueryDAO.findWaterPipeCaliberSum(600,900);
					vo.setNum(num);
					list.add(vo);
					break;
				case "D6" :
					num = devQueryDAO.findWaterPipeCaliberSum(900,null);
					vo.setNum(num);
					list.add(vo);
					break;
				default : break;
			}
		}
		return list;
	}

	public List<SonsNumVO> findSonsNumByPid(Long id) throws BizException {
		devQueryDAO.findSonsNumByPid(id);
		return null;
	}
}