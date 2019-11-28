package com.jdrx.gis.service.dataManage;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jdrx.gis.beans.constants.basic.EPGDataTypeCategory;
import com.jdrx.gis.beans.constants.basic.EupdateAndInsert;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.entry.basic.*;
import com.jdrx.gis.beans.entry.dataManage.DevSaveParam;
import com.jdrx.gis.beans.entry.user.SysOcpUserPo;
import com.jdrx.gis.beans.vo.datamanage.ImportVO;
import com.jdrx.gis.config.DictConfig;
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
import com.jdrx.gis.util.ComUtil;
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
	Map<String,List<DictDetailPO>> findTplIfConfig() throws BizException {
		String dictConfigAuth;
		dictConfigAuth = dictConfig.getAuthId();

		Map<String, List<DictDetailPO>> map = Maps.newHashMap();

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
						throw new BizException(dictConfigAuth + "配置的参数格式不正确，请重新配置，" + format);
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
		Map<String, List<DictDetailPO>> dictConfigMap = findTplIfConfig();
		// 数据字典中配置的模板信息
		// List<DictDetailPO> detailTplPOList = dictConfigMap.get("dictConfigTpl");
		// 数据字典中配置的权属单位
		List<DictDetailPO> authIdPOList = dictConfigMap.get("dictConfigAuth");
		// 数据库中配置的模板信息-字段中文名, 数据类型
		HashMap<String, String> nameDataTypeMap = Maps.newHashMap();
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
		if (Objects.nonNull(authIdPOList) && authIdPOList.size() > 0) {
			authIdPOList.stream().forEach(dictDetailPO -> {
				String val = dictDetailPO.getVal();
				String authId = "-1";
				if (Objects.nonNull(val) && !StringUtils.isEmpty(val)) {
					// 这里字典数据配置的格式是 opc的资源=gis系统自定义的权限id,如34=1，我们把1又和广安区映射
					String[] gisAuthId = val.split("=");
					authId = gisAuthId[1];
				}
				// 要求数据权属名称不能重复
				authMap.put(dictDetailPO.getName(), authId);
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

				// 如果是validHeader数组的字段数据，都不能为空
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

				// 如果是管点，X坐标不能为空
				if (GISConstants.IMPORT_SHEET0_NAME.equals(sheetName) && GISConstants.X_CHN.equals(headerName)) {
					if (StringUtils.isEmpty(cellStringVal)) {
						throw new BizException(cellAddrDesc + "的数据不能为空！");
					} else if (cellStringVal.length() > 16) {
						throw new BizException(cellAddrDesc + "的数据长度超过16位，请更改数据内容或把单元格式更改成文本格式！");
					}
				}
				// 如果是管点，Y坐标不能为空
				if (GISConstants.IMPORT_SHEET0_NAME.equals(sheetName) && GISConstants.Y_CHN.equals(headerName)) {
					if (StringUtils.isEmpty(cellStringVal)) {
						throw new BizException(cellAddrDesc + "的数据不能为空！");
					}  else if (cellStringVal.length() > 16) {
						throw new BizException(cellAddrDesc + "的数据长度超过16位，请更改数据内容或把单元格式更改成文本格式！");
					}
				}
				// 验证单元格值的数据类型是否正确：数值型 | 字符型 | 日期时间型
				boolean dataTypeStat = validCellDataType(cellStringVal, category);
				if (!dataTypeStat) {
					throw new BizException(cellAddrDesc + "的数据格式不正确，请确认并修改！");
				}

				// 验证类别名称是否合法
				String typeName = null;
				if (GISConstants.DEV_TYPE_NAME_CHN.equals(headerName)) {
					Map<Integer, Map<String, String>> allDevTypeNames = headerMap.get("allDevTypeNames");
					Map<String, String> allDevTypes = allDevTypeNames.get(KEY);
					if (!allDevTypes.containsKey(cellStringVal)) {
						throw new BizException(cellAddrDesc + "的类别名称[" + cellStringVal + "]在数据库中不存在，请确认是" +
								"否新增类型，如果是，请联系管理员添加；如果不是，请更正数据后重新上传！");
					}
					// 把类别名称转为ID
					typeName = allDevTypes.get(cellStringVal);
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
					pointCodeSets.add(cellStringVal);
				}

				if (GISConstants.IMPORT_SHEET1_NAME.equals(sheetName)){
					if (GISConstants.LINE_START_CODE_CHN.equals(headerName)) {
						lineCode.append(cellStringVal);
					} else if (GISConstants.LINE_END_CODE_CHN.equals(headerName)) {
						lineCode.append(cellStringVal);
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
		return excelDevList;
	}


	/**
	 * 保存模板，如果有一模一样的模板，直接返回模板ID，模板ID（管点取的是管件，管段取的是水管）
	 * @param tplConfigVal
	 * @param loginUserName
	 * @param typeName
	 * @return
	 * @throws BizException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Long saveTplAttr(String tplConfigVal, String loginUserName, String typeName) throws BizException {
		try{
			Long typeId = shareDevTypePOMapper.getIdByNameForTopHierarchy(typeName);
			boolean hasTypeId = false;
			if (Objects.isNull(typeId)) {
				throw new BizException(typeName + "对应的模板未在数据库表gis_dev_tpl_attr配置，请先配置！");
			} else {
				hasTypeId = true;
			}
			List<Map<String, String>> typeDescList = gisDevTplAttrService.selectTypeIdDescMap();
			List<DictDetailPO> dictDetailPOS =  dictDetailService.findDetailsByTypeVal(tplConfigVal);
			List<GisDevTplAttrPO> tplAttrPOList = Lists.newArrayList();
			StringBuffer sb = new StringBuffer();
			if (Objects.nonNull(dictDetailPOS) && dictDetailPOS.size() > 0) {
				for (DictDetailPO dictDetailPO : dictDetailPOS) {
					GisDevTplAttrPO gisDevTplAttrPO = new GisDevTplAttrPO();
					String confVal = dictDetailPO.getVal();
					String[] confValArray = confVal.split(",");
					int i = 0;
					gisDevTplAttrPO.setFieldDesc(confValArray[i++]);
					sb.append(gisDevTplAttrPO.getFieldDesc()).append(",");
					gisDevTplAttrPO.setFieldName(confValArray[i++]);
					gisDevTplAttrPO.setDataType(confValArray[i++]);
					gisDevTplAttrPO.setIdx(Short.parseShort(confValArray[i]));
					gisDevTplAttrPO.setTplId(typeId);
					gisDevTplAttrPO.setCreateBy(loginUserName);
					gisDevTplAttrPO.setCreateAt(new Date());
					tplAttrPOList.add(gisDevTplAttrPO);
				}
			}
			String configDesc = String.valueOf(sb);
			boolean isExist = false;
			configDesc = configDesc.substring(0, configDesc.lastIndexOf(","));
			if(Objects.nonNull(typeDescList) && typeDescList.size() > 0) {
				for (Map<String, String> map : typeDescList) {
					String fieldDescArray = "";
					String tId = "";
					for(Map.Entry<String, String> entry : map.entrySet()) {
						String key = entry.getKey();
						if("fielddescarray".equals(entry.getKey())) {
							fieldDescArray = entry.getValue();
						}
						if("typeid".equals(entry.getKey())) {
							tId = String.valueOf(entry.getValue());
						}
					}
					String[] fieldsDB = fieldDescArray.split(",");
					String[] configFields = configDesc.split(",");
					Collections.sort(Arrays.asList(fieldsDB));
					Collections.sort(Arrays.asList(configFields));
					fieldDescArray = Joiner.on(",").join(fieldsDB);
					configDesc = Joiner.on(",").join(configFields);
					if (fieldDescArray.equals(configDesc)) {
						isExist = true;
						typeId = Long.parseLong(tId);
						break;
					}

				}
			}
			// 如果数据库不存在相同的模板再插入数据库
			if (!isExist) {
				if (hasTypeId) {
					gisDevTplAttrService.delByTypeId(typeId);
				}
				gisDevTplAttrService.batchInsertSelective(tplAttrPOList);
			}
			return typeId;
		} catch (Exception e) {
			Logger.error("保存字段模板信息失败！");
			e.printStackTrace();
			throw new BizException(e);
		}
	}

	/**
	 * 保存Excel数据
	 * @param devSaveParam
	 * @return
	 * @throws BizException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean saveExcelData(DevSaveParam devSaveParam) throws BizException {
		boolean result = false;
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
				e2 = gisDevExtService.splitBatchInsert(gisDevExtPOS);
			}
			Logger.debug("share_dev add " + e1 + " rows, and gis_dev_ext add " + e2 + " rows");
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			new BizException("保存设备数据失败！");
		}
		return result;
	}

	/**
	 * 更新数据
	 * @param devSaveParam
	 * @return
	 * @throws BizException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean updateDBData(DevSaveParam devSaveParam) throws BizException {
		boolean result = false;
		devSaveParam.setSaveFlag(2);
		Map<String, List> buildMap = buildDevPO(devSaveParam);
		try {
			List<ShareDevPO> shareDevPOS = buildMap.get(GISConstants.SHARE_DEV_S);
			List<GISDevExtPO> gisDevExtPOS = buildMap.get(GISConstants.GIS_DEV_EXT_S);
			int e1 = 0, e2 = 0;
			if (Objects.nonNull(shareDevPOS) && shareDevPOS.size() > 0) {
				e1 = shareDevService.splitBatchUpdate(shareDevPOS);
			}
			if(Objects.nonNull(gisDevExtPOS) && gisDevExtPOS.size() > 0) {
				e2 = gisDevExtService.splitBatchUpdate(gisDevExtPOS);
			}
			Logger.debug("share_dev update " + e1 + " rows, and gis_dev_ext update " + e2 + " rows");
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			new BizException("保存设备数据失败！");
		}
		return result;
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
							.setLineStartX(shareDevPOStart.getLat())
							.setLineStartY(shareDevPOStart.getLng())
							.setLineEndX(shareDevPOEnd.getLat())
							.setLineEndY(shareDevPOEnd.getLng())
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
				String caliber = Objects.nonNull(map.get(GISConstants.CALIBER_CHN)) ? String.valueOf(map.get(GISConstants.CALIBER_CHN)) : null;
				String material = Objects.nonNull(map.get(GISConstants.MATERIAL_CHN)) ? String.valueOf(map.get(GISConstants.MATERIAL_CHN)) : null;
				String belongTo = Objects.nonNull(map.get(GISConstants.AUTH_ID_S)) ? String.valueOf(map.get(GISConstants.AUTH_ID_S)) : null;
				HashMap dataInfoTempMap = (HashMap) map.get(GISConstants.DATA_INFO);
				Object dataInfoStr =  dataInfoTempMap.get("value");
				PGobject jsonObject = new PGobject();
				try {
					jsonObject.setValue(String.valueOf(dataInfoStr));
					jsonObject.setType("jsonb");
				} catch (SQLException e) {
					e.printStackTrace();
				}

				Date creatAt = new Date();
				PGgeometry geom = null;
				String devCode = null;
				if (p_l == 1) {
					devCode = pointCode;
					geom = (PGgeometry) codeGeomMap.get(pointCode);
				} else if (p_l == 2) {
					devCode = lineCode;
					geom = (PGgeometry) codeGeomMap.get(lineCode);
				}
				// share_dev数据
				shareDevPO.setId(String.valueOf(devId));
				shareDevPO.setTypeId(typeId);
				shareDevPO.setName(name);
				shareDevPO.setLng(Objects.nonNull(map.get(GISConstants.Y_CHN)) ? String.valueOf(map.get(GISConstants.Y_CHN)) : "");
				shareDevPO.setLat(Objects.nonNull(map.get(GISConstants.X_CHN)) ? String.valueOf(map.get(GISConstants.X_CHN)) : "");
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
		boolean result = false;
		DevSaveParam devSaveParam = new DevSaveParam();
		devSaveParam.setLoginUserName(loginUserName);
		devSaveParam.setExistsCodes(existsExtPOs);
		if (Objects.nonNull(pointDataList)) { // 点
			devSaveParam.setDataMapList(pointDataList);
			devSaveParam.setSheetName(GISConstants.IMPORT_SHEET0_NAME);

			if (tsBool) {
				updateDBData(devSaveParam);
			}
			result = saveExcelData(devSaveParam);
		}

		if (Objects.nonNull(lineDataList)) { // 线
			devSaveParam.setDataMapList(lineDataList);
			devSaveParam.setSheetName(GISConstants.IMPORT_SHEET1_NAME);
			if (tsBool) {
				updateDBData(devSaveParam);
			}
			result = saveExcelData(devSaveParam);
		}
		importVO.setRetStatus(result);
		importVO.setIsOverride("N");
		importVO.setMsg("Success");
		return importVO;
	}

}
