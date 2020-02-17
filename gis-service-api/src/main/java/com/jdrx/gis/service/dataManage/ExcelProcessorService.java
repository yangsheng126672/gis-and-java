package com.jdrx.gis.service.dataManage;

import au.com.bytecode.opencsv.CSVWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jdrx.gis.beans.constants.basic.EPGDataTypeCategory;
import com.jdrx.gis.beans.constants.basic.EupdateAndInsert;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.entity.basic.*;
import com.jdrx.gis.beans.entity.dataManage.DevSaveParam;
import com.jdrx.gis.beans.entity.user.SysOcpUserPo;
import com.jdrx.gis.beans.vo.datamanage.ImportVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.ShareDevPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.dao.pgSys.PgTypeDAO;
import com.jdrx.gis.dubboRpc.UserRpc;
import com.jdrx.gis.service.analysis.NetsAnalysisService;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.gis.service.basic.GISDeviceService;
import com.jdrx.gis.service.basic.GisDevExtService;
import com.jdrx.gis.service.basic.ShareDevService;
import com.jdrx.gis.service.query.QueryDevService;
import com.jdrx.gis.util.ComUtil;
import com.jdrx.gis.util.JavaFileToFormUpload;
import com.jdrx.gis.util.Neo4jUtil;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.share.service.SequenceDefineService;
import org.apache.poi.ss.usermodel.*;
import org.postgis.PGgeometry;
import org.postgresql.util.PGobject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: liaosijun
 * @Time: 2019/11/4 17:02
 */
