package com.jdrx.gis.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * @Description: Excel 样式设置
 * @Author: liaosijun
 * @Time: 2019/6/19 13:19
 */
public class ExcelStyleUtil {

	public static CellStyle createHeaderStyle(SXSSFWorkbook workbook){
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderBottom(BorderStyle.THIN); //下边框
		style.setBorderLeft(BorderStyle.THIN);  //左边框
		style.setBorderTop(BorderStyle.THIN);   //上边框
		style.setBorderRight(BorderStyle.THIN); //右边框
		style.setAlignment(HorizontalAlignment.CENTER); //居中
		// 生成一个字体
		Font font = workbook.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		style.setFont(font);
		return style;
	}

	public static CellStyle createBodyStyle(SXSSFWorkbook workbook){
		CellStyle style = workbook.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN); //下边框
		style.setBorderLeft(BorderStyle.THIN);  //左边框
		style.setBorderTop(BorderStyle.THIN);   //上边框
		style.setBorderRight(BorderStyle.THIN); //右边框
		style.setAlignment(HorizontalAlignment.LEFT); //居左
		// 生成一个字体
		Font font = workbook.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 10);
		font.setBold(false);
		style.setFont(font);
		return style;
	}
}