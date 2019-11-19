package com.jdrx.gis.service.dataManage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jdrx.gis.beans.constants.basic.EPGDataTypeCategory;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.beans.entry.basic.GisDevTplAttrPO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.dao.pgSys.PgTypeDAO;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.gis.util.ComUtil;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: liaosijun
 * @Time: 2019/11/4 17:02
 */
@Component
public class ExcelProcessor {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ExcelProcessor.class);

	@Autowired
	private DictConfig dictConfig;

	@Autowired
	private DictDetailService dictDetailService;

	@Autowired
	private PgTypeDAO pgTypeDAO;

	@Autowired
	private ShareDevTypePOMapper shareDevTypePOMapper;

	@Autowired
	private GisDevTplAttrService gisDevTplAttrService;

	// 需要判断是否为空的列
	private static List<String>  validHeader = Lists.newArrayList();

	static {
		validHeader.add(GISConstants.CALIBER_CHN);
		validHeader.add(GISConstants.DATA_AUTH_CHN);
		validHeader.add(GISConstants.DEV_TYPE_NAME_CHN);
		validHeader.add(GISConstants.LINE_START_CODE_CHN);
		validHeader.add(GISConstants.LINE_END_CODE_CHN);
		validHeader.add(GISConstants.POINT_CODE_CHN);
	}

	private final static Integer KEY = 2464;

	/**
	 * 固定sheet名字，预防或提醒管点和管段数据别放混了
	 * @param workbook
	 * @throws BizException
	 */
	void validSheetName(Workbook workbook) throws BizException {
		Sheet sheet0 = workbook.getSheetAt(0);
		Sheet sheet1 = workbook.getSheetAt(1);
		if (!GISConstants.IMPORT_SHEET0_NAME.equals(sheet0.getSheetName().trim())) {
			throw new BizException("第一个sheet的名称错误！");
		}
		if (!GISConstants.IMPORT_SHEET1_NAME.equals(sheet1.getSheetName().trim())) {
			throw new BizException("第二个sheet的名称错误！");
		}
	}

	/**
	 * 每个Sheet控制上传条数
	 * @param workbook
	 * @param sheetName
	 * @throws BizException
	 */
	void validTotalRows(Workbook workbook, String sheetName) throws BizException {
		Sheet sheet = workbook.getSheet(sheetName);
		int totalRows = sheet.getPhysicalNumberOfRows();
		if (totalRows > GISConstants.IMPORT_MAX_ROWS ) {
			throw new BizException("每个Sheet页最多只能上传" + GISConstants.IMPORT_MAX_ROWS + "条数据！当前共"
					+ totalRows + "条数据（包含有格式的空行）！");
		}
	}
	/**
	 * 验证配置并获取信息
	 * @param sheetName     sheet的名称
	 * @return
	 * @throws BizException
	 */
	Map<String,List<DictDetailPO>> findTplIfConfig(String sheetName) throws BizException {
		String dictConfigTpl = null, dictConfigAuth = null;
		if (GISConstants.IMPORT_SHEET0_NAME.equals(sheetName)){
			dictConfigTpl = dictConfig.getPointTpl();
		} else if (GISConstants.IMPORT_SHEET1_NAME.equals(sheetName)) {
			dictConfigTpl = dictConfig.getLineTpl();
		}
		dictConfigAuth = dictConfig.getAuthId();

		Map<String, List<DictDetailPO>> map = Maps.newHashMap();
		if (Objects.isNull(dictConfigTpl)) {
			throw new BizException("请联系管理员，需在Nacos中配置管点和管段的模板参数！管点的key值为[dict.pointTpl]，" +
					"管段的key值为[dict.lineTpl]");
		} else {
			Logger.debug("验证管理员在数据库配置的模板信息：");
			List<DictDetailPO> dictDetailPOS =  dictDetailService.findDetailsByTypeVal(dictConfigTpl);
			String format = "格式为：表头,对应英文字段名,数据类型,排序。 如：管点编码,code,varchar,1  如果不清楚，请联系管理员。";
			if (Objects.isNull(dictDetailPOS) | dictDetailPOS.size() == 0) {
				throw new BizException("请在[字典配置]页面为" + dictConfigTpl + "添加模板参数，" +
						"[参数名称]项可以配置成Excel里面的表头，[参数值]项请严格按照格式配置，" + format);
			} else {
				for (DictDetailPO dictDetailPO : dictDetailPOS) {
					String val = dictDetailPO.getVal();
					if(!(Objects.nonNull(val) && val.split(",").length == 4)) {
						throw new BizException(dictConfigTpl + "配置的参数格式不正确，请重新配置，" + format);
					}
				}
				Logger.debug("dict_detail配置模板" + dictConfigTpl + "数据：" + dictDetailPOS);
				map.put("dictConfigTpl", dictDetailPOS);
			}
		}

		if (Objects.isNull(dictConfigAuth)) {
			throw new BizException("请联系管理员，需在Nacos中配置权属单位的参数！key值为[dict.authId]");
		} else {
			Logger.debug("验证管理员在数据库配置的权属单位信息：");
			List<DictDetailPO> dictDetailPOS =  dictDetailService.findDetailsByTypeVal(dictConfigAuth);
			String format = "格式为：OCP的资源ID=GIS系统的权属单位ID，如：55=2  OCP如果暂时未给出ID，可以先自己随便定义" +
					"一个，等OCP给出后再更新，如果不清楚，请联系管理员。";
			if (Objects.isNull(dictDetailPOS) | dictDetailPOS.size() == 0) {
				throw new BizException("请在[字典配置]页面为" + dictConfigAuth + "添加权属单位参数，" +
						"[参数名称]项配置成权属单位名称，[参数值]项请严格按照格式配置，" + format);
			} else {
				for (DictDetailPO dictDetailPO : dictDetailPOS) {
					String val = dictDetailPO.getVal();
					if(!(Objects.nonNull(val) && val.split("=").length == 2)) {
						throw new BizException(dictConfigTpl + "配置的参数格式不正确，请重新配置，" + format);
					}
				}
				Logger.debug("dict_detail配置权属单位" + dictConfigAuth + "数据：" + dictDetailPOS);
				map.put("dictConfigAuth", dictDetailPOS);
			}
		}

		return map;
	}

	/**
	 * 验证表头是否在数据库中配置。
	 * @param workbook      Excel
	 * @param sheetName     sheet的名称
	 * @throws BizException
	 */
	Map<String,HashMap> valid_GetExcelHeader(Workbook workbook, String sheetName) throws BizException {
		Map<String, List<DictDetailPO>> dictConfigMap = findTplIfConfig(sheetName);
		// 数据字典中配置的模板信息
		List<DictDetailPO> detailTplPOList = dictConfigMap.get("dictConfigTpl");
		// 数据字典中配置的权属单位
		List<DictDetailPO> authIdPOList = dictConfigMap.get("dictConfigAuth");
		// 数据库中配置的模板信息-字段中文名, 数据类型
		HashMap<String, String> nameDataTypeMap = Maps.newHashMap();
		// Excel中第几列，数据类型分类
		HashMap<Integer, String> cellDataTypeMap = Maps.newHashMap();
		// Excel中第几列，字段中文名
		HashMap<Integer, String> cellIdxHeaderMap = Maps.newHashMap();
		// 装所有类型名称
		HashMap<Integer, List<String>> allDevTypeNames = Maps.newHashMap();

		detailTplPOList.stream().forEach(dictDetailPO -> {
			String[] vals = dictDetailPO.getVal().split(",");
			if (Objects.nonNull(vals)) {
				nameDataTypeMap.put(vals[0],vals[2]);
			}
		});
		Sheet sheet = workbook.getSheet(sheetName);
		Row header = sheet.getRow(0);
		int firstCellNum = header.getPhysicalNumberOfCells();
		if (firstCellNum != detailTplPOList.size()) {
			throw new BizException("表头的名称数量和数据库字典配置的模板参数数量不匹配，" +
					"当前Excel有" + firstCellNum + "个，数据库中的参数配置了" +
					detailTplPOList.size() + "个！");
		}
		for (int i = 0; i < firstCellNum; i ++) {
			Cell cell = header.getCell(i);
			String headerName = cell.getStringCellValue();
			if (Objects.nonNull(headerName) && !StringUtils.isEmpty(headerName)) {
				headerName.trim();
			}
			if (!nameDataTypeMap.containsKey(headerName)) {
				throw new BizException("表头中的[" + headerName + "]未在字典配置中配置，请先配置！");
			} else {
				String typeName = nameDataTypeMap.get(headerName);
				String category = pgTypeDAO.selectCategoryByTypname(typeName);
				if (Objects.isNull(category)) {
					throw new BizException("字典配置中配置的数据类型[" + typeName + "]错误，PG数据库中无此数据类型，请确认！");
				}
				cellDataTypeMap.put(i, category);
				cellIdxHeaderMap.put(i, headerName);
			}
		}
		List<ShareDevTypePO> allDevLeafType = shareDevTypePOMapper.findAllDevLeafType();
		List<String> allDevTypeList = Lists.newArrayList();
		if(Objects.nonNull(allDevLeafType) && allDevLeafType.size() > 0) {
			allDevLeafType.stream().forEach(shareDevTypePO -> {
				allDevTypeList.add(shareDevTypePO.getName());
			});
		}
		allDevTypeNames.put(KEY, allDevTypeList);

		HashMap<Integer, List<String>> authIdMap = Maps.newHashMap();
		List<String> authIdNameList = Lists.newArrayList();
		if (Objects.nonNull(authIdPOList) && authIdPOList.size() > 0) {
			authIdPOList.stream().forEach(dictDetailPO -> {
				authIdNameList.add(dictDetailPO.getName());
			});
		}
		authIdMap.put(KEY, authIdNameList);

		Map<String, HashMap> map = Maps.newHashMap();
		map.put("cellDataTypeMap",cellDataTypeMap);
		map.put("cellIdxHeaderMap", cellIdxHeaderMap);
		map.put("allDevTypeNames", allDevTypeNames);
		map.put("authIdMap", authIdMap);
		return map;
	}

	/**
	 * 验证当前单元格的数据类型是否和模板中配置的数据类型是否匹配
	 * @param cellStringVal         表格中单元格中的数据
	 * @return
	 * @throws BizException
	 */
	boolean validCellDataType(String cellStringVal, String category) throws BizException {
		boolean bool = true;
		try {
			if (EPGDataTypeCategory.N.getCode().equals(category)) {
				bool = ComUtil.verifyNumber(cellStringVal);
			} else if (EPGDataTypeCategory.D.getCode().equals(category)) {
				bool = ComUtil.verifyExcelInputDateTime(cellStringVal);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			throw new BizException("验证当前单元格的数据类型是否和模板中配置的数据类型是否匹配出错！");
		}
		return bool;
	}

	/**
	 * 验证当前单元格的设备类型在数据库中是否存在
	 * @param typeName      Excel中的设备类型名称
	 * @return
	 * @throws BizException
	 */
	boolean validCellTypeName(String typeName) throws BizException {
		boolean bool = true;
		try {
			if (Objects.nonNull(typeName) && !StringUtils.isEmpty(typeName)) {
				typeName.trim();
			}
			ShareDevTypePO shareDevTypePO = shareDevTypePOMapper.selectByTypeName(typeName);
			if (Objects.isNull(shareDevTypePO)) {
				bool = false;
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			throw new BizException("验证当前单元格的设备类型出错！");
		}
		return bool;
	}

	/**
	 * 日期格式转换
	 * @param cell
	 * @param cellValue
	 * @return
	 */
	String handleDateCell(Cell cell) {
		Date cellValue = cell.getDateCellValue();
//		Short dateStyle = HSSFDataFormat.getBuiltinFormat("m/d/yy");
		Short timeStyle = HSSFDataFormat.getBuiltinFormat("h:mm:ss");
		Short cellStyle = cell.getCellStyle().getDataFormat();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		if (timeStyle.equals(cellStyle)) {
			return sdf2.format(cellValue);
		} else {
			return sdf1.format(cellValue);
		}
	}

	List<Map<String, String>> getExcelDataList(Workbook workbook, String sheetName) throws BizException {
		// 验证表头
		Map<String,HashMap> headerMap = valid_GetExcelHeader(workbook, sheetName);
		// Excel中第几列，数据类型分类
		HashMap<Integer, String> cellDataTypeMap = headerMap.get("cellDataTypeMap");
		// Excel中第几列，字段中文名
		HashMap<Integer, String> cellIdxHeaderMap = headerMap.get("cellIdxHeaderMap");

		Sheet sheet = workbook.getSheet(sheetName);
		int total = sheet.getPhysicalNumberOfRows();

		Set<String> pointCodeSets = Sets.newHashSet();
		Set<String> lineCodeSets = Sets.newHashSet();
		List<List<Map<String, String>>> allDataList = Lists.newArrayList();
		for (int i = 1; i < total; i ++ ){
			Row row = sheet.getRow(i);
			int cells = row.getPhysicalNumberOfCells();
			List<Map<String, String>> excelDataList = Lists.newArrayList();
			Map<String, String> dataMap = Maps.newHashMap();
			for (int j = 0; j < cells; j ++) {
				String category = cellDataTypeMap.get(j);
				Cell cell = row.getCell(j);
				String cellStringVal = "";
				if (Objects.nonNull(cell)) {
					if (EPGDataTypeCategory.N.getCode().equals(category)) {
						cell.setCellType(CellType.STRING);
						cellStringVal = cell.getStringCellValue();
					} else if (EPGDataTypeCategory.D.getCode().equals(category)) {
						cellStringVal = handleDateCell(cell);
					} else {
						cellStringVal = cell.getStringCellValue();
					}
				}
				if (!StringUtils.isEmpty(cellStringVal)) {
					cellStringVal.trim();
				}
				String cellAddrDesc = "第[" + ( i + 1 ) + "]行第[" + ( j + 1 ) + "]列";
				String headerName = cellIdxHeaderMap.get(j);

				// 如果是validHeader字段数据，都不能为空
				if (validHeader.contains(headerName)) {
					if (StringUtils.isEmpty(cellStringVal)) {
						throw new BizException(cellAddrDesc + "的数据不能为空！");
					}
				}

				// 如果是管段，那么材质不能为空
				if (GISConstants.IMPORT_SHEET1_NAME.equals(sheetName) && GISConstants.MATERIAL_CHN.equals(headerName)) {
					if (StringUtils.isEmpty(cellStringVal)) {
						throw new BizException(cellAddrDesc + "的数据不能为空！");
					}
				}

				// 验证单元格值的数据类型是否正确：数值型 | 字符型 | 日期时间型
				boolean dataTypeStat = validCellDataType(cellStringVal, category);
				if (!dataTypeStat) {
					throw new BizException(cellAddrDesc + "的数据格式不正确，请确认！");
				}

				// 验证类别名称是否合法
				if (GISConstants.DEV_TYPE_NAME_CHN.equals(headerName)) {
					Map<Integer, List<String>> allDevTypeNames = headerMap.get("allDevTypeNames");
					List<String> allDevTypeList = allDevTypeNames.get(KEY);
					if (!allDevTypeList.contains(cellStringVal)) {
						throw new BizException(cellAddrDesc + "的设备类型[" + cellStringVal + "]在数据库中不存在，请确认是" +
								"否新增类型，如果是，请联系管理员添加；如果不是，请更正数据后重新上传！");
					}
				}

				// 验证权属单位是否合法
				if (GISConstants.DATA_AUTH_CHN.equals(headerName)) {
					Map<Integer, List<String>> authIdMap = headerMap.get("authIdMap");
					List<String> authIdNameList = authIdMap.get(KEY);
					if (!authIdNameList.contains(cellStringVal)) {
						throw new BizException(cellAddrDesc + "的权属单位[" + cellStringVal + "]在数据库中不存在，请确认！");
					}
				}

				// 用以验证管点的编码是否重复
				if (GISConstants.IMPORT_SHEET0_NAME.equals(sheetName) && GISConstants.POINT_CODE_CHN.equals(headerName)) {
					pointCodeSets.add(cellStringVal);
				}

				// 用以验证管段的起点和终点编码是否重复
				StringBuffer lineCode = new StringBuffer();
				if (GISConstants.IMPORT_SHEET1_NAME.equals(sheetName)){
					if (GISConstants.LINE_START_CODE_CHN.equals(headerName)) {
						lineCode.append(cellStringVal);
					} else if (GISConstants.LINE_END_CODE_CHN.equals(headerName)) {
						lineCode.append(cellStringVal);
						lineCodeSets.add(String.valueOf(lineCode));
					}

				}

				// 添加数据
				dataMap.put(headerName,cellStringVal);
			}
			System.out.println(dataMap);
			excelDataList.add(dataMap);
		}

		if(GISConstants.IMPORT_SHEET0_NAME.equals(sheetName)) {
			if (pointCodeSets.size() != total - 1) {
				throw new BizException(GISConstants.POINT_CODE_CHN + "有重复编码，请更正后上传！");
			}
		}
		if(GISConstants.IMPORT_SHEET1_NAME.equals(sheetName)) {
			if (lineCodeSets.size() != total -1 ) {
				throw new BizException(GISConstants.LINE_START_CODE_CHN + "和" + GISConstants.LINE_END_CODE_CHN + "有重复编码，请更正后上传！");
			}
		}

//		allDataList.forEach(list -> {
//			System.out.println(list);
//		});
		return null;
	}

	/**
	 * 保存设备数据
	 * @param workbook
	 * @return
	 * @throws BizException
	 */
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = RuntimeException.class)
	public Boolean saveDevData(Workbook workbook) throws BizException {
		getExcelDataList(workbook, GISConstants.IMPORT_SHEET0_NAME);

		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = RuntimeException.class)
	public void saveTplAttr(String tplConfigVal) throws BizException {
		try{
			tplConfigVal = dictConfig.getPointTpl();
			List<DictDetailPO> dictDetailPOS =  dictDetailService.findDetailsByTypeVal(tplConfigVal);
			List<GisDevTplAttrPO> tplAttrPOList = Lists.newArrayList();
			if (Objects.nonNull(dictDetailPOS) && dictDetailPOS.size() > 0) {
				dictDetailPOS.stream().forEach(dictDetailPO -> {
					GisDevTplAttrPO gisDevTplAttrPO = new GisDevTplAttrPO();
					String confVal = dictDetailPO.getVal();
					String[] confValArray = confVal.split(",");
					int i = 0;
					gisDevTplAttrPO.setFieldDesc(confValArray[i++]);
					gisDevTplAttrPO.setFieldName(confValArray[i++]);
					gisDevTplAttrPO.setDataType(confValArray[i++]);
					gisDevTplAttrPO.setIdx(Short.parseShort(confValArray[i]));
					gisDevTplAttrPO.setTypeId(dictDetailPO.getTypeId());
					tplAttrPOList.add(gisDevTplAttrPO);
				});
			}
			gisDevTplAttrService.batchInsertSelective(tplAttrPOList);
		} catch (Exception e) {
			Logger.error("保存字段模板信息失败！");
			e.printStackTrace();
			throw new BizException(e);
		}
	}
}
