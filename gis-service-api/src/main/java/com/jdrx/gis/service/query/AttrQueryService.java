package com.jdrx.gis.service.query;

import com.jdrx.gis.beans.dto.query.AttrQeuryDTO;
import com.jdrx.gis.beans.entry.basic.GisDevTplAttrPO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.beans.vo.query.GISDevExtVO;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.GisDevTplAttrPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.util.ComUtil;
import com.jdrx.gis.util.ExcelStyleUtil;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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

	/**
	 * 导出根据所选区域或属性键入的参数值所查询设备列表信息
	 * @param dto
	 * @param response
	 * @throws BizException
	 */
	public void exportDevListByAreaOrInputVal(AttrQeuryDTO dto, HttpServletResponse response) throws BizException {
		try {
			ShareDevTypePO shareDevTypePO = shareDevTypePOMapper.getByPrimaryKey(dto.getTypeId());
			XSSFWorkbook workbook;
			workbook = new XSSFWorkbook(this.getClass().getResourceAsStream("/template/devinfoAttr.xlsx"));

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
			List<GisDevTplAttrPO> attrPOs = gisDevTplAttrPOMapper.findAttrListByTypeId(dto.getTypeId());
			if (Objects.isNull(attrPOs)) {
				Logger.error("表头信息为空");
				throw new BizException("设备列表的title为空");
			}
			for (int i = 0; i < attrPOs.size(); i++) {
				GisDevTplAttrPO gisDevTplAttrPO = attrPOs.get(i);
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(style);
				String txt = gisDevTplAttrPO.getFieldDesc();
				XSSFRichTextString text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
				cell.setCellValue(text);
			}
			List<GISDevExtVO> devList = gisDevExtPOMapper.findDevListByAreaOrInputVal(dto);
			if (Objects.nonNull(devList)) {
				String[] filedNames = attrPOs.stream().map(GisDevTplAttrPO::getFieldName).toArray(String[]::new);
				devList =  dealDataInfoByDevIds(devList, filedNames);
			} else {
				Logger.debug("条件参数{}获取的设备信息为空", dto.toString());
			}
			if (Objects.nonNull(devList)) {
				int body_i = 1;
				for (GISDevExtVO gisDevExtVO : devList) {
					Map<String, String> map = gisDevExtVO.getDataMap();
					if(Objects.isNull(map)) {
						continue;
					}
					XSSFRow xssfRow = sheet.createRow(body_i ++);
					for (int i = 0; i < attrPOs.size(); i++) {
						XSSFCell cell = xssfRow.createCell(i);
						XSSFRichTextString text;
						GisDevTplAttrPO gisDevTplAttrPO = attrPOs.get(i);
						String txt = null;
						if (Objects.nonNull(gisDevTplAttrPO)){
							String fieldName = gisDevTplAttrPO.getFieldName();
							txt = map.get(fieldName);
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
	 * 根据配置的模板字段，把json数据转换成map信息
	 * @param gisDevExtVOs  设备信息列表
	 * @param filedNames  模板的字段
	 * @return
	 * @throws BizException
	 */
	private List<GISDevExtVO> dealDataInfoByDevIds(List<GISDevExtVO> gisDevExtVOs, String[] filedNames) throws BizException {
		if (Objects.isNull(gisDevExtVOs) || Objects.isNull(filedNames)) {
			throw new BizException("参数为空");
		}
		gisDevExtVOs.stream().map(vo -> {
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
		return gisDevExtVOs;
	}
}