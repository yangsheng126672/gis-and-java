package com.jdrx.gis.beans.constants.basic;

/**
 * @Description: 常量
 * @Author: liaosijun
 * @Time: 2019/6/19 16:28
 */
public class GISConstants {

	/**
	 * 验证串
	 */
	public static final String TRANSPARENT_TOKEN_FEILD = "X-TOKE";

	/**
	 * 用作空间查询展示设备编号，要想把设备编号放在第一列的话，
	 * 就需要配置类型模板时，把gis_dev_tpl_attr的field_name字段统一成dev_id,
	 * 否则动态展示设备属性数据时就不能固定在第一列
	 */
	public final static String DEV_ID = "dev_id";

	/**
	 * 用作空间查询展示类名称，因为这个字段不是配置在类型模板里面的
	 */
	public final static String DEV_TYPE_NAME = "devTypeName";

	public final static String DEV_TYPE_NAME_DESC = "类型";

	/**
	 * 类型模板中配置json数据的那个空间信息字段，统一取名geom，方便空间查询展示时，过滤掉
	 */
	public final static String GEOM = "geom";


	/********************************图层请求参数常量字段start**********************/
	public final static String F = "f";

	public final static String F_VALUE = "pjson";

	public final static String OUT_FIELDS = "outFields";

	public final static String GEOMETRY = "geometry";

	public final static String INSR = "inSR";

	public final static String FEATURES = "features";

	public final static String ATTRIBUTES = "attributes";
	/********************************图层请求参数常量字段end**********************/

	/** 编码 */
	public final static String UTF8 = "UTF-8";

	/** 饼图分块大小 */
	public final static int PIE_SIZE = 10;

	/** 饼图里面超过PIE_SIZE （含） 都放在其它里面 */
	public final static String OTHER_NAME = "其它";

	/** 导出Excel拆解 */
	public final static int EXPORT_PAGESIZE = 20000;

	/** 下载文件redis失效时间 */
	public final static int DOWNLOAD_EXPIRE = 3600;

	/** 管段长度配置模板的字段*/
	public final static String PIPE_LENGTH = "pipe_length";

	/** redis 导出文件的key加下划线 */
	public final static String UNDER_LINE = "_";

	/** 数据归属 */
	public final static String BELONG_TO_IDS = "belongToIds";

	/** 导入设备数据模板的Excel名称*/
	public final static String TEMPLATE_EXCEL_NAME = "templateForImport.xls";

	/** 导入数据的Excle 第一个sheet名称*/
	public final static String IMPORT_SHEET0_NAME = "管点表";

	/** 导入数据的Excel 第二个sheet名称*/
	public final static String IMPORT_SHEET1_NAME = "管段表";

	/** 一次最多导入条数*/
	public final static int IMPORT_MAX_ROWS = 30000;

	/** 设备数据导入中表头需要验证之一： 类别名称 */
	public final static String DEV_TYPE_NAME_CHN = "类别名称";

	/** 设备数据导入中表头需要验证之二： 材质 */
	public final static String MATERIAL_CHN = "材质";

	/** 设备数据导入中表头需要验证之三：管点编码 */
	public final static String POINT_CODE_CHN = "管点编码";

	/** 设备数据导入中表头需要验证之四：起点编码 */
	public final static String LINE_START_CODE_CHN = "起点编码";

	/** 设备数据导入中表头需要验证之五：终点编码 */
	public final static String LINE_END_CODE_CHN = "终点编码";

	/** 设备数据导入中表头需要验证之六：管径 */
	public final static String CALIBER_CHN = "管径";

	/** 设备数据导入中表头需要验证之七：权属单位 */
	public final static String DATA_AUTH_CHN = "权属单位";
}