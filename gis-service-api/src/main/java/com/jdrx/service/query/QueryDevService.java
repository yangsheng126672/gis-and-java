package com.jdrx.service.query;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jdrx.beans.constants.basic.ECaliber;
import com.jdrx.beans.constants.basic.ELimbLeaf;
import com.jdrx.beans.constants.basic.GISConstants;
import com.jdrx.beans.entry.basic.ShareDevTypePO;
import com.jdrx.beans.entry.query.SpaceInfTotalPO;
import com.jdrx.beans.vo.query.FieldNameVO;
import com.jdrx.beans.vo.query.SonsNumVO;
import com.jdrx.beans.vo.query.SpaceInfoVO;
import com.jdrx.beans.vo.query.WaterPipeTypeNumVO;
import com.jdrx.dao.query.DevQueryDAO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.service.basic.ShareDevTypeService;
import com.jdrx.util.ComUtil;
import com.jdrx.util.ExcelStyleUtil;
import org.apache.poi.xssf.usermodel.*;
import org.postgresql.util.PGobject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

	/**
	 * 获取第一级图层对应的设备个数
	 * @return
	 */
	public List<SpaceInfTotalPO> findFirstHierarchyDevTypeNum() throws BizException{
		List<SpaceInfTotalPO> list  = new ArrayList<>();
		List<ShareDevTypePO> devTypePOs = devQueryDAO.findFirstHierarchyDevTypeNum();
		devTypePOs.stream().forEach(devTypePO ->{
			SpaceInfTotalPO spaceInfTotalPO = new SpaceInfTotalPO();
			spaceInfTotalPO.setCoverageName(devTypePO.getName());
			spaceInfTotalPO.setNumber(0L);
			spaceInfTotalPO.setId(devTypePO.getId());
			list.add(spaceInfTotalPO);
		});
		list.stream().forEach(spaceInfTotalPO -> {
			List<ShareDevTypePO> shareDevTypePOs = devQueryDAO.findDevTypeByPID(spaceInfTotalPO.getId());
			List<Long> ids = new ArrayList<>();
			if (!ObjectUtils.isEmpty(shareDevTypePOs)){
				shareDevTypePOs.stream().forEach(shareDevTypePO ->{
					ids.add(shareDevTypePO.getId());
				});
			}
			Long cnt = devQueryDAO.getCountByTypeIds(ids);
			spaceInfTotalPO.setNumber(cnt);
		});
		return list;
	}

	/**
	 * 根据类型ID查询所属的设备信息
	 * @param pid
	 * @return
	 * @throws BizException
	 */
	public List<SpaceInfoVO> findDevListByTypeID(Long pid) throws BizException{
		List<SpaceInfoVO> list = devQueryDAO.findDevListByTypeID(pid);
		return list;
	}

	/**
	 * 根据类型ID查表头，递归该ID下面所有子类，让所有子类的模板配置的字段都展示出来，
	 * 若字段名称一样就合并成一个。
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public List<FieldNameVO> findFieldNamesByTypeID(Long id) throws BizException{
		List<FieldNameVO> list = devQueryDAO.findFieldNamesByTypeID(id);
		return list;
	}

	/**
	 * 水管口径数量统计，当前按照我们自己定义的大小分类
	 * @return
	 * @throws BizException
	 */
	public List<WaterPipeTypeNumVO> findWaterPipeCaliberSum() throws BizException{
		List<WaterPipeTypeNumVO> list = new ArrayList<>();
		for (ECaliber ec : ECaliber.values()){
			WaterPipeTypeNumVO vo = new WaterPipeTypeNumVO();
			vo.setTypeName(ec.getName());
			long num;
			switch (ec.getCode()){
				case "D1" :
					num = devQueryDAO.findWaterPipeCaliberSum(null,100);
					vo.setNum(num);
					list.add(vo);
					break;
				case "D2" :
					num = devQueryDAO.findWaterPipeCaliberSum(100,200);
					vo.setNum(num);
					list.add(vo);
					break;
				case "D3" :
					num = devQueryDAO.findWaterPipeCaliberSum(200,400);
					vo.setNum(num);
					list.add(vo);
					break;
				case "D4" :
					num = devQueryDAO.findWaterPipeCaliberSum(400,600);
					vo.setNum(num);
					list.add(vo);
					break;
				case "D5" :
					num = devQueryDAO.findWaterPipeCaliberSum(600,900);
					vo.setNum(num);
					list.add(vo);
					break;
				case "D6" :
					num = devQueryDAO.findWaterPipeCaliberSum(900,null);
					vo.setNum(num);
					list.add(vo);
					break;
				default : break;
			}
		}
		return list;
	}

	/**
	 * 根据设备类型的ID，查询子类的设备个数，子类为第二层的子类
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public List<SonsNumVO> findSonsNumByPid(Long id) throws BizException {
		List<SonsNumVO> resultList = new ArrayList<>();
		// 根据设备类型ID查第二层的子类
		List<ShareDevTypePO> secondTypeList = shareDevTypeService.findDevTypeListByTypeId(id);

		if (!Objects.isNull(secondTypeList)) {
			secondTypeList.stream().forEach(po -> {
				SonsNumVO vo = new SonsNumVO();
				// 根据ID查所有子类
				try {
					List<ShareDevTypePO> allTypeList = null;
					Integer ll = Integer.valueOf(po.getLimbLeaf());

					// 如果当前类型下有子类
					if (ELimbLeaf.LIMB.getVal().equals(ll)) {
						allTypeList = shareDevTypeService.selectAllDevByTypeId(po.getId());
					// 如果当前类型下没有子类
					} else if (ELimbLeaf.LEAF.getVal().equals(ll)){
						allTypeList = shareDevTypeService.selectAllDevByCurrTypeId(po.getId());
					}
					List<Long> typeIds = new ArrayList<>();
					if (!Objects.isNull(allTypeList)) {
						typeIds = allTypeList.stream().map(ShareDevTypePO::getId).collect(Collectors.toList());
					}
					vo.setTypeName(po.getName());
					long num = 0L;
					// 如果typeIds 为空就会统计称全表个数，故不为空才查询
					if (typeIds != null && typeIds.size() > 0) {
						num = devQueryDAO.getCountByTypeIds(typeIds);
					}
					vo.setNum(num);
					resultList.add(vo);
				} catch (BizException e) {
					Logger.error("空间查询，子类个数统计异常：{}", e.getMsg());
					e.printStackTrace();
				}
			});
		}
		return resultList;
	}

	public void exportDevInfoByPID(HttpServletResponse response,Long id) throws BizException {

		try {
			XSSFWorkbook workbook;
			workbook = new XSSFWorkbook(this.getClass().getResourceAsStream("/template/devinfo.xlsx"));
			ShareDevTypePO shareDevTypePO = shareDevTypeService.getByPrimaryKey(id);
			String title = "Sheet1";
			if (Objects.nonNull(shareDevTypePO)) {
				title = shareDevTypePO.getName();
			}
			XSSFSheet sheet = workbook.getSheetAt(0);
			workbook.setSheetName(0, title);
			sheet.setDefaultColumnWidth((short) 12); // 设置列宽
			XSSFCellStyle style = ExcelStyleUtil.createHeaderStyle(workbook);
			XSSFCellStyle style2 = ExcelStyleUtil.createBodyStyle(workbook);

			XSSFRow row = sheet.createRow(0);
			List<FieldNameVO> headerList = devQueryDAO.findFieldNamesByTypeID(id);
			String[] fieldNames = null;
			if (Objects.nonNull(headerList)) {
				fieldNames = new String[headerList.size() + 1];
				fieldNames[0] = GISConstants.DEV_TYPE_NAME;
				ComUtil.swapIdx(fieldNames,GISConstants.DEV_ID,0);

				for (int i = 0; i < headerList.size(); i++) {
					FieldNameVO fieldNameVO = headerList.get(i);
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(style);
					XSSFRichTextString text = new XSSFRichTextString(fieldNameVO.getFieldDesc());
					cell.setCellValue(text);
					fieldNames[i + 1]  = fieldNameVO.getFieldName();
				}
			}
			List<SpaceInfoVO> devList = devQueryDAO.findDevListByTypeID(id);
			if (Objects.nonNull(devList)) {
				int body_i = 1;
				for (SpaceInfoVO spaceInfoVO : devList) {
					XSSFRow xssfRow = sheet.createRow(body_i++);
					PGobject dataInfo = (PGobject) spaceInfoVO.getDataInfo();
					String dataStr = dataInfo.getValue();
					Gson g = new Gson();
					JsonObject jsonObject = g.fromJson(dataStr,JsonObject.class);
					String typeName = spaceInfoVO.getTypeName();
					for (int i = 0; i < fieldNames.length; i++) {
						String key = fieldNames[i];
						XSSFCell cell = xssfRow.createCell(i);
						/**
						 * 由于空间查询的表头是某个父类型下面所有配置的属性去重的集合，
						 * 故，某一个子类可能没有父类的某个属性，此处就跳过获取属性的值，
						 * geom也不需展示，过滤掉
						 */
						if (!jsonObject.has(key) || GISConstants.GEOM.equals(key)){
							continue;
						}
						JsonElement element = jsonObject.get(key);
						XSSFRichTextString text = new XSSFRichTextString(element.isJsonNull() ? "" : element.getAsString());
						cell.setCellValue(text);
						cell.setCellStyle(style2);
					}
				}
			}
			response.reset();
			response.setCharacterEncoding("utf-8");
			response.setHeader("content-disposition", "attachment;filename=" + title + ".xlsx");
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			workbook.write(response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}