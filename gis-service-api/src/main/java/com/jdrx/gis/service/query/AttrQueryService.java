package com.jdrx.gis.service.query;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.query.AttrQeuryDTO;
import com.jdrx.gis.beans.dto.query.CaliberDTO;
import com.jdrx.gis.beans.dto.query.CriteriaWithDataTypeCategoryCodeDTO;
import com.jdrx.gis.beans.dto.query.MeterialDTO;
import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.beans.entry.basic.GisDevTplAttrPO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.beans.vo.query.FieldNameVO;
import com.jdrx.gis.beans.vo.query.GISDevExtVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.GisDevTplAttrPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.dao.query.DevQueryDAO;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.gis.util.ComUtil;
import com.jdrx.gis.util.ExcelStyleUtil;
import com.jdrx.gis.util.JavaFileToFormUpload;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.jdbc.beans.vo.PageVO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
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

	@Autowired
	LayerService layerService;

	@Autowired
	private DictDetailService detailService;

	@Autowired
	private DictConfig dictConfig;

	@Autowired
	private PathConfig pathConfig;

	@Autowired
	private DevQueryDAO devQueryDAO;

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
		try {
			Logger.debug("根据设备类型的ID查它所有子孙类中在gis_dev_tpl_attr配置了模板信息的子孙类");
			return shareDevTypePOMapper.findHasTplDevTypeListById(id);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("根据设备类型的ID{}查它所有子孙类中在gis_dev_tpl_attr配置了模板信息的子孙类失败！", id);
			throw new BizException("根据设备类型的ID查它所有子孙类中在gis_dev_tpl_attr配置了模板信息的子孙类失败！");
		}
	}


	/**
	 * 根据设备类型查模板信息
	 * @param typeId  设备类型ID
	 * @return
	 * @throws BizException
	 */
	public List<FieldNameVO> findAttrListByTypeId(Long typeId) throws BizException {
		try {

			List<GisDevTplAttrPO> list = gisDevTplAttrPOMapper.findAttrListByTypeId(typeId);
			List<FieldNameVO> fieldNameVOS = Lists.newArrayList();
			if (Objects.nonNull(list)) {
				list.stream().forEach(gisDevTplAttrPO -> {
					FieldNameVO vo = new FieldNameVO();
					BeanUtils.copyProperties(gisDevTplAttrPO, vo);
					if (Objects.nonNull(gisDevTplAttrPO.getDataType())) {
						String dataType = gisDevTplAttrPOMapper.getCategoryCodeByDataType(gisDevTplAttrPO.getDataType());
						vo.setDataType(dataType);
					}
					fieldNameVOS.add(vo);
				});
				// 设备模板里面是没有配置设备的类型名称的，其实可以配置，但感觉不是很合理
				// 所以这里就把类名称这一列+上来
				FieldNameVO vo = new FieldNameVO();
				vo.setFieldName(GISConstants.DEV_TYPE_NAME);
				vo.setFieldDesc(GISConstants.DEV_TYPE_NAME_DESC);
				fieldNameVOS.add(vo);

				for (int i = 0; i < fieldNameVOS.size(); i++) {
					FieldNameVO fieldNameVO = fieldNameVOS.get(i);
					if (Objects.isNull(fieldNameVO)) {
						break;
					}
					String fieldName = fieldNameVO.getFieldName();
					if (StringUtils.isEmpty(fieldName)) {
						break;
					}
					if (GISConstants.DEV_ID.equals(fieldName)) {
						Collections.swap(fieldNameVOS, i, 0);
						continue;
					}
					if (GISConstants.DEV_TYPE_NAME.equals(fieldName)) {
						Collections.swap(fieldNameVOS, i, 1);
					}
				}
			}
			return fieldNameVOS;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("根据设备类型ID={}查模板信息失败！", typeId);
			throw new BizException("根据设备类型ID查模板信息失败！");
		}
	}

	/**
	 * 根据所选区域或属性键入的参数值查设备列表信息
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public PageVO<GISDevExtVO> findDevListByAreaOrInputVal(AttrQeuryDTO dto) throws BizException {
		try {
			Long start = System.currentTimeMillis();
			String devStr = null;
			Long[] devIds = dto.getDevIds();
			List<Long> ids = Objects.nonNull(devIds) ? Arrays.asList(devIds) : Lists.newArrayList();
			if (Objects.nonNull(devIds) && devIds.length > 0) {
				devStr = Joiner.on(",").join(ids);
			}
			PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy());
			List<CriteriaWithDataTypeCategoryCodeDTO> criteriaList = dto.getCriteriaList();
			if (Objects.nonNull(criteriaList) && criteriaList.size() > 0) {
				criteriaList.stream().forEach(cri -> {
					try {
						String rp = ComUtil.processAttrField(cri.getFieldName(), cri.getCriteria(), cri.getDataTypeCategoryCode());
						cri.setAssemblyStr(rp);
					} catch (BizException e) {
						e.printStackTrace();
					}
				});
			}
            Page<GISDevExtVO> list = (Page<GISDevExtVO>) gisDevExtPOMapper.findDevListByAreaOrInputVal(dto, devStr);
			Long end = System.currentTimeMillis();
			Logger.debug("数据库耗时： " + (end - start) + " ms");
			return new PageVO<GISDevExtVO>(list);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("据所选区域或属性键入的参数值查设备列表信息失败，{}", dto.toString());
			throw new BizException("据所选区域或属性键入的参数值查设备列表信息失败！");
		}
	}

	/**
	 * 根据所选区域或属性键入的参数值查设备列表信息，分页
	 * @param dto
	 * @return
	 * @throws BizException
	 */
