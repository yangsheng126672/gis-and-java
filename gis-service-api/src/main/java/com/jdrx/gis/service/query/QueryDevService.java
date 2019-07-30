package com.jdrx.gis.service.query;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jdrx.gis.beans.constants.basic.ELimbLeaf;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.query.*;
import com.jdrx.gis.beans.dto.third.GetPipeTotalLenthDTO;
import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.beans.entry.basic.ShareDevPO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.beans.entry.query.PipeLengthPO;
import com.jdrx.gis.beans.entry.query.SpaceInfTotalPO;
import com.jdrx.gis.beans.vo.query.*;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.ShareDevPOMapper;
import com.jdrx.gis.dao.query.DevQueryDAO;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.gis.service.basic.ShareDevTypeService;
import com.jdrx.gis.util.ComUtil;
import com.jdrx.gis.util.ExcelStyleUtil;
import com.jdrx.gis.util.JavaFileToFormUpload;
import com.jdrx.gis.util.RedisComponents;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
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
	DictConfig dictConfig;

	@Autowired
	DictDetailService dictDetailService;

	@Autowired
	LayerService layerService;

	@Autowired
	private PathConfig pathConfig;

	@Autowired
	private RedisComponents redisComponents;

	@Autowired
	private ShareDevPOMapper shareDevPOMapper;

	/**
	 * 根据传来的设备集合获取第一级图层和图层对应的设备个数
	 * @return
	 */
	public List<SpaceInfTotalPO> findFirstHierarchyDevTypeNum(DevIDsDTO devIDsDTO) throws BizException{
		try {
			String devStr = null;
			Long[] devIds = devIDsDTO.getDevIds();
			List<Long> ids = null;
			if (Objects.nonNull(devIds) && devIds.length > 0) {
				ids = Objects.nonNull(devIds) ? Arrays.asList(devIds) : Lists.newArrayList();
				devStr = Joiner.on(",").join(ids);
			}
			List<SpaceInfTotalPO> list = devQueryDAO.findSpaceInfoByDevIds(devStr);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("获取第一级图层对应的设备个数失败！");
			throw new BizException("获取第一级图层对应的设备个数失败！");
		}
	}

	/**
	 * 根据设备IDs和类型ID获取设备信息
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public List<SpaceInfoVO> findDevListByTypeID(DevIDsForTypeDTO dto, String devIds) throws BizException{
		try {
			List<SpaceInfoVO> list = devQueryDAO.findDevListByTypeID(dto, devIds);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("根据设备IDs和类型ID获取设备信息失败！pid = {} ", dto.getTypeId());
			throw  new BizException("根据设备IDs和类型ID获取设备信息失败！");
		}
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
		try {
			List<FieldNameVO> list = devQueryDAO.findFieldNamesByTypeID(id);
			if (Objects.nonNull(list)) {
				// 设备模板里面是没有配置设备的类型名称的，其实可以配置，但感觉不是很合理
				// 所以这里就把类名称这一列+上来
				FieldNameVO vo = new FieldNameVO();
				vo.setFieldName(GISConstants.DEV_TYPE_NAME);
				vo.setFieldDesc(GISConstants.DEV_TYPE_NAME_DESC);
				list.add(vo);

				for (int i = 0; i < list.size(); i++) {
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
					if (GISConstants.DEV_TYPE_NAME.equals(fieldName)) {
						Collections.swap(list, i, 1);
					}
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("根据类型ID查表头失败！");
			throw new BizException("根据类型ID查表头失败！");
		}
	}

	/**
	 * 获取设备类型下面的设备列表信息
	 * @param id 设备类型ID
	 * @return
	 * @throws BizException
	 */
	/**
	public List<SpaceInfoVO> findDevListByTypeID2(Long id, String devIds) throws BizException {
		List<SpaceInfoVO> list = findDevListByTypeID(id, devIds);
		return list;
	}*/

	/**
	 * 水管口径数量统计，当前按照我们自己定义的大小分类
	 * @return
	 * @throws BizException
	 */
	public List<WaterPipeTypeNumVO> findWaterPipeCaliberSum(RangeDTO rangeDTO) throws BizException{
		List<WaterPipeTypeNumVO> list = new ArrayList<>();
		String caliberType = dictConfig.getCaliberType();
		if (Objects.isNull(caliberType)) {
			throw new BizException("配置文件中未配置水管口径的类型值！");
		}
		String devStr = layerService.getDevIdsArray(rangeDTO.getRange(), rangeDTO.getInSR());
		List<DictDetailPO> dictDetailPOs = dictDetailService.findDetailsByTypeVal(caliberType);
		if (Objects.nonNull(dictDetailPOs)) {
			for (DictDetailPO dictDetailPO : dictDetailPOs) {
				WaterPipeTypeNumVO vo = new WaterPipeTypeNumVO();
				vo.setTypeName(dictDetailPO.getName());
				try {
					Object[] params = ComUtil.splitCaliberType(dictDetailPO.getVal());
					if (Objects.isNull(params)) {
						throw new BizException("水管口径类型参数值格式不正确");
					}
					String pre = String.valueOf(params[0]);
					int min = Integer.parseInt(String.valueOf(params[1]));
					int max = Integer.parseInt(String.valueOf(params[2]));
					String suf = String.valueOf(params[3]);

					long num = devQueryDAO.findWaterPipeCaliberSum(pre, min, max, suf, devStr);
					vo.setNum(num);
					list.add(vo);
				} catch (BizException e) {
					Logger.error("根据水管口径对应设备数量出错：{}", e.getMsg());
					e.printStackTrace();
				}
			}
		} else {
			throw new BizException("数据库dict_detail表中没有配置水管口径的范围值！");
		}
		return list;
	}

	/**
	 * 根据typeID 和 DevIds 统计子类的个数
	 * @param devIDsForTypeDTO
	 * @return
	 * @throws BizException
	 */
	public List<SonsNumVO> findSonsNumByPid(DevIDsForTypeDTO devIDsForTypeDTO) throws BizException {
		List<SonsNumVO> resultList = new ArrayList<>();
		// 根据设备类型ID查第二层的子类
		List<ShareDevTypePO> secondTypeList = shareDevTypeService.findDevTypeListByTypeId(devIDsForTypeDTO.getTypeId());

		String devStr = null;
		Long[] devIds = devIDsForTypeDTO.getDevIds();
		List<Long> ids = null;
		if (Objects.nonNull(devIds) && devIds.length > 0) {
			ids = Objects.nonNull(devIds) ? Arrays.asList(devIds) : Lists.newArrayList();
			devStr = Joiner.on(",").join(ids);
		}

		if (!Objects.isNull(secondTypeList)) {
			for (ShareDevTypePO po : secondTypeList) {
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
						num = devQueryDAO.getCountByTypeIds(typeIds, devStr);
					}
					vo.setNum(num);
					resultList.add(vo);
				} catch (BizException e) {
					Logger.error("空间查询，子类个数统计异常：{}", e.getMsg());
					e.printStackTrace();
				}
			}
		}
		// 数量倒序
		resultList.sort(Comparator.comparing(SonsNumVO :: getNum).reversed());

		Logger.debug("typeId = {}子类数据：" , devIDsForTypeDTO.getTypeId());
		resultList.stream().forEach(vo -> {
			Logger.debug(vo.toString());
		} );

		if (resultList.size() > GISConstants.PIE_SIZE) {
			SonsNumVO other = new SonsNumVO();
			long num = 0L;
			for (int i = GISConstants.PIE_SIZE - 1; i < resultList.size(); i ++){
				num += resultList.get(i).getNum();
				resultList.remove(i);
				i--;
			}
			other.setNum(num);
			other.setTypeName(GISConstants.OTHER_NAME);
			resultList.add(other);
		}
		return resultList;
	}

	/**
	 * 导出空间查询的设备列表数据
	 * @param dto
	 * @throws BizException
	 */
	public String exportDevInfoByPID(DevIDsForTypeDTO dto) throws BizException {
		OutputStream os = null;
		try {
			SXSSFWorkbook workbook;
			workbook = new SXSSFWorkbook(1000); // 超过1000写入硬盘
			ShareDevTypePO shareDevTypePO = shareDevTypeService.getByPrimaryKey(dto.getTypeId());
			String title = "Sheet1";
			if (Objects.nonNull(shareDevTypePO)) {
				title = shareDevTypePO.getName();
			}

			SXSSFSheet sheet = workbook.createSheet(title);
			sheet.setDefaultColumnWidth((short) 12); // 设置列宽
			CellStyle style = ExcelStyleUtil.createHeaderStyle(workbook);
			CellStyle style2 = ExcelStyleUtil.createBodyStyle(workbook);

			Row row = sheet.createRow(0);
			List<FieldNameVO> headerList = findFieldNamesByTypeID(dto.getTypeId());
			if (Objects.isNull(headerList)) {
				Logger.error("空间查询的表头信息为空");
				throw new BizException("设备列表的title为空");
			}
			for (int i = 0; i < headerList.size(); i++) {
				FieldNameVO fieldNameVO = headerList.get(i);
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
			Integer total = devQueryDAO.findDevListByTypeIDCount(dto, devStr); // 总条数

			/**
			 * 之所以分一下，是因为如果数据量过大，一次加载到内存有可能出现OOM，
			 * 针对Excel的大数据SXSSFWorkbook比XSSFWorkbook支持更好
 			 */
			int pageSize = GISConstants.EXPORT_PAGESIZE;
			int pageTotal;
			if (total <= pageSize) {
				pageTotal = 1;
			} else {
				pageTotal = total / pageSize == 0 ? total / pageSize : total / pageSize + 1;
			}
			Logger.debug("总条数：" + total + "\t每页条数：" + pageSize + "\t总页数：" + pageTotal);

			List<FieldNameVO> fieldsList = findFieldNamesByTypeID(dto.getTypeId());
			if (Objects.isNull(fieldsList)) {
				Logger.error("空间查询的表头信息为空");
				throw new BizException("设备列表的title为空");
			}
			String[] filedNames = fieldsList.stream().map(FieldNameVO::getFieldName).toArray(String[]::new);

			int body_i = 1; // body 行索引
			int pageNum = 1;
			while (pageTotal-- > 0) {
				DevIDsForTypeDTO devIDsForTypeDTO = new DevIDsForTypeDTO();
				BeanUtils.copyProperties(dto,devIDsForTypeDTO);
				devIDsForTypeDTO.setPageSize(pageSize);
				devIDsForTypeDTO.setPageNum(pageNum);
				PageVO<SpaceInfoVO> pageVO = findDevListPageByTypeID(devIDsForTypeDTO);
				List<SpaceInfoVO> subDevList = pageVO.getData();
				if (Objects.nonNull(subDevList)) {
					subDevList.stream().map(vo -> {
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


					for (SpaceInfoVO spaceInfoVO : subDevList) {
						Map<String, String> map = spaceInfoVO.getDataMap();
						if (Objects.isNull(map)) {
							continue;
						}
						Row xssfRow = sheet.createRow(body_i++);
						for (int i = 0; i < headerList.size(); i++) {
							Cell cell = xssfRow.createCell(i);
							XSSFRichTextString text;
							FieldNameVO fieldNameVO = headerList.get(i);
							String txt = null;
							if (Objects.nonNull(fieldNameVO)) {
								String fieldName = fieldNameVO.getFieldName();
								if (GISConstants.DEV_TYPE_NAME.equals(fieldName)) {
									txt = spaceInfoVO.getTypeName();
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
				subDevList.clear();
				pageNum ++;
			}
			String filePath = pathConfig.getDownloadPath() + "/" + title + ".xls";
			os = new FileOutputStream(new File(filePath));
			workbook.write(os);
			String result = JavaFileToFormUpload.send(pathConfig.getUploadFileUrl(), filePath);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw new BizException("导出空间数据信息失败！");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("导出空间数据信息失败！");
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取设备类型下面的设备列表信息，分页
	 * @param dto
	 * @return
	 * @throws BizException
	 */
//	public PageVO<SpaceInfoVO> findDevListPageByTypeID(RangeTypeDTO dto) throws BizException {
//		String devStr = null;
//		if (!StringUtils.isEmpty(dto.getRange())) {
//			devStr = layerService.getDevIdsArray(dto.getRange(), dto.getInSR());
//			if (Objects.nonNull(devStr) && StringUtils.trimWhitespace(devStr).length() == 0) {
//				devStr = "0";
//			}
//		}
//		PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy());
//		Page<SpaceInfoVO> list = (Page<SpaceInfoVO>) findDevListByTypeID(dto, devStr);
//		return new PageVO<>(list);
//	}

	/**
	 * 获取设备类型下面的设备列表信息，分页
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public PageVO<SpaceInfoVO> findDevListPageByTypeID(DevIDsForTypeDTO dto) throws BizException {
		String devStr = null;
		Long[] devIds = dto.getDevIds();
		List<Long> ids = Objects.nonNull(devIds) ? Arrays.asList(devIds) : Lists.newArrayList();
		if (Objects.nonNull(devIds) && devIds.length > 0) {
			devStr = Joiner.on(",").join(ids);
		}
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy());
		Page<SpaceInfoVO> list = (Page<SpaceInfoVO>) findDevListByTypeID(dto, devStr);
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
		try {
			List<GISDevExtVO> list  = gisDevExtPOMapper.findDevListByDevIds(ids);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("根据设备ID集合获取设备列表信息失败！");
			throw new BizException("根据设备ID集合获取设备列表信息失败！");
		}
		/** 可以不用转换，前端直接去datainfo字段的值
		try {
			List<GISDevExtVO> list = gisDevExtPOMapper.findDevListByDevIds(ids);
			if (Objects.nonNull(list)) {
				list.stream().map(vo -> {
					Object obj = vo.getDataInfo();
					if (Objects.isNull(obj)) {
						return vo;
					}
					Long devId = vo.getDevId();
					List<GisDevTplAttrPO> attrPOList = gisDevTplAttrPOMapper.findTplAttrsByDevId(devId);
					String[] filedNames = attrPOList.stream().map(GisDevTplAttrPO::getFieldName).toArray(String[]::new);
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
		} catch (Exception e) {
			Logger.error("根据设备ID集合获取设备列表信息失败！");
			throw new BizException("根据设备ID集合获取设备列表信息失败！");
		}**/
	}

	/**
	 * 根据设备ID集合获取设备列表信息，分页
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public PageVO<GISDevExtVO> findDevListPageByDevIDs(QueryDevDTO dto) throws BizException {
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy());
		Page<GISDevExtVO> list = (Page<GISDevExtVO>) findDevListByDevIDs(dto.getDevIds());
		return new PageVO<>(list);
	}

	/**
	 * 根据前端传来的范围+typeId获取路径
	 * @param parm 划定的范围 + typeId
	 * @return
	 * @throws BizException
	 */
	public String getDownLoadFile(String parm) throws BizException {
		try {
			Object obj = redisComponents.get(parm);
			if(obj == null) {
				return EApiStatus.ERR_VALIDATE.getStatus();
			} else if(obj != null  && EApiStatus.ERR_SYS.getStatus().equals(obj.toString())) {
				return EApiStatus.ERR_SYS.getStatus();
			} else{
				return obj.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("获取下载文件param = {} 失败", parm);
			throw new BizException("获取下载文件失败！");
		}
	}

	/**
	 * 获取每天的管段总长度
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public PipeLengthPO findPipeLength(GetPipeTotalLenthDTO dto) throws BizException {
		try{
			return gisDevExtPOMapper.findPipeLength(GISConstants.PIPE_LENGTH);
		} catch (Exception e){
			e.printStackTrace();
			Logger.debug("获取每天的管段总长度失败！");
			throw new BizException(e);
		}
	}

	/**
	 * 根据多个设备类型（limb_leaf=1，即枝干），查它们所属的设备信息
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public List<GISDevExtVO> findDevListByTypeIdsAndDevIds(DevIDsForTypesDTO dto) throws BizException {
		try{
			// 根据枝干获取叶子类型
			Long[] limbTypeIds = dto.getTypeIds();
			List<Long> typeIds = Arrays.asList(limbTypeIds);
			List<ShareDevTypePO> devTypePOS = shareDevTypeService.findLeafTypesByLimbTypeIds(typeIds);

			// 根据叶子类型获取设备信息
			List<Long> leafTypeIds = null;
			if (Objects.nonNull(devTypePOS)) {
				leafTypeIds = devTypePOS.stream().map(ShareDevTypePO :: getId).collect(Collectors.toList());
			}
			String leafTypeIdsStr = null;
			if (Objects.nonNull(leafTypeIds)) {
				leafTypeIdsStr = Joiner.on(",").join(leafTypeIds);
			}
			List<ShareDevPO> shareDevPOS = shareDevPOMapper.findDevListByTypeIds(leafTypeIdsStr);

			// 获取设备列表的属性信息
			List<Long> devIds;
			List<GISDevExtVO> gisDevExtVOS = null;
			List<Long> devIdsRange = null;
			if (Objects.nonNull(dto.getDevIds())) {
				devIdsRange = Arrays.asList(dto.getDevIds());
			}
			List<Long> copyDevIdsRange = Lists.newArrayList();
			copyDevIdsRange.addAll(devIdsRange);
			if (Objects.nonNull(shareDevPOS)) {
				devIds = shareDevPOS.stream().map(ShareDevPO :: getId).collect(Collectors.toList());
				// 求交集
				copyDevIdsRange.retainAll(devIds);
				gisDevExtVOS = gisDevExtPOMapper.findDevListByDevIds(copyDevIdsRange);
			}
			return gisDevExtVOS;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException(e);
		}
	}
}