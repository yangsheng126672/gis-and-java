package com.jdrx.gis.beans.constants.basic;

/**
 * @Description: 常量
 * @Author: liaosijun
 * @Time: 2019/6/19 16:28
 */
public class GISConstants {

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

	public final static String DEV_TYPE_NAME_DESC = "类名称";

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
}