//	public PageVO<GISDevExtVO> findDevListPageByAreaOrInputVal(AttrQeuryDTO dto) throws BizException {
//		Page<GISDevExtVO> list = (Page<GISDevExtVO>) findDevListByAreaOrInputVal(dto);
//		return new PageVO<GISDevExtVO>(list);
//	}

	/**
	 * 导出根据所选区域或属性键入的参数值所查询设备列表信息
	 * @param dto
	 * @throws BizException
	 */
	public String exportDevListByAreaOrInputVal(AttrQeuryDTO dto) throws BizException {
		OutputStream bos = null;
		try {
			ShareDevTypePO shareDevTypePO = shareDevTypePOMapper.getByPrimaryKey(dto.getTypeId());
			SXSSFWorkbook workbook;
			workbook = new SXSSFWorkbook(1000);

			String title = "Sheet1";
			if (Objects.nonNull(shareDevTypePO)) {
				title = shareDevTypePO.getName();
			}
			SXSSFSheet sheet = workbook.createSheet(title);
			sheet.setDefaultColumnWidth((short) 12); // 设置列宽
			CellStyle style = ExcelStyleUtil.createHeaderStyle(workbook);
			CellStyle style2 = ExcelStyleUtil.createBodyStyle(workbook);

			Row row = sheet.createRow(0);
			List<FieldNameVO> attrPOs = findAttrListByTypeId(dto.getTypeId());
			if (Objects.isNull(attrPOs)) {
				Logger.error("表头信息为空");
				throw new BizException("设备列表的title为空");
			}

			for (int i = 0; i < attrPOs.size(); i++) {
				FieldNameVO fieldNameVO = attrPOs.get(i);
				Cell cell = row.createCell(i);
				cell.setCellStyle(style);
				String txt = fieldNameVO.getFieldDesc();
				XSSFRichTextString text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
				cell.setCellValue(text);
			}
			String devStr = null;
			Long[] devIds = dto.getDevIds();
			List<Long> ids = Objects.nonNull(devIds) ? Arrays.asList(devIds) : Lists.newArrayList();
			if (Objects.nonNull(devIds) && devIds.length > 0) {
				devStr = Joiner.on(",").join(ids);
			}
			List<CriteriaWithDataTypeCategoryCodeDTO> criteriaList = dto.getCriteriaList();
			if (Objects.nonNull(criteriaList) && criteriaList.size() > 0) {
				criteriaList.stream().forEach(cri -> {
					try {
						String rp = ComUtil.processAttrField(cri.getFieldName(), cri.getCriteria(), cri.getDataTypeCategoryCode());
						cri.setAssemblyStr(rp);
					} catch (BizException e) {
						e.printStackTrace();
					}
				});
			}
			int total = gisDevExtPOMapper.findDevListByAreaOrInputValCount(dto, devStr);
			int pageSize = GISConstants.EXPORT_PAGESIZE;
			int pageTotal;
			if (total <= pageSize) {
				pageTotal = 1;
			} else {
				pageTotal = total / pageSize == 0 ? total / pageSize : total / pageSize + 1;
			}
			Logger.debug("总条数：" + total + "\t每页条数：" + pageSize + "\t总页数：" + pageTotal);
			int pageNum = 1;
			while(pageTotal -- > 0) {
				dto.setPageNum(pageNum);
				dto.setPageSize(GISConstants.EXPORT_PAGESIZE);
				List<GISDevExtVO> devList = gisDevExtPOMapper.findDevListByAreaOrInputVal(dto, devStr);
				if (Objects.nonNull(devList)) {
					String[] filedNames = attrPOs.stream().map(FieldNameVO::getFieldName).toArray(String[]::new);
					devList = dealDataInfoByDevIds(devList, filedNames);
				} else {
					Logger.debug("条件参数{}获取的设备信息为空", dto.toString());
				}
				if (Objects.nonNull(devList)) {
					int body_i = 1;
					for (GISDevExtVO gisDevExtVO : devList) {
						Map<String, String> map = gisDevExtVO.getDataMap();
						if (Objects.isNull(map)) {
							continue;
						}
						Row xssfRow = sheet.createRow(body_i++);
						for (int i = 0; i < attrPOs.size(); i++) {
							Cell cell = xssfRow.createCell(i);
							XSSFRichTextString text;
							FieldNameVO fieldNameVO = attrPOs.get(i);
							String txt = null;
							if (Objects.nonNull(fieldNameVO)) {
								String fieldName = fieldNameVO.getFieldName();
								if (GISConstants.DEV_TYPE_NAME.equals(fieldName)) {
									txt = gisDevExtVO.getTypeName();
								} else {
									txt = map.get(fieldName);
								}
							}
							text = new XSSFRichTextString(StringUtils.isEmpty(txt) ? "" : txt);
							cell.setCellValue(text);
							cell.setCellStyle(style2);
						}
					}
				}
				devList.clear();
				pageNum ++;
			}
			String filePath = pathConfig.getDownloadPath() + "/" + title + ".xls";
			bos = new FileOutputStream(new File(filePath));
			workbook.write(bos);
			String result = JavaFileToFormUpload.send(pathConfig.getUploadFileUrl(), filePath);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			Logger.error("IO失败，{}", dto.toString());
			throw new BizException("IO失败！");
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("导出属性信息失败，{}", dto.toString());
			throw new BizException("导出属性信息失败！");
		} finally {
			if (bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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

	/**
	 * 获取管径范围对应的图层地址
	 * @param caliberDTO
	 * @return
	 */
	public String findCiliberLayerUrlByNum(CaliberDTO caliberDTO)throws BizException {
		String layerUrl = null;
		String caliberLyerUrl = null;
		try {
			layerUrl = dictConfig.getCaliberUrl();
			List<DictDetailPO> detailPOs = detailService.findDetailsByTypeVal(layerUrl);
			for (DictDetailPO dictDetail:detailPOs){
				if (dictDetail.getName().equals(caliberDTO.getNum())){
					caliberLyerUrl = dictDetail.getVal();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return caliberLyerUrl;
	}

	/**
	 * 获取管材对应的图层地址
	 * @param meterialDTO
	 * @return
	 */
	public String findMeterialLayerUrlByNum(MeterialDTO meterialDTO)throws BizException {
		String layerUrl = null;
		String caliberLyerUrl = null;
		try {
			layerUrl = dictConfig.getMeterialUrl();
			List<DictDetailPO> detailPOs = detailService.findDetailsByTypeVal(layerUrl);
			for (DictDetailPO dictDetail:detailPOs){
				if (dictDetail.getName().equals(meterialDTO.getType())){
					caliberLyerUrl = dictDetail.getVal();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return caliberLyerUrl;
	}

	/**
	 * 根据设备ID获取模板字段
	 * @param devId
	 * @return
	 * @throws BizException
	 */
	public List<FieldNameVO> getFieldNames(Long devId) throws BizException {
		try {
			return devQueryDAO.findFieldNamesByDevID(devId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException(e);
		}
	}

	/**
	 * 验证一下查询条件是否正确
	 * @param dto
	 * @return
	 */
	public Boolean validateCriteria(AttrQeuryDTO dto) throws BizException{
		try {
			PageHelper.startPage(1, 10, null);
			List<CriteriaWithDataTypeCategoryCodeDTO> criteriaList = dto.getCriteriaList();
			StringBuffer sb = new StringBuffer();
			if (Objects.nonNull(criteriaList) && criteriaList.size() > 0) {
				criteriaList.stream().forEach(cri -> {
					try {
						String rp = ComUtil.processAttrField(cri.getFieldName(), cri.getCriteria(), cri.getDataTypeCategoryCode());
						cri.setAssemblyStr(rp);
						sb.append(" " + rp);
					} catch (BizException e) {
						e.printStackTrace();
					}
				});
			}

			if (String.valueOf(sb).length() > 512) {
				throw new BizException("保存属性查询的筛选条件失败: 条件的值超过数据库的长度！");
			}

			gisDevExtPOMapper.findDevListByAreaOrInputVal(dto, null);
			return true;
		} catch (BizException e) {
			e.printStackTrace();
			throw new BizException(e);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}