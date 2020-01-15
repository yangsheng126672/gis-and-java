package com.jdrx.gis.service.dataManage;

import com.jdrx.gis.beans.vo.basic.PipeLengthVO;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.util.ExcelStyleUtil;
import com.jdrx.gis.util.JavaFileToFormUpload;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

/**
 *
 * @Author: liaosijun
 * @Time: 2020/1/14 10:13
 */
@Service
public class SelfExaminationReportService {

	@Autowired
	private GISDevExtPOMapper gisDevExtPOMapper;

	@Autowired
	private PathConfig pathConfig;

	public List<PipeLengthVO> getPipeLength() {
		List<PipeLengthVO> pipeLengthByAuthId = gisDevExtPOMapper.getPipeLengthByAuthId();
		return pipeLengthByAuthId;
	}

	public String exportSelfExaminationReport() throws BizException {
		SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
		SXSSFSheet sheet = workbook.createSheet("管网长度");
		SXSSFRow headerRow = sheet.createRow(0);
		String[] headerNames = {"地区", "管网长度"};
		int firstRowI = 0;
		CellStyle style = ExcelStyleUtil.createHeaderStyle(workbook);
		CellStyle style2 = ExcelStyleUtil.createBodyStyle(workbook);
		for (String headername : headerNames) {
			Cell cell = headerRow.createCell(firstRowI++);
			cell.setCellStyle(style);
			cell.setCellValue(headername);
		}
		List<PipeLengthVO> pipeLength = getPipeLength();
		for (int i = 0; i < pipeLength.size(); i ++) {
			PipeLengthVO vo = pipeLength.get(i);
			Row row = sheet.createRow(i);
			Cell cell = row.createCell(i);
			cell.setCellStyle(style2);
			cell.setCellValue(vo.getLength());
		}
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

}
