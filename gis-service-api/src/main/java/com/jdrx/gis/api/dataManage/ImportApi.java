package com.jdrx.gis.api.dataManage;

import com.jdrx.gis.api.query.AttrQueryApi;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.service.dataManage.ExcelProcessorService;
import com.jdrx.platform.common.support.gateway.GwConstants;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.Objects;

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
	public void exportDataExcelTemplate(HttpServletRequest request, HttpServletResponse response) throws BizException {
		Logger.debug("api/0/dataImport/exportDataExcelTemplate 导出设备数据的Excel模板");
		String url = pathConfig.getTemplatePath() + File.separator + GISConstants.TEMPLATE_EXCEL_NAME;
		File file = new File(url);
		InputStream in;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			Logger.debug("Excel模板不存在！");
			throw new BizException("Excel模板不存在！");
		}
		response.reset();
		response.setHeader("Content-disposition", "attachment; filename=" + GISConstants.TEMPLATE_EXCEL_NAME);
		response.setContentType("application/msexcel");
		OutputStream out;
		try {
			out = response.getOutputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new BizException(e1);
		}
		try {
			byte[] buff = new byte[1024];
			int bytesRead;
			while (-1 != (bytesRead = in.read(buff, 0, buff.length))) {
				out.write(buff, 0, bytesRead);
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new BizException(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@ApiOperation(value = "导入设备数据")
	@RequestMapping(value = "importDeviceData")
	public ResposeVO importDeviceData(@PathVariable("file") MultipartFile file,
	                                  @RequestHeader(value = GwConstants.TRANSPARENT_USERID_FEILD) Long userId,
										@RequestHeader(value = GwConstants.TRANSPARENT_TOKEN_FEILD) String token) throws BizException {
		Logger.debug("api/0/dataImport/importDeviceData 导入设备数据");
		String filaName = file.getOriginalFilename();
		if (Objects.nonNull(filaName)) {
			String suffix = filaName.substring(filaName.lastIndexOf(".") + 1);
			if (!"xls".equals(suffix) && !"xlsx".equals(suffix)) {
				throw new BizException("只能上传Excel文件");
			}
		}
		InputStream inputStream;
		Workbook workbook;
		boolean res = false;
		try {
			inputStream = file.getInputStream();
			workbook = WorkbookFactory.create(inputStream);
			res = excelProcessorService.saveExcelData(workbook, userId, token, 1);
		} catch (IOException | InvalidFormatException e) {
			e.printStackTrace();
		} catch (BizException e) {
			e.printStackTrace();
			throw new BizException(e.getMessage());
		}
		return ResponseFactory.ok(res);
	}

}
