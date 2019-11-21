package com.jdrx.gis.api.dataManage;

import com.google.common.collect.Maps;
import com.jdrx.gis.api.query.AttrQueryApi;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.datamanage.ImportDTO;
import com.jdrx.gis.beans.vo.datamanage.ImportVO;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.service.dataManage.ExcelProcessorService;
import com.jdrx.gis.util.JavaFileToFormUpload;
import com.jdrx.platform.common.support.gateway.GwConstants;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @Author: liaosijun
 * @Time: 2019/11/1 13:19
 */
@RestController
@Api("数据导入和模板下载")
@RequestMapping(value = "api/0/dataImport", method = RequestMethod.POST)
public class ImportApi {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(AttrQueryApi.class);

	@Autowired
	private PathConfig pathConfig;

	@Autowired
	private ExcelProcessorService excelProcessorService;

	@ApiOperation(value = "导出用于设备数据导入的Excel模板")
	@RequestMapping(value = "exportDataExcelTemplate")
	public ResposeVO exportDataExcelTemplate() throws BizException {
		Logger.debug("api/0/dataImport/exportDataExcelTemplate 导出设备数据的Excel模板");
		String url = pathConfig.getTemplatePath() + File.separator + GISConstants.TEMPLATE_EXCEL_NAME;
		String result;
		try {
			 result = JavaFileToFormUpload.send(pathConfig.getUploadFileUrl(), url);
		} catch (IOException e1) {
			e1.printStackTrace();
			Logger.debug("Excel模板不存在！");
			throw new BizException("Excel模板不存在！");
		}
		return ResponseFactory.ok(result);
	}



	@ApiOperation(value = "解析Excel数据")
	@RequestMapping(value = "analysisExcel")
	public ResposeVO analysisExcel(@PathVariable("file") MultipartFile file) throws BizException {
		Logger.debug("api/0/dataImport/analysisExcel 解析excel数据");
		excelProcessorService.validSuffix(file);
		InputStream inputStream;
		Workbook workbook;
		Map<String, List> map = Maps.newHashMap();
		try {
			inputStream = file.getInputStream();
			workbook = WorkbookFactory.create(inputStream);
			// 校验sheetName
			excelProcessorService.validSheetName(workbook);
			List<Map<String, Object>> pointList = excelProcessorService.getExcelDataList(workbook, GISConstants.IMPORT_SHEET0_NAME);
			List<Map<String, Object>> lineList = excelProcessorService.getExcelDataList(workbook, GISConstants.IMPORT_SHEET1_NAME);
			map.put(GISConstants.POINT_LIST_S, pointList);
			map.put(GISConstants.LINE_LIST_S, lineList);
		} catch (IOException | InvalidFormatException e) {
			e.printStackTrace();
		}
		return ResponseFactory.ok(map);
	}


	@ApiOperation(value = "导入设备数据")
	@RequestMapping(value = "importDeviceData")
	public ResposeVO importDeviceData(@RequestBody @Valid ImportDTO importDTO,
	                                  @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
	                                  @RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws BizException {
		Logger.debug("api/0/dataImport/importDeviceData 导入设备数据");
		Map<String, List> dataMap = importDTO.getDataMap();
		ImportVO importVO;
		try {
			importVO = excelProcessorService.saveExcelData(dataMap, userId, token, importDTO.getTs());
		} catch (BizException e) {
			e.printStackTrace();
			throw new BizException(e.getMessage());
		}
		return ResponseFactory.ok(importVO);
	}

}
