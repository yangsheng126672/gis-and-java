package com.jdrx.gis.service.query;

import com.jdrx.gis.beans.dto.query.AttrQeuryDTO;
import com.jdrx.gis.beans.entry.basic.GisDevTplAttrPO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.beans.vo.query.GISDevExtVO;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.GisDevTplAttrPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 属性查询服务
 * @Author: liaosijun
 * @Time: 2019/6/21 10:38
 */
@Service
public class AttrQueryService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(AttrQueryService.class);

	@Autowired
	ShareDevTypePOMapper shareDevTypePOMapper;

	@Autowired
	GisDevTplAttrPOMapper gisDevTplAttrPOMapper;

	@Autowired
	GISDevExtPOMapper gisDevExtPOMapper;

	@Autowired
	QueryDevService queryDevService;

	/**
	 * 根据设备类型的ID查它所有子孙类中在gis_dev_tpl_attr配置了模板信息的子孙类，
	 * 并且查询出来的设备类型信息就不做层级展示。因为，按照前端页面的需求：父类A查出所有子类B1，B2等等都展示在下拉框中，
	 * 倘若B1在gis_dev_tpl_attr配置了模板，而且B1的子类C1，C2，C3也都在gis_dev_tpl_attr配置了
	 * 模板的话，当下拉框中选中B1时，它后面的联动下拉框就不知道作何展示（是该展示B1的模板字段还是该展示
	 * C1、C2、C3等这些子类型呢？），故type_id的子孙类中配置了模板信息的都放在一起罗列出来。
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public List<ShareDevTypePO> findHasTplDevTypeListById(Long id) throws BizException {
		Logger.debug("根据设备类型的ID查它所有子孙类中在gis_dev_tpl_attr配置了模板信息的子孙类");
		return shareDevTypePOMapper.findHasTplDevTypeListById(id);
	}


	/**
	 * 根据设备类型查模板信息
	 * @param typeId  设备类型ID
	 * @return
	 * @throws BizException
	 */
	public List<GisDevTplAttrPO> findAttrListByTypeId(Long typeId) throws BizException {
		return gisDevTplAttrPOMapper.findAttrListByTypeId(typeId);
	}

	/**
	 * 根据所选区域或属性键入的参数值查设备列表信息
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public List<GISDevExtVO> findDevListByAreaOrInputVal(AttrQeuryDTO dto) throws BizException {
		List<GISDevExtVO> list = gisDevExtPOMapper.findDevListByAreaOrInputVal(dto);
		List<Long> devIds = null;
		if (Objects.nonNull(list)) {
			devIds = list.stream().map(GISDevExtVO::getDevId).collect(Collectors.toList());
		}
		List<GISDevExtVO> gisDevExtVOList = queryDevService.findDevListByDevIDs(devIds);
		return gisDevExtVOList;
	}
}