@Component
public class ExcelProcessorService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ExcelProcessorService.class);

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

	@Autowired
	private ShareDevService shareDevService;

	@Autowired
	private GISDeviceService gisDeviceService;

	@Autowired
	private SequenceDefineService sequenceDefineService;

	@Autowired
	private GISDevExtPOMapper gisDevExtPOMapper;

	@Autowired
	private ShareDevPOMapper shareDevPOMapper;

	@Autowired
	private NetsAnalysisService netsAnalysisService;

	@Autowired
	private GisDevExtService gisDevExtService;

	@Autowired
	private UserRpc userRpc;

	@Autowired
	private PathConfig pathConfig;

	@Autowired
	private Neo4jUtil neo4jUtil;

	@Autowired
	private QueryDevService queryDevService;

	// 需要判断是否为空的列 line
	private static List<String>  validHeader1 = Lists.newArrayList();

	// 需要判断是否为空的列 point
	private static List<String>  validHeader2 = Lists.newArrayList();

	private final static String CONNECTOR = "-";

	static {
		validHeader1.add(GISConstants.CALIBER_CHN);
		validHeader1.add(GISConstants.DATA_AUTH_CHN);
		validHeader1.add(GISConstants.LINE_START_CODE_CHN);
		validHeader1.add(GISConstants.LINE_END_CODE_CHN);
		validHeader1.add(GISConstants.MATERIAL_CHN);
	}

	static {
		validHeader2.add(GISConstants.DATA_AUTH_CHN);
		validHeader2.add(GISConstants.LINE_START_CODE_CHN);
		validHeader2.add(GISConstants.LINE_END_CODE_CHN);
		validHeader2.add(GISConstants.X_CHN);
		validHeader2.add(GISConstants.Y_CHN);
	}

	private final static Integer KEY = 2464;

	/**
	 * 验证上传文件是否为Excel文件
	 * @param file
	 * @throws BizException
	 */
	public void validSuffix(MultipartFile file) throws BizException{
		String filaName = file.getOriginalFilename();
		if (Objects.nonNull(filaName)) {
			String suffix = filaName.substring(filaName.lastIndexOf(".") + 1);
			if (!"xls".equals(suffix) && !"xlsx".equals(suffix)) {
				throw new BizException("只能上传Excel文件");
			}
		}
	}

	/**
	 * 固定sheet名字，预防或提醒管点和管段数据别放混了
	 * @param workbook
	 * @throws BizException
	 */
	public void validSheetName(Workbook workbook) throws BizException {
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
	 * @return
	 * @throws BizException
	 */
	private List<DictDetailPO> findTplIfConfig() throws BizException {
		String dictConfigAuth = dictConfig.getAuthId();
		if (Objects.isNull(dictConfigAuth)) {
			throw new BizException("请联系管理员，需在Nacos中配置权属单位的参数！key值为[dict.authId]");
		} else {
			List<DictDetailPO> dictDetailPOS =  dictDetailService.findDetailsByTypeVal(dictConfigAuth);
			if (Objects.isNull(dictDetailPOS) | dictDetailPOS.size() == 0) {
				throw new BizException("请在[字典配置]页面为" + dictConfigAuth + "添加权属单位参数，" +
						"[参数名称]项配置成权属单位名称，[参数值]配置成机构ID");
			} else {
				Logger.debug("dict_detail配置权属单位" + dictConfigAuth + "数据：" + dictDetailPOS);
			}
			return dictDetailPOS;
		}
	}

	/**
	 * 验证表头是否在数据库中配置。
	 * @param workbook      Excel
	 * @param sheetName     sheet的名称
	 * @throws BizException
	 */
	Map<String,HashMap> valid_GetExcelHeader(Workbook workbook, String sheetName) throws BizException {
		// Excel中第几列，数据类型分类
		HashMap<Integer, String> cellDataTypeMap = Maps.newHashMap();
		// Excel中第几列，字段中文名
		HashMap<Integer, String> cellIdxHeaderMap = Maps.newHashMap();
		// 装所有类型名称
		HashMap<Integer, HashMap<String, String>> allDevTypeNames = Maps.newHashMap();
		// 模板信息中，字段中文名，字段英文名
		HashMap<String, String> fieldMap = Maps.newHashMap();
		List<GisDevTplAttrPO> attrPOList = Lists.newArrayList();
		if(GISConstants.IMPORT_SHEET0_NAME.equals(sheetName)) {
			attrPOList = gisDevTplAttrService.selectTplByTplName(GISConstants.TOP_TPL_1_CHN);
		} else if (GISConstants.IMPORT_SHEET1_NAME.equals(sheetName)) {
			attrPOList = gisDevTplAttrService.selectTplByTplName(GISConstants.TOP_TPL_2_CHN);
		}
		Map<String, String> caMap = Maps.newHashMap();
		attrPOList.stream().forEach(attrPO -> {
			caMap.put(attrPO.getFieldDesc(), attrPO.getDataType());
			fieldMap.put(attrPO.getFieldDesc(), attrPO.getFieldName());
		});
		Sheet sheet = workbook.getSheet(sheetName);
		Row header = sheet.getRow(0);
		int firstCellNum = header.getPhysicalNumberOfCells();
		for (int i = 0; i < firstCellNum; i ++) {
			Cell cell = header.getCell(i);
			String headerName = cell.getStringCellValue();

			if (Objects.nonNull(headerName) && !StringUtils.isEmpty(headerName)) {
				headerName.trim();
			}
			if (!caMap.containsKey(headerName)) {
				throw new BizException(sheetName + "中表头[" + headerName + "]在数据库的模板未配置");
			} else {
				String typeName = caMap.get(headerName);
				String category = pgTypeDAO.selectCategoryByTypname(typeName);
				cellDataTypeMap.put(i, category);
				cellIdxHeaderMap.put(i, headerName);
			}
		}
		List<ShareDevTypePO> allDevLeafType = shareDevTypePOMapper.findAllDevLeafType();
		HashMap<String, String> allDevTypes = Maps.newHashMap();
		if(Objects.nonNull(allDevLeafType) && allDevLeafType.size() > 0) {
			allDevLeafType.stream().forEach(shareDevTypePO -> {
				/** 即要通过Excel里面的类别名称找到对应的ID，就必须要求类型中层级为叶子（类型下面挂类型的为
				 * 枝干类型，类型下面直接挂设备数据的叫叶子类型）不能有重复，例如：管件下面有个类型叫 闸阀，
				 * 阀门类型下面也有个闸阀，你让我去匹配哪一个？
				 */
				allDevTypes.put(shareDevTypePO.getName(), String.valueOf(shareDevTypePO.getId()));
			});
		}
		allDevTypeNames.put(KEY, allDevTypes);

		HashMap<Integer, Map<String, String>> authIdMap = Maps.newHashMap();
		Map<String, String> authMap = Maps.newHashMap();
		List<DictDetailPO> dictDetailPOS = findTplIfConfig();
		if (Objects.nonNull(dictDetailPOS) && dictDetailPOS.size() > 0) {
			dictDetailPOS.stream().forEach(dictDetailPO -> {
				String val = dictDetailPO.getVal();
				// 要求数据权属名称不能重复
				authMap.put(dictDetailPO.getName(), val);
			});
		}
		authIdMap.put(KEY, authMap);

		Map<String, HashMap> map = Maps.newHashMap();
		map.put("cellDataTypeMap",cellDataTypeMap);
		map.put("cellIdxHeaderMap", cellIdxHeaderMap);
		map.put("allDevTypeNames", allDevTypeNames);
		map.put("authIdMap", authIdMap);
		map.put("fieldMap", fieldMap);
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
	 * @return
	 */
	String handleDateCell(Cell cell) throws BizException{
		String addr = "第" + (cell.getAddress().getRow() + 1) + "行" +  (cell.getAddress().getColumn() + 1) + "列";
		try {
			boolean isDateCell = false;
			try {
				isDateCell = DateUtil.isCellDateFormatted(cell);
			} catch (Exception e) {
				 throw new BizException(addr + "不是日期格式，请更改单元格格式！");
			}
			if (!isDateCell) {
				throw new BizException(addr + "不是日期格式，请更改单元格格式！");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date cellDate = cell.getDateCellValue();
			if (Objects.isNull(cellDate)) {
				return "";
			}
			return sdf.format(cellDate);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(addr + "单元格的日期格式不正确！");
			throw new BizException(e.getMessage());
		}
	}

	/**
	 * 验证并获取Excel的数据
	 * @param workbook
	 * @param sheetName
	 * @return
	 * @throws BizException
	 */
	public List<Map<String, Object>> getExcelDataList(Workbook workbook, String sheetName) throws BizException {
		// 验证表头
		Map<String,HashMap> headerMap = valid_GetExcelHeader(workbook, sheetName);
		// 验证总条数，防止数据量过大
		validTotalRows(workbook, sheetName);
		// Excel中第几列，数据类型分类
		HashMap<Integer, String> cellDataTypeMap = headerMap.get("cellDataTypeMap");
		// Excel中第几列，字段中文名
		HashMap<Integer, String> cellIdxHeaderMap = headerMap.get("cellIdxHeaderMap");
		// 配置的字段中文名和字段英文名
		HashMap<String, String> fieldMap = headerMap.get("fieldMap");

		Sheet sheet = workbook.getSheet(sheetName);
		int total = sheet.getPhysicalNumberOfRows();

		// 用以判断管点的编码是否重复，有重复请修改后导入
		Set<String> pointCodeSets = Sets.newHashSet();
		// 用以判断管段的编码是否重复，有重复请修改后导入
		Set<String> lineCodeSets = Sets.newHashSet();
		// 存放数据
		List<Map<String, Object>> excelDevList = Lists.newArrayList();
		// 获取实际列数
		int cells = sheet.getRow(0).getPhysicalNumberOfCells();
		// 遍历数据，有非法数据抛出异常信息
		for (int i = 1; i < total; i ++ ){
			Row row = sheet.getRow(i);
			if (Objects.isNull(row)) {
				throw new BizException("请检查" + sheetName + "的数据格式是否正确，确认是否含有有格式的空单元格，确认是否有误操作导致非可视区单元格有内容等等");
			}
			Map<String, Object> shareDevDataMap = Maps.newHashMap();
			Map<String, String> gisExtDataMap = Maps.newHashMap();
			StringBuffer lineCode = new StringBuffer();
			for (int j = 0; j < cells; j ++) {
				// PG中对数据类型进行分类，数字类型都是N，字符是S，日期时间是D
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
						cell.setCellType(CellType.STRING);
						cellStringVal = cell.getStringCellValue();
					}
				}
				if (!StringUtils.isEmpty(cellStringVal)) {
					cellStringVal.trim();
				}
				String cellAddrDesc = sheetName + " 第[" + ( i + 1 ) + "]行第[" + ( j + 1 ) + "]列";
				// 表头的中文名称
				String headerName = cellIdxHeaderMap.get(j);

				String typeName = null;
				Map<Integer, Map<String, String>> allDevTypeNames = headerMap.get("allDevTypeNames");
				Map<String, String> allDevTypes = allDevTypeNames.get(KEY);

				// 如果是管段，那么材质不能为空
				if (GISConstants.IMPORT_SHEET1_NAME.equals(sheetName) ) {
					if (validHeader1.contains(headerName)) {
						if (StringUtils.isEmpty(cellStringVal)) {
							throw new BizException(cellAddrDesc + "的数据不能为空！");
						}
					}

					if (GISConstants.CALIBER_CHN.equals(headerName)) {
						String caliberTypeName = queryDevService.getCaliberNameByCaliber(Integer.parseInt(cellStringVal));
						shareDevDataMap.put(GISConstants.DEV_TYPE_NAME_CHN, caliberTypeName);
						typeName  = allDevTypes.get(caliberTypeName);
						gisExtDataMap.put(GISConstants.GIS_ATTR_NAME, caliberTypeName);
					}
				}


				// 如果是管点
				if (GISConstants.IMPORT_SHEET0_NAME.equals(sheetName)) {
					if (validHeader2.contains(headerName)) {
						if (StringUtils.isEmpty(cellStringVal)) {
							throw new BizException(cellAddrDesc + "的数据不能为空！");
						}
					}

					if (GISConstants.DEV_TYPE_NAME_CHN.equals(headerName)) {
						if (!allDevTypes.containsKey(cellStringVal)) {
							throw new BizException(cellAddrDesc + "的类别名称[" + cellStringVal + "]在数据库中不存在，请确认是" +
									"否新增类型，如果是，请联系管理员添加；如果不是，请更正数据后重新上传！");
						}
						// 把类别名称转为ID
						typeName = allDevTypes.get(cellStringVal);
					}

					// X坐标不能为空
					if (GISConstants.X_CHN.equals(headerName) | GISConstants.Y_CHN.equals(headerName)) {
						if (cellStringVal.length() > 16) {
							throw new BizException(cellAddrDesc + "的数据长度超过16位，请更改数据内容或把单元格式更改成文本格式！");
						}
					}

				}

				// 验证单元格值的数据类型是否正确：数值型 | 字符型 | 日期时间型
				if (Objects.nonNull(cellStringVal) && !StringUtils.isEmpty(cellStringVal)) {
					boolean dataTypeStat = validCellDataType(cellStringVal, category);
					if (!dataTypeStat) {
						throw new BizException(cellAddrDesc + "的数据格式不正确，请确认并修改！");
					}
				}

				// 验证权属单位是否合法
				String authId = null;
				if (GISConstants.DATA_AUTH_CHN.equals(headerName)) {
					Map<Integer,  Map<String, String>> authIdMap = headerMap.get("authIdMap");
					Map<String, String> authIdNameMap = authIdMap.get(KEY);
					if (!authIdNameMap.containsKey(cellStringVal)) {
						throw new BizException(cellAddrDesc + "的权属单位[" + cellStringVal + "]在数据库中不存在，请确认！");
					}
					// 数据权限的所属名称转换为ID
					authId = authIdNameMap.get(cellStringVal);
				}

				// 用以验证管点的编码是否重复
				if (GISConstants.IMPORT_SHEET0_NAME.equals(sheetName) && GISConstants.POINT_CODE_CHN.equals(headerName)) {
					if (cellStringVal.contains(CONNECTOR)) {
						throw new BizException("编码格式不正确，不能含有" + CONNECTOR);
					}
					pointCodeSets.add(cellStringVal);
				}

				if (GISConstants.IMPORT_SHEET1_NAME.equals(sheetName)){
					if (GISConstants.LINE_START_CODE_CHN.equals(headerName)) {
						if (cellStringVal.contains(CONNECTOR)) {
							throw new BizException("编码格式不正确，不能含有" + CONNECTOR);
						}
						lineCode.append(cellStringVal);
					} else if (GISConstants.LINE_END_CODE_CHN.equals(headerName)) {
						if (cellStringVal.contains(CONNECTOR)) {
							throw new BizException("编码格式不正确，不能含有" + CONNECTOR);
						}
						lineCode.append(CONNECTOR).append(cellStringVal);
					}

				}
				// 添加share_dev数据
				shareDevDataMap.put(headerName,cellStringVal);
				// 添加gis_dev_ext
				String en_header = fieldMap.get(headerName);
				gisExtDataMap.put(en_header, cellStringVal);
				if (j == (cells - 1)) {
					try {
						PGobject dataInfo = ComUtil.convertMaptoPGObject(gisExtDataMap);
						shareDevDataMap.put(GISConstants.DATA_INFO, dataInfo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (Objects.nonNull(typeName)) {
					shareDevDataMap.put(GISConstants.DEV_TYPE_NAME_EN, typeName);
				}
				if (Objects.nonNull(authId)) {
					shareDevDataMap.put(GISConstants.AUTH_ID_S, authId);
				}
			}
			lineCodeSets.add(String.valueOf(lineCode));
			excelDevList.add(shareDevDataMap);
		}
		int pointCodeNum = 0, lineCodeNum = 0;
		if (pointCodeSets.size() > 0) {
			pointCodeNum = gisDevExtPOMapper.findCountByCodes(pointCodeSets);
		}
		if (lineCodeSets.size() > 0) {
			lineCodeNum = gisDevExtPOMapper.findCountByCodes(lineCodeSets);
		}
		if (pointCodeNum + lineCodeNum > 0) {
			throw new BizException("部分编码已经存在，请更正后上传！");
		}
		if(GISConstants.IMPORT_SHEET0_NAME.equals(sheetName)) {
			if (pointCodeSets.size() != total - 1) {
				throw new BizException("Excel中" + GISConstants.POINT_CODE_CHN + "有重复编码，请更正后上传！");
			}
		}
		if(GISConstants.IMPORT_SHEET1_NAME.equals(sheetName)) {
			if (lineCodeSets.size() != total -1 ) {
				throw new BizException("Excel中" + GISConstants.LINE_START_CODE_CHN + "和" + GISConstants.LINE_END_CODE_CHN + "有重复编码，请更正后上传！");
			}
		}
		return excelDevList;
	}

	/**
	 * 保存Excel数据
	 * @param devSaveParam
	 * @return
	 * @throws BizException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, String>> saveExcelData(DevSaveParam devSaveParam) throws BizException {
		List<Map<String, String>> codeDevIdMapList = Lists.newArrayList();
		devSaveParam.setSaveFlag(1);
		Map<String, List> buildMap = buildDevPO(devSaveParam);
		try {
			List<ShareDevPO> shareDevPOS = buildMap.get(GISConstants.SHARE_DEV_S);
			List<GISDevExtPO> gisDevExtPOS = buildMap.get(GISConstants.GIS_DEV_EXT_S);
			int e1 = 0, e2 = 0;
			if (Objects.nonNull(shareDevPOS) && shareDevPOS.size() > 0) {
				e1 = shareDevService.splitBatchInsert(shareDevPOS);
			}
			if(Objects.nonNull(gisDevExtPOS) && gisDevExtPOS.size() > 0) {
				for (GISDevExtPO gisDevExtPO : gisDevExtPOS){
					Map<String, String> map = Maps.newHashMap();
					map.put(gisDevExtPO.getCode(), gisDevExtPO.getDevId());
					codeDevIdMapList.add(map);
				}
				e2 = gisDevExtService.splitBatchInsert(gisDevExtPOS);
			}
			Logger.debug("share_dev add " + e1 + " rows, and gis_dev_ext add " + e2 + " rows");
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			new BizException("保存设备数据失败！");
		}
		return codeDevIdMapList;
	}

	/**
	 * 更新数据
	 * @param devSaveParam
	 * @return
	 * @throws BizException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, String>> updateDBData(DevSaveParam devSaveParam) throws BizException {
		devSaveParam.setSaveFlag(2);
		Map<String, List> buildMap = buildDevPO(devSaveParam);
		List<Map<String, String>> codeDevIdMapList = Lists.newArrayList();
		try {
			List<ShareDevPO> shareDevPOS = buildMap.get(GISConstants.SHARE_DEV_S);
			List<GISDevExtPO> gisDevExtPOS = buildMap.get(GISConstants.GIS_DEV_EXT_S);
			int e1 = 0, e2 = 0;
			if (Objects.nonNull(shareDevPOS) && shareDevPOS.size() > 0) {
				e1 = shareDevService.splitBatchUpdate(shareDevPOS);
			}
			if(Objects.nonNull(gisDevExtPOS) && gisDevExtPOS.size() > 0) {
				for (GISDevExtPO gisDevExtPO : gisDevExtPOS){
					Map<String, String> map = Maps.newHashMap();
					map.put(gisDevExtPO.getCode(), gisDevExtPO.getDevId());
					codeDevIdMapList.add(map);
				}
				e2 = gisDevExtService.splitBatchUpdate(gisDevExtPOS);
			}
			Logger.debug("share_dev update " + e1 + " rows, and gis_dev_ext update " + e2 + " rows");
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			new BizException("保存设备数据失败！");
		}
		return codeDevIdMapList;
	}

	/**
	 * 创建PO对象，包含gis_dev_ext和share_dev的
	 * @param devSaveParam
	 * @return
	 * @throws BizException
	 */
	public Map<String, List> buildDevPO(DevSaveParam devSaveParam) throws BizException {
		List<ShareDevPO> shareDevPOList = Lists.newArrayList();
		List<GISDevExtPO> gisDevExtPOList = Lists.newArrayList();
		Map<String, Object> codeGeomMap = Maps.newHashMap();
		Map<String, List> buildMap = Maps.newHashMap();
		List<Map<String, Object>> dataMapList = devSaveParam.getDataMapList();
		String sheetName = devSaveParam.getSheetName();
		Map<String, String> existsExtMaps = Maps.newHashMap();
		Map<String, String> existsExtMaps2 = Maps.newHashMap();
		List<GISDevExtPO> existsExtPOs = devSaveParam.getExistsCodes();
		if (Objects.nonNull(existsExtPOs) && existsExtPOs.size() > 0) {
			existsExtPOs.stream().forEach(gisDevExtPO -> {
				String devCode = gisDevExtPO.getCode();
				existsExtMaps.put(devCode, gisDevExtPO.getDevId());
				existsExtMaps2.put(devCode, gisDevExtPO.getDevId());
			});
		}
		if (Objects.nonNull(dataMapList) && dataMapList.size() > 0) {
			List<CodeXYPO> codeXYPOList = Lists.newArrayList();
			for (Map<String, Object> map : dataMapList) {
				String pointCode = null;
				String lineCode = String.valueOf(map.get(GISConstants.LINE_START_CODE_CHN))
						+ "-" + String.valueOf(map.get(GISConstants.LINE_END_CODE_CHN));
				if (GISConstants.IMPORT_SHEET0_NAME.equals(sheetName)) {
					pointCode = String.valueOf(map.get(GISConstants.POINT_CODE_CHN));
					String x = String.valueOf(map.get(GISConstants.X_CHN));
					String y = String.valueOf(map.get(GISConstants.Y_CHN));
					CodeXYPO codeXYPO = new CodeXYPO();
					codeXYPO.setPointX(x)
							.setPointY(y)
							.setPointCode(pointCode)
							.setCode(pointCode);
					codeXYPOList.add(codeXYPO);

				} else if (GISConstants.IMPORT_SHEET1_NAME.equals(sheetName)) {
					String startCode = String.valueOf(map.get(GISConstants.LINE_START_CODE_CHN));
					String endCode = String.valueOf(map.get(GISConstants.LINE_END_CODE_CHN));
					CodeXYPO codeXYPO = new CodeXYPO();
					GISDevExtPO start = gisDevExtPOMapper.selectByCode(startCode);
					if (Objects.isNull(start)) {
						throw new BizException(sheetName + "的" + GISConstants.LINE_START_CODE_CHN + "[" + startCode + "]在数据库中不存在！" +
								"便无法找到对应的坐标，请确认！");
					}
					GISDevExtPO end = gisDevExtPOMapper.selectByCode(endCode);
					if (Objects.isNull(end)) {
						throw new BizException(sheetName + "的" + GISConstants.LINE_END_CODE_CHN + "[" + endCode + "]在数据库中不存在！" +
								"便无法找到对应的坐标，请确认！");
					}

					ShareDevPO shareDevPOStart = shareDevPOMapper.selectByPrimaryKey(start.getDevId());
					ShareDevPO shareDevPOEnd = shareDevPOMapper.selectByPrimaryKey(end.getDevId());
					codeXYPO.setLineCode(lineCode)
							.setLineStartX(shareDevPOStart.getLng())
							.setLineStartY(shareDevPOStart.getLat())
							.setLineEndX(shareDevPOEnd.getLng())
							.setLineEndY(shareDevPOEnd.getLat())
							.setCode(lineCode);
					codeXYPOList.add(codeXYPO);
				}

			}
			List<Map<String, Object>> codeGeomList = null;
			if (GISConstants.IMPORT_SHEET0_NAME.equals(sheetName)) {
				codeGeomList = geomConvertFromExt(codeXYPOList, 1);
			} else if (GISConstants.IMPORT_SHEET1_NAME.equals(sheetName)) {
				codeGeomList = geomConvertFromExt(codeXYPOList, 2);
			}
			if (Objects.nonNull(codeGeomList)) {
				for (Map<String, Object> map : codeGeomList) {
					codeGeomMap.put(String.valueOf(map.get("code")), map.get("geom"));
				}
			}
			for (Map<String, Object> map : dataMapList) {
				ShareDevPO shareDevPO = new ShareDevPO();
				Long typeId = null;
				String pointCode = String.valueOf(map.get(GISConstants.POINT_CODE_CHN));
				String lineCode = String.valueOf(map.get(GISConstants.LINE_START_CODE_CHN))
						+ "-" + String.valueOf(map.get(GISConstants.LINE_END_CODE_CHN));
				String typeIdStr = Objects.nonNull(map.get(GISConstants.DEV_TYPE_NAME_EN)) ? String.valueOf(map.get(GISConstants.DEV_TYPE_NAME_EN)) : null;
				if (Objects.nonNull(typeIdStr)) {
					typeId = Long.parseLong(typeIdStr);
				}
				Long seq = sequenceDefineService.increment(gisDeviceService.sequenceKey());
				String devId = String.format("%04d%s%06d",typeId, GISConstants.PLATFORM_CODE, seq);
				String name = String.valueOf(map.get(GISConstants.DEV_TYPE_NAME_CHN));

				String caliber = Objects.nonNull(map.get(GISConstants.CALIBER_CHN)) ? String.valueOf(map.get(GISConstants.CALIBER_CHN)) : null;

				int p_l = 0;
				if (GISConstants.IMPORT_SHEET0_NAME.equals(sheetName)) {
					p_l = 1;
					if (EupdateAndInsert.INSERT.getVal() == devSaveParam.getSaveFlag()) {
						if (existsExtMaps.containsKey(pointCode)) {
							continue;
						}
					} else if (EupdateAndInsert.UPDATE.getVal() == devSaveParam.getSaveFlag()) {
						if (!existsExtMaps.containsKey(pointCode)) {
							continue;
						}
					}
				} else if (GISConstants.IMPORT_SHEET1_NAME.equals(sheetName)) {
					p_l = 2;
					if (EupdateAndInsert.INSERT.getVal() == devSaveParam.getSaveFlag()) {
						if (existsExtMaps.containsKey(lineCode)) {
							continue;
						}
					} else if (EupdateAndInsert.UPDATE.getVal() == devSaveParam.getSaveFlag()) {
						if (!existsExtMaps.containsKey(lineCode)) {
							continue;
						}
					}
				}
				String material = Objects.nonNull(map.get(GISConstants.MATERIAL_CHN)) ? String.valueOf(map.get(GISConstants.MATERIAL_CHN)) : null;
				String belongTo = Objects.nonNull(map.get(GISConstants.AUTH_ID_S)) ? String.valueOf(map.get(GISConstants.AUTH_ID_S)) : null;

				// data_info数据
				HashMap dataInfoTempMap = (HashMap) map.get(GISConstants.DATA_INFO);
				Object dataInfoStr =  dataInfoTempMap.get("value");
				JSONObject jsb = JSON.parseObject(String.valueOf(dataInfoStr));
				Date creatAt = new Date();
				PGgeometry geom = null;
				String devCode = null;
				if (p_l == 1) {
					devCode = pointCode;
					geom = (PGgeometry) codeGeomMap.get(pointCode);
				} else if (p_l == 2) {
					devCode = lineCode;
					geom = (PGgeometry) codeGeomMap.get(lineCode);
					Double pipe_length = gisDevExtPOMapper.getLengthByGeomStr(geom.getValue());
					jsb.put(GISConstants.PIPE_LENGTH, pipe_length);
				}

				PGobject jsonObject = new PGobject();
				try {
					jsonObject.setValue(jsb.toJSONString());
					jsonObject.setType("jsonb");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				// share_dev数据
				shareDevPO.setId(String.valueOf(devId));
				shareDevPO.setTypeId(typeId);
				shareDevPO.setName(name);
				shareDevPO.setLng(Objects.nonNull(map.get(GISConstants.X_CHN)) ? String.valueOf(map.get(GISConstants.X_CHN)) : "");
				shareDevPO.setLat(Objects.nonNull(map.get(GISConstants.Y_CHN)) ? String.valueOf(map.get(GISConstants.Y_CHN)) : "");
				shareDevPO.setAddr(String.valueOf(map.get(GISConstants.DEV_ADDR_CHN)));
				if (EupdateAndInsert.INSERT.getVal() == devSaveParam.getSaveFlag()) {
					shareDevPO.setCreateAt(creatAt);
					shareDevPO.setCreateBy(devSaveParam.getLoginUserName());
				} else if (EupdateAndInsert.UPDATE.getVal() == devSaveParam.getSaveFlag()) {
					shareDevPO.setUpdateAt(creatAt);
					shareDevPO.setUpdateBy(devSaveParam.getLoginUserName());
					shareDevPO.setId(existsExtMaps.get(devCode));
				}
				shareDevPO.setPlatformCode(GISConstants.PLATFORM_CODE);
				shareDevPOList.add(shareDevPO);
				// gis_dev_ext数据
				GISDevExtPO gisDevExtPO = new GISDevExtPO();
				gisDevExtPO.setDevId(devId);
				gisDevExtPO.setName(name);
				gisDevExtPO.setCode(devCode);
				if (Objects.nonNull(caliber)) {
					gisDevExtPO.setCaliber(Integer.parseInt(caliber));
				}
				gisDevExtPO.setMaterial(material);
				gisDevExtPO.setGeom(geom.getValue());
				gisDevExtPO.setTplTypeId(typeId);
				gisDevExtPO.setDataInfo(jsonObject);
				if (EupdateAndInsert.INSERT.getVal() == devSaveParam.getSaveFlag()) {
					gisDevExtPO.setCreateAt(creatAt);
					gisDevExtPO.setCreateBy(devSaveParam.getLoginUserName());
				} else if (EupdateAndInsert.UPDATE.getVal() == devSaveParam.getSaveFlag()) {
					gisDevExtPO.setUpdateAt(creatAt);
					gisDevExtPO.setUpdateBy(devSaveParam.getLoginUserName());
					gisDevExtPO.setDevId(existsExtMaps2.get(devCode));
				}
				if (Objects.nonNull(belongTo)) {
					gisDevExtPO.setBelongTo(Long.parseLong(belongTo));
				}
				gisDevExtPOList.add(gisDevExtPO);
			}

		}
		buildMap.put(GISConstants.SHARE_DEV_S, shareDevPOList);
		buildMap.put(GISConstants.GIS_DEV_EXT_S, gisDevExtPOList);
		return buildMap;
	}

	/**
	 * geom转换成文本（批量）
	 * @param codeXYPOs
	 * @param isPoint
	 * @return
	 * @throws BizException
	 */
	public List<Map<String, Object>> geomConvertFromExt(List<CodeXYPO> codeXYPOs, int isPoint) throws BizException {
		if (Objects.isNull(codeXYPOs)) {
			return Lists.newArrayList();
		}
		String srid = netsAnalysisService.getValByDictString(dictConfig.getWaterPipeSrid());
		if (1 == isPoint) {
			codeXYPOs.stream().forEach(codeXYPO -> {
				StringBuffer str = new StringBuffer();
				str.append("POINT(")
						.append(codeXYPO.getPointX() + " ")
						.append(codeXYPO.getPointY() + ")");
				codeXYPO.setStr(String.valueOf(str));
			});
		} else if (2 == isPoint) {
			codeXYPOs.stream().forEach(codeXYPO -> {
				StringBuffer sb = new StringBuffer();
				sb.append("LINESTRING(")
						.append(codeXYPO.getLineStartX() + " ")
						.append(codeXYPO.getLineStartY())
						.append(",")
						.append(codeXYPO.getLineEndX() + " ")
						.append(codeXYPO.getLineEndY() + ")");
				codeXYPO.setStr(String.valueOf(sb));
			});
		}
		int sridInt = Integer.parseInt(srid);
		List<Map<String,Object>> codeGeomList = gisDevExtService.splitFindGeomMapByPointCode(codeXYPOs, sridInt);
		return codeGeomList;
	}

	/**
	 * 根据解析出来的Excel数据，导入到数据库中
	 * @param dataMap           解析的Excel数据
	 * @param userId            登录用户的ID
	 * @param token             token
	 * @return
	 * @throws BizException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ImportVO saveExcelData(Map<String, List> dataMap, Long userId, String token, Integer ts) throws BizException {
		ImportVO importVO = new ImportVO();
		SysOcpUserPo sysOcpUserPo = userRpc.getUserById(userId, token);
		String loginUserName = sysOcpUserPo.getName();
		List<Map<String, Object>> pointDataList = dataMap.get(GISConstants.POINT_LIST_S);
		List<Map<String, Object>> lineDataList = dataMap.get(GISConstants.LINE_LIST_S);
		Set<String> codesSet = Sets.newHashSet();
		if (Objects.nonNull(pointDataList) && pointDataList.size() > 0) {
			for (Map<String, Object> map : pointDataList) {
				codesSet.add(String.valueOf(map.get(GISConstants.POINT_CODE_CHN)));
			}
		}
		if (Objects.nonNull(lineDataList) && lineDataList.size() > 0) {
			for (Map<String, Object> map : lineDataList) {
				String startCode = String.valueOf(map.get(GISConstants.LINE_START_CODE_CHN));
				String endCode = String.valueOf(map.get(GISConstants.LINE_END_CODE_CHN));
				StringBuffer sb = new StringBuffer().append(startCode)
						.append("-")
						.append(endCode);
				codesSet.add(String.valueOf(sb));
			}
		}
		List<GISDevExtPO> existsExtPOs = gisDevExtService.selectByCodes(codesSet);
		boolean tsBool = false;
		if (Objects.nonNull(existsExtPOs) && existsExtPOs.size() > 0) {
			tsBool = true;
		}
		if (tsBool && 1 == ts) {
			importVO.setMsg("excel中有[" + existsExtPOs.size() + "]条数据在数据库中已存在，是否覆盖，请确认");
			importVO.setRetStatus(false);
			importVO.setIsOverride("Y");
			return importVO;
		}
		List<Map<String, String>> result1;
		List<Map<String, String>> result2;
		DevSaveParam devSaveParam = new DevSaveParam();
		devSaveParam.setLoginUserName(loginUserName);
		devSaveParam.setExistsCodes(existsExtPOs);
		if (Objects.nonNull(pointDataList)) { // 点
			devSaveParam.setDataMapList(pointDataList);
			devSaveParam.setSheetName(GISConstants.IMPORT_SHEET0_NAME);
			List<Map<String, String>> pList = Lists.newArrayList();
			if (tsBool) {
				pList = updateDBData(devSaveParam);
			}
			result1 = saveExcelData(devSaveParam);
			result1.addAll(pList);
			pointDataList = putDevId(result1, pointDataList, 0);
			pointDataList = putNodeType(pointDataList);
		}

		if (Objects.nonNull(lineDataList)) { // 线
			devSaveParam.setDataMapList(lineDataList);
			devSaveParam.setSheetName(GISConstants.IMPORT_SHEET1_NAME);
			List<Map<String, String>> pList = Lists.newArrayList();
			if (tsBool) {
				pList = updateDBData(devSaveParam);
			}
			result2 = saveExcelData(devSaveParam);
			result2.addAll(pList);
			lineDataList = putDevId(result2, lineDataList, 1);
		}

		String filePathPoint = writeCsv(pointDataList, "point");
		String filePathLine = writeCsv(lineDataList, "line");
		neo4jUtil.createNodesByCsvPoint(filePathPoint);
		neo4jUtil.createNodesByCsvLine(filePathLine);

		importVO.setRetStatus(true);
		if (tsBool) {
			importVO.setIsOverride("Y");
		} else {
			importVO.setIsOverride("N");
		}
		importVO.setMsg("Success");
		return importVO;
	}

	/**
	 * 写csv文件，便于图数据库导入，一条条创建效率太低下，效率最高的导入方式都需要脱机导入，故选择load csv方式
	 * 导入，既满足速率相对一条条导入有很大提高，又满足不脱机
	 * @param pointDataList
	 * @param title
	 * @return
	 */
	private String writeCsv(List<Map<String, Object>> pointDataList, String title) {
		String path = "";
		try {
			String[] noNeedHeader = {GISConstants.DATA_INFO, GISConstants.DEV_TYPE_NAME_EN};
			Set<String> headerSet = Sets.newHashSet();
			if (Objects.nonNull(pointDataList) && pointDataList.size() > 0) {
				for (Map<String, Object> map : pointDataList) {
					map.keySet().removeAll(Arrays.asList(noNeedHeader));
				}
				Set<String> headers = pointDataList.get(0).keySet();
				headerSet.addAll(headers);
			}
			if (headerSet.contains(GISConstants.CALIBER_CHN)) {
				headerSet.add(GISConstants.GIS_ATTR_CALIBER);
				headerSet.remove(GISConstants.CALIBER_CHN);
			}
			String filePath = pathConfig.getDownloadPath() + "/" + title + ".csv";
			CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8),
					CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
			writer.writeNext(StringUtils.toStringArray(headerSet));
			int total = pointDataList.size();
			int pageSize = GISConstants.EXPORT_PAGESIZE;
			int pageTotal;
			if (total <= pageSize) {
				pageTotal = 1;
			} else {
				pageTotal = total / pageSize == 0 ? total / pageSize : total / pageSize + 1;
			}
			int pageNum = 0;
			while (pageTotal-- > 0) {
				List<Map<String, Object>> subList = pointDataList.subList(pageNum * pageSize,
						(pageNum + 1) * pageSize > total ? total : (pageNum + 1) * pageSize);
				for (Map<String, Object> map : subList) {
					Iterator<String> iterator = headerSet.iterator();
					String[] rows = new String[map.size()];
					for (int i = 0; i < headerSet.size(); i++) {
						String header = iterator.hasNext() ? iterator.next() : "";
						if (GISConstants.GIS_ATTR_CALIBER.equals(header)) {
							header = GISConstants.CALIBER_CHN;
						}
						String txt = Objects.nonNull(map.get(header)) ? String.valueOf(map.get(header)) : "";
						rows[i] = txt;
					}
					writer.writeNext(rows);
				}
				pageNum++;
			}
			writer.flush();
			writer.close();
			path = JavaFileToFormUpload.send(pathConfig.getUploadFileUrl(), filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * 保存设备返回的dev_id，存入图数据库中
	 * @param list
	 * @param exList
	 * @param flag
	 * @return
	 */
	private List<Map<String, Object>> putDevId(List<Map<String, String>> list, List<Map<String, Object>> exList, int flag) {
		if (Objects.isNull(list)) {
			return exList;
		}
		for (Map<String, Object> exMap : exList) {
			for (Map<String, String> map : list) {
				String code = "";
				if (flag == 0) {
					Object codeObj = exMap.get(GISConstants.POINT_CODE_CHN);
					if (Objects.nonNull(codeObj)) {
						code = String.valueOf(codeObj);
					}
				} else if (flag == 1) {
					Object startObj = exMap.get(GISConstants.LINE_START_CODE_CHN);
					Object endObj = exMap.get(GISConstants.LINE_END_CODE_CHN);
					if (Objects.nonNull(startObj) && Objects.nonNull(endObj)) {
						code = String.valueOf(startObj) + "-" + String.valueOf(endObj);
					}
				}
				if (map.containsKey(code)) {
					exMap.put(GISConstants.DEV_ID, map.get(code));
					break;
				}
			}
		}
		return exList;
	}

	/**
	 * 增加管点是否为阀门的标志，存入图数据库，便于爆管分析使用
	 * @param pointDataList
	 * @return
	 */
	private List<Map<String, Object>> putNodeType(List<Map<String, Object>> pointDataList) throws BizException {
		List<String> devIds = Lists.newArrayList();
		for (Map<String, Object> map : pointDataList) {
			devIds.add(Objects.nonNull(map.get(GISConstants.DEV_ID)) ? String.valueOf(map.get(GISConstants.DEV_ID)) : "");
		}
		List<DictDetailPO> details;
		try {
			details = dictDetailService.findDetailsByTypeVal(dictConfig.getCloseableValveTypeIds());
		} catch (BizException e) {
			e.printStackTrace();
			throw new BizException("查询可关闭的阀门类型失败！");
		}
		List<Long> typeIds = null;
		if (Objects.nonNull(details)) {
			typeIds = Lists.newArrayList();
			for (DictDetailPO dictDetailPO : details) {
				typeIds.add(Long.parseLong(dictDetailPO.getVal()));
			}
		}
		// 键devId,值是否为阀门
		List<Map<String, Integer>> nodeTypes = shareDevPOMapper.findNodeType(devIds, typeIds);
		for (Map<String, Object> map : pointDataList) {
			String dev_id = String.valueOf(map.get(GISConstants.DEV_ID));
			for (Map<String, Integer> xMap : nodeTypes) {
				if(xMap.containsValue(dev_id)) {
					map.put(GISConstants.NODE_TYPE, xMap.get(GISConstants.NODE_TYPE));
					break;
				}
			}
		}
		return pointDataList;
	}

}
