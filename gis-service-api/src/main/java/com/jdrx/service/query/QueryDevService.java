package com.jdrx.service.query;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.jdrx.beans.constants.basic.ECaliber;
import com.jdrx.beans.constants.basic.ELimbLeaf;
import com.jdrx.beans.constants.basic.GISConstants;
import com.jdrx.beans.dto.query.QueryDevDTO;
import com.jdrx.beans.entry.basic.GISDevExtPO;
import com.jdrx.beans.entry.basic.GisDevTplAttrPO;
import com.jdrx.beans.entry.basic.ShareDevTypePO;
import com.jdrx.beans.entry.query.SpaceInfTotalPO;
import com.jdrx.beans.vo.query.*;
import com.jdrx.dao.basic.GISDevExtPOMapper;
import com.jdrx.dao.basic.GisDevTplAttrPOMapper;
import com.jdrx.dao.basic.ShareDevPOMapper;
import com.jdrx.dao.query.DevQueryDAO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.jdbc.beans.vo.PageVO;
import com.jdrx.service.basic.ShareDevTypeService;
import com.jdrx.util.ComUtil;
import com.jdrx.util.ExcelStyleUtil;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

	@Autowired
	private ShareDevTypeService shareDevTypeService;

	@Autowired
	private GISDevExtPOMapper gisDevExtPOMapper;

	@Autowired
	private GisDevTplAttrPOMapper gisDevTplAttrPOMapper;

	@Autowired
	private ShareDevPOMapper shareDevPOMapper;
	/**
	 * 获取第一级图层对应的设备个数
	 * @return
	 */
	public List<SpaceInfTotalPO> findFirstHierarchyDevTypeNum() throws BizException{
		List<SpaceInfTotalPO> list  = new ArrayList<>();
		List<ShareDevTypePO> devTypePOs = devQueryDAO.findFirstHierarchyDevTypeNum();
		devTypePOs.stream().forEach(devTypePO ->{
			SpaceInfTotalPO spaceInfTotalPO = new SpaceInfTotalPO();
			spaceInfTotalPO.setCoverageName(devTypePO.getName());
			spaceInfTotalPO.setNumber(0L);
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
			Long cnt = devQueryDAO.getCountByTypeIds(ids);
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
	public List<SpaceInfoVO> findDevListByTypeID(Long pid) throws BizException{
		List<SpaceInfoVO> list = devQueryDAO.findDevListByTypeID(pid);
		return list;
	}

	/**
	 * 根据类型ID查表头，递归该ID下面所有子类，让所有子类的模板配置的字段都展示出来，
	 * 若字段名称一样就合并成一个。
	 * 把设备编号和类名称放在第一二列
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public List<FieldNameVO> findFieldNamesByTypeID(Long id) throws BizException{
		List<FieldNameVO> list = devQueryDAO.findFieldNamesByTypeID(id);
		if (Objects.nonNull(list)) {
			// 设备模板里面是没有配置设备的类型名称的，其实可以配置，但感觉不是很合理
			// 这里就把类名称这一列+上来
			FieldNameVO vo = new FieldNameVO();
			vo.setFieldName(GISConstants.DEV_TYPE_NAME);
			vo.setFieldDesc(GISConstants.DEV_TYPE_NAME_DESC);
			list.add(vo);

			for (int i = 0; i < list.size(); i++){
				FieldNameVO fieldNameVO = list.get(i);
				if (Objects.isNull(fieldNameVO)) {
					break;
				}
				String fieldName = fieldNameVO.getFieldName();
				if (StringUtils.isEmpty(fieldName)) {
					break;
				}
				if (GISConstants.DEV_ID.equals(fieldName)) {
					Collections.swap(list, i, 0);
					continue;
				}
				if (GISConstants.DEV_TYPE_NAME.equals(fieldName)){
					Collections.swap(list, i, 1);
				}
			}
		}
		return list;
	}

	/**
	 * 获取设备类型下面的设备列表信息，处理json
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public List<SpaceInfoVO> findDevListByTypeID2(Long id) throws BizException {
		List<SpaceInfoVO> list = findDevListByTypeID(id);
		List<FieldNameVO> fieldsList = findFieldNamesByTypeID(id);
		if (Objects.isNull(fieldsList)) {
			Logger.error("空间查询的表头信息为空");
			throw new BizException("设备列表的title为空");
		}
		String[] filedNames = fieldsList.stream().map(FieldNameVO::getFieldName).toArray(String[]::new);
		if (Objects.nonNull(list)) {
			list.stream().map(vo -> {
				Object obj = vo.getDataInfo();
				if (Objects.isNull(obj)) {
					return vo;
				}
				try {
					Map<String, String> map = ComUtil.parseDataInfo(obj, filedNames);
					vo.setDataMap(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return vo;
			}).collect(Collectors.toList());
		}
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

	/**
	 * 根据设备类型的ID，查询子类的设备个数，子类为第二层的子类
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public List<SonsNumVO> findSonsNumByPid(Long id) throws BizException {
		List<SonsNumVO> resultList = new ArrayList<>();
		// 根据设备类型ID查第二层的子类
		List<ShareDevTypePO> secondTypeList = shareDevTypeService.findDevTypeListByTypeId(id);

		if (!Objects.isNull(secondTypeList)) {
			secondTypeList.stream().forEach(po -> {
				SonsNumVO vo = new SonsNumVO();
				// 根据ID查所有子类
				try {
					List<ShareDevTypePO> allTypeList = null;
					Integer ll = Integer.valueOf(po.getLimbLeaf());

					// 如果当前类型下有子类
					if (ELimbLeaf.LIMB.getVal().equals(ll)) {
						allTypeList = shareDevTypeService.findAllDevTypeListByTypePId(po.getId());
					// 如果当前类型下没有子类
					} else if (ELimbLeaf.LEAF.getVal().equals(ll)){
						allTypeList = shareDevTypeService.findAllDevTypeListByCurrTypeId(po.getId());
					}
					List<Long> typeIds = new ArrayList<>();
					if (!Objects.isNull(allTypeList)) {
						typeIds = allTypeList.stream().map(ShareDevTypePO::getId).collect(Collectors.toList());
					}
					vo.setTypeName(po.getName());
					long num = 0L;
					// 如果typeIds 为空就会统计称全表个数，故不为空才查询
					if (typeIds != null && typeIds.size() > 0) {
						num = devQueryDAO.getCountByTypeIds(typeIds);
					}
					vo.setNum(num);
					resultList.add(vo);
				} catch (BizException e) {
					Logger.error("空间查询，子类个数统计异常：{}", e.getMsg());
					e.printStackTrace();
				}
			});
		}
		return resultList;
	}

	/**
	 * 导出空间查询的设备列表数据
	 * @param response
	 * @param id
	 * @throws BizException
	 */
	public void exportDevInfoByPID(HttpServletResponse response, Long id) throws BizException {
		try {
			XSSFWorkbook workbook;
			workbook = new XSSFWorkbook(this.getClass().getResourceAsStream("/template/devinfo.xlsx"));
			ShareDevTypePO shareDevTypePO = shareDevTypeService.getByPrimaryKey(id);
			String title = "Sheet1";
			if (Objects.nonNull(shareDevTypePO)) {
				title = shareDevTypePO.getName();
			}
			XSSFSheet sheet = workbook.getSheetAt(0);
			workbook.setSheetName(0, title);
			sheet.setDefaultColumnWidth((short) 12); // 设置列宽
			XSSFCellStyle style = ExcelStyleUtil.createHeaderStyle(workbook);
			XSSFCellStyle style2 = ExcelStyleUtil.createBodyStyle(workbook);

			XSSFRow row = sheet.createRow(0);
			List<FieldNameVO> headerList = findFieldNamesByTypeID(id);
			if (Objects.isNull(headerList)) {
				Logger.error("空间查询的表头信息为空");
				throw new BizException("设备列表的title为空");
			}
			for (int i = 0; i < headerList.size(); i++) {
				FieldNameVO fieldNameVO = headerList.get(i);
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(style);
				String txt = fieldNameVO.getFieldDesc();
				XSSFRichTextString text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
				cell.setCellValue(text);
			}
			List<SpaceInfoVO> devList = findDevListByTypeID2(id);
			if (Objects.nonNull(devList)) {
				int body_i = 1;
				for (SpaceInfoVO spaceInfoVO : devList) {
					Map<String, String> map = spaceInfoVO.getDataMap();
					if(Objects.isNull(map)) {
						continue;
					}
					XSSFRow xssfRow = sheet.createRow(body_i ++);
					for (int i = 0; i < headerList.size(); i++) {
						XSSFCell cell = xssfRow.createCell(i);
						XSSFRichTextString text;
						FieldNameVO fieldNameVO = headerList.get(i);
						String txt = null;
						if (Objects.nonNull(fieldNameVO)){
							String fieldName = fieldNameVO.getFieldName();
							if (GISConstants.DEV_TYPE_NAME.equals(fieldName)) {
								txt = spaceInfoVO.getTypeName();
							}else {
								txt = map.get(fieldName);
							}
						}
						text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
						cell.setCellValue(text);
						cell.setCellStyle(style2);
					}
				}
			}
			response.reset();
			response.setCharacterEncoding("utf-8");
			response.setHeader("content-disposition", "attachment;filename=" + title + ".xlsx");
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			workbook.write(response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取设备类型下面的设备列表信息，分页
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public PageVO<SpaceInfoVO> findDevListPageByTypeID(QueryDevDTO dto) throws BizException {
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
		Page<SpaceInfoVO> list = (Page<SpaceInfoVO>) findDevListByTypeID2(dto.getId());
		return new PageVO<>(list);
	}

	/**
	 * 根据设备ID集合获取设备列表信息
	 * @param ids
	 * @return
	 * @throws BizException
	 */
	public List<GISDevExtVO> findDevListByDevIDs(List<Long> ids) throws BizException {
		if (Objects.isNull(ids)) {
			Logger.error("参数为空");
			return Lists.newArrayList();
		}
		List<GISDevExtPO> list =  gisDevExtPOMapper.findDevListByDevIds(ids);
		List<GISDevExtVO> gisDevExtVOList = Lists.newArrayList();
		if (Objects.nonNull(list)){
			list.stream().map(po ->{
				Object obj = po.getDataInfo();
				if (Objects.isNull(obj)) {
					return po;
				}
				Long devId = po.getDevId();
				List<GisDevTplAttrPO> attrPOList = gisDevTplAttrPOMapper.findTplAttrsByDevId(devId);
				String[] filedNames = attrPOList.stream().map(GisDevTplAttrPO::getFieldName).toArray(String[]::new);
				GISDevExtVO vo = new GISDevExtVO();
				BeanUtils.copyProperties(po,vo);
				try {
					Map<String, String> map = ComUtil.parseDataInfo(obj, filedNames);
					vo.setDataMap(map);
					gisDevExtVOList.add(vo);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return po;
			}).collect(Collectors.toList());
		}
		return gisDevExtVOList;
	}

	/**
	 * 根据设备ID集合获取设备列表信息，分页
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public PageVO<GISDevExtVO> findDevListPageByDevIDs(QueryDevDTO dto) throws BizException {
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
		Page<GISDevExtVO> list = (Page<GISDevExtVO>) findDevListByDevIDs(dto.getIds());
		return new PageVO<>(list);
	}
}