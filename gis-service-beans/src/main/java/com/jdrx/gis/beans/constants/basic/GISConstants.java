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
	public static final String TRANSPARENT_TOKEN_FEILD = "authorization";

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
	public final static String TEMPLATE_EXCEL_NAME = "templateForImport.xlsx";

	/** 导入数据的Excle 第一个sheet名称*/
	public final static String IMPORT_SHEET0_NAME = "管点表";

	/** 导入数据的Excel 第二个sheet名称*/
	public final static String IMPORT_SHEET1_NAME = "管段表";

	/** 一次最多导入条数*/
	public final static int IMPORT_MAX_ROWS = 20000;

	/** 设备数据导入中表头需要验证之一： 名称 */
	public final static String DEV_TYPE_NAME_CHN = "名称";

	/** 设备数据导入中表头需要验证之二： 材质 */
	public final static String MATERIAL_CHN = "材质";

	/** 设备数据导入中表头需要验证之三：管点编码 */
	public final static String POINT_CODE_CHN = "管点编码";

	/** 设备数据导入中表头需要验证之四：起点编码 */
	public final static String LINE_START_CODE_CHN = "起点编码";

	/** 设备数据导入中表头需要验证之五：终点编码 */
	public final static String LINE_END_CODE_CHN = "终点编码";

	/** 设备数据导入中表头需要验证之六：管径 */
	public final static String CALIBER_CHN = "管径(mm)";

	/** 设备数据导入中表头需要验证之七：权属单位 */
	public final static String DATA_AUTH_CHN = "权属单位";

	/** 设备数据导入中表头需要验证之八：横坐标 */
	public final static String X_CHN = "X坐标";

	/** 设备数据导入中表头需要验证之九：纵坐标 */
	public final static String Y_CHN = "Y坐标";

	/** 平台编码 */
	public final static String PLATFORM_CODE = "02";

	/** 道路名称 */
	public final static String DEV_ADDR_CHN = "道路名称";

	/** 管件，导入的Excel的管点的模板根据管件去查模板 ID */
	public final static String TOP_TPL_1_CHN = "管点模板";

	/** 水管，导入的Excel的管段的模板根据水管去查模板ID */
	public final static String TOP_TPL_2_CHN = "管段模板";

	/** data_info */
	public final static String DATA_INFO = "data_info";

	/********************************Neo4j图数据库常量字段**********************/

	/** 管点标签 一个标签其实就是代表一张表*/
	public final static String NEO_POINT = "gd";

	/** 逻辑管点标签 */
	public final static String NEO_POINT_LJ = "ljgd";

	/** 管线标签 */
	public final static String NEO_LINE = "gdline";

	/** 逻辑管线标签 */
	public final static String NEO_LINE_LJ = "ljgdline";

	/** 普通节点 */
	public final  static String NEO_NODE_NORMAL = "0";

	/** 阀门节点 */
	public final  static String NEO_NODE_VALVE = "1";

	/** 水源节点 */
	public final static String NEO_NODE_WATER = "2";
	/** typeName */
	public final static String DEV_TYPE_NAME_EN = "typeName";

	/** 点List */
	public final static String POINT_LIST_S = "pointList";

	/** 线List */
	public final static String LINE_LIST_S = "lineList";

	public final static String SHARE_DEV_S = "shareDev";

	public final static String GIS_DEV_EXT_S = "gisDevExt";

	public final static String AUTH_ID_S = "authId";

	public final static String CALIBER_0 = "DN100（不含）以下管段";

	public final static String CALIBER_100 = "DN100-DN200管段";

	public final static String CALIBER_200 = "DN200-DN400管段";

	public final static String CALIBER_400 = "DN400-DN600管段";

	public final static String CALIBER_600 = "DN600-DN900管段";

	public final static String CALIBER_900 = "DN900（含）以上管段";
}