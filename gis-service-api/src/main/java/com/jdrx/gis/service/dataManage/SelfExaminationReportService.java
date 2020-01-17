package com.jdrx.gis.service.dataManage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jdrx.gis.beans.entity.basic.DictDetailPO;
import com.jdrx.gis.beans.entity.query.Pipe;
import com.jdrx.gis.beans.entity.query.TypeToDevNumsPO;
import com.jdrx.gis.beans.vo.basic.PipeLengthVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.query.SelfExamination;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.gis.util.ExcelStyleUtil;
import com.jdrx.gis.util.JavaFileToFormUpload;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author: liaosijun
 * @Time: 2020/1/14 10:13
 */
@Service
public class SelfExaminationReportService {

	@Autowired
	private GISDevExtPOMapper gisDevExtPOMapper;

	@Autowired
	private PathConfig pathConfig;

	@Autowired
	private DictConfig dictConfig;

	@Autowired
	private DictDetailService detailService;

	@Autowired
	private SelfExamination selfExamination;

	String[] headerLength = {"area", "pipeLength"};

	String[] headerDevNums = {"name", "num"};

	static Map<String, String> map1;

	static Map<String, String> map2;

	static {
		map1 = Maps.newHashMap();
		map1.put("name", "类型");
		map1.put("num", "设备个数");
	}

	static {
		map2 = Maps.newHashMap();
		map2.put("area", "地区");
		map2.put("pipeLength", "管网长度");
	}

	/**
	 * 获取每个区的管网长度
	 *
	 * @return
	 */
	public List<Pipe> getPipeLength() throws BizException {
		List<PipeLengthVO> pipeLengthByAuthId = gisDevExtPOMapper.getPipeLengthByAuthId();
		if (Objects.isNull(pipeLengthByAuthId)) {
			throw new BizException("未查到相关数据！");
		}
		List<DictDetailPO> orgList = detailService.findDetailsByTypeVal(dictConfig.getAuthId());
		if (Objects.isNull(orgList)) {
			throw new BizException("未配置权限机构！");
		}
		Map<Long, String> orgMap = Maps.newHashMap();
		orgList.forEach(dictDetailPO -> {
			orgMap.put(Long.valueOf(dictDetailPO.getVal()), dictDetailPO.getName());
		});
		List<Pipe> dataList = Lists.newArrayList();
		pipeLengthByAuthId.forEach(pipeLengthVO -> {
			Pipe pipe = new Pipe();
			pipe.setArea(orgMap.get(pipeLengthVO.getAuthId()));
			pipe.setPipeLength(pipeLengthVO.getLength());
			dataList.add(pipe);
		});
		return dataList;
	}

	private void createHeader(Param param) {
		SXSSFWorkbook workbook = param.getWorkbook();
		String[] headerNames = param.getHeaderNames();
		Map<String, String> map = param.getMap();
		SXSSFSheet sheet = workbook.createSheet(param.getSheetName());
		SXSSFRow headerRow = sheet.createRow(0);
		int fc = 0;
		CellStyle style = ExcelStyleUtil.createHeaderStyle(workbook);
		for (String headername : headerNames) {
			Cell cell = headerRow.createCell(fc++);
			cell.setCellStyle(style);
			cell.setCellValue(map.get(headername));
		}
	}

	private void createData(Param param) throws BizException {
		SXSSFWorkbook workbook = param.getWorkbook();
		String[] headerNames = param.getHeaderNames();
		List list = param.getList();
		Class clazz = param.getClazz();
		Map<String, String> map = param.getMap();
		Sheet sheet = workbook.getSheet(param.getSheetName());
		if (Objects.isNull(list) && list.size() == 0) {
			throw new BizException("数据为空！");
		} else {
			List<Map<String, Object>> rList = Lists.newArrayList();
			list.forEach(po -> {
				Field[] declaredFields = clazz.getDeclaredFields();
				Object poCopy;
				try {
					poCopy = clazz.newInstance();
					BeanUtils.copyProperties(po, poCopy);
					Map<String, Object> dataMap = Maps.newHashMap();
					for (Field field : declaredFields) {
						String fieldName = field.getName();
						for (Map.Entry<String, String> entry : map.entrySet()) {
							String key = entry.getKey();
							if (fieldName.equals(key)) {
								field.setAccessible(true);
								Object fieldVal = field.get(poCopy);
								dataMap.put(fieldName, fieldVal);
								break;
							}
						}
					}
					rList.add(dataMap);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			});
			CellStyle style2 = ExcelStyleUtil.createBodyStyle(workbook);
			for (int i = 0; i < rList.size(); i++) {
				Row row = sheet.createRow(i + 1);
				Map<String, Object> objectMap = rList.get(i);
				for (int j = 0; j < headerNames.length; j++) {
					Cell cell = row.createCell(j);
					cell.setCellStyle(style2);
					Object obj = objectMap.get(headerNames[j]);
					cell.setCellValue(Objects.isNull(obj) ? "" : String.valueOf(obj));
				}
			}
		}
	}

	public void getDevNums(SXSSFWorkbook workbook) throws BizException {
		Param param = new Param();
		param.setWorkbook(workbook);
		param.setSheetName("设备数量");
		param.setHeaderNames(headerDevNums);
		param.setMap(map1);
		param.setClazz(TypeToDevNumsPO.class);
		createHeader(param);
		List<TypeToDevNumsPO> devNums = selfExamination.findDevNums();
		param.setList(devNums);
		createData(param);
	}

	public void getPipeLengthForExcel(SXSSFWorkbook workbook) throws BizException {
		Param param = new Param();
		param.setWorkbook(workbook);
		param.setSheetName("管网长度");
		param.setHeaderNames(headerLength);
		param.setMap(map2);
		createHeader(param);
		List<Pipe> pipeLength = getPipeLength();
		param.setClazz(Pipe.class);
		param.setList(pipeLength);
		createData(param);
	}

	public String exportSelfExaminationReport() throws BizException {
		SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
		getPipeLengthForExcel(workbook);
		getDevNums(workbook);
		String filePath = pathConfig.getDownloadPath() + File.separator + "数据报告.xlsx";
		FileOutputStream bos;
		String result;
		try {
			bos = new FileOutputStream(new File(filePath));
			workbook.write(bos);
			result = JavaFileToFormUpload.send(pathConfig.getUploadFileUrl(), filePath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BizException("IO异常");
		}
		return result;
	}

	class Param{
		private SXSSFWorkbook workbook;
		private String[] headerNames;
		private List list;
		private Class clazz;
		private Map<String, String> map;
		private String sheetName;

		public SXSSFWorkbook getWorkbook() {
			return workbook;
		}

		public void setWorkbook(SXSSFWorkbook workbook) {
			this.workbook = workbook;
		}

		public String[] getHeaderNames() {
			return headerNames;
		}

		public void setHeaderNames(String[] headerNames) {
			this.headerNames = headerNames;
		}

		public List getList() {
			return list;
		}

		public void setList(List list) {
			this.list = list;
		}

		public Class getClazz() {
			return clazz;
		}

		public void setClazz(Class clazz) {
			this.clazz = clazz;
		}

		public Map<String, String> getMap() {
			return map;
		}

		public void setMap(Map<String, String> map) {
			this.map = map;
		}

		public String getSheetName() {
			return sheetName;
		}

		public void setSheetName(String sheetName) {
			this.sheetName = sheetName;
		}
	}
}