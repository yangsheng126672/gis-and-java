package com.jdrx.gis.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jdrx.gis.beans.constants.basic.EPGDataTypeCategory;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.entity.basic.ShareDevTypePO;
import com.jdrx.platform.commons.rest.exception.BizException;
import okhttp3.*;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 工具类
 * @Author: liaosijun
 * @Time: 2019/6/19 16:04
 */
public class ComUtil {


	/**
	 * 交换ss数组中target字符到idx位置,适用于ss数组元素不重复
	 * @param ss
	 * @param target
	 * @param idx
	 * @throws Exception
	 */
	public static void swapIdx(String[] ss, String target, int idx) throws Exception {
		if (Objects.isNull(ss)) {
			throw new Exception("数组为空");
		}
		if (idx >= ss.length || idx < 0) {
			throw new IndexOutOfBoundsException("数组下标越界");
		}
		if (Objects.isNull(target)) {
			throw new Exception("需要交换的元素为空");
		}
		String el = ss[idx];
		int cnt = 0;
		for (int i = 0; i < ss.length; i++) {
			String s = ss[i];
			if (s.equals(target)) {
				ss[i] = el;
				ss[idx] = target;
				break;
			}else {
				cnt ++;
			}
		}
		if (cnt == ss.length){
			throw new Exception("需要交换的元素不存在");
		}
	}

	/**
	 * 解析jsonObject为map并且过滤geom字段
	 * @param obj 待解析的jsonObject
	 * @param titles 表头数组
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> parseDataInfo(Object obj, String[] titles) throws Exception{
		Map<String, String> map = Maps.newHashMap();
		try {
			if (Objects.isNull(obj)) {
				throw new Exception("传入待解析的JsonObject为空");
			}
			PGobject dataInfo = (PGobject) obj;
			String dataStr = dataInfo.getValue();
			Gson g = new Gson();
			JsonObject jsonObject = g.fromJson(dataStr, JsonObject.class);
			if (Objects.isNull(titles)) {
				throw new Exception("Title数组为空");
			}
			for (int i = 0; i < titles.length; i++){
				String key = titles[i];
				/**
				 * 由于空间查询的表头是某个父类型下面所有配置的属性去重的集合，
				 * 故，某一个子类可能没有父类的某个属性，此处就跳过获取属性的值，
				 * geom也不需展示，过滤掉
				 */
				if (!jsonObject.has(key) || GISConstants.GEOM.equals(key)){
					continue;
				}
				JsonElement element = jsonObject.get(key);
				String val = element.isJsonNull() ? "" : element.getAsString();
				map.put(key, val);
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new Exception("解析设备的属性JSON数据出错");
		}
		return map;
	}

	/**
	 * 递归处理   数据库树结构数据->树形json
	 * @param id
	 * @param list
	 * @return
	 */
	public static JSONArray getNodeJson(Long id,List<ShareDevTypePO> list){
		if (list.size() ==0){
			return null;
		}
		//当前层级当前点下的所有子节点
		List<ShareDevTypePO> childList = getChildNodes(id,list);
		JSONArray childTree = new JSONArray();
		for (ShareDevTypePO node : childList) {
			JSONObject o = new JSONObject();
			o.put("id",node.getId());
			o.put("name", node.getName());
			o.put("type", node.getLimbLeaf());
			JSONArray childs = getNodeJson(node.getId(),list);  //递归调用该方法
			if(!childs.isEmpty()) {
				o.put("children",childs);
			}
			childTree.fluentAdd(o);
		}
		return childTree;
	}

	/**
	 * 获取当前节点的所有子节点
	 * @param nodeId
	 * @param nodes
	 * @return
	 */
	public static List<ShareDevTypePO> getChildNodes(Long nodeId, List<ShareDevTypePO> nodes){
		List<ShareDevTypePO> list = new ArrayList();
		for (ShareDevTypePO shareDevTypePO : nodes ) {
			if(shareDevTypePO.getPId().equals(nodeId)){
				list.add(shareDevTypePO);
			}
		}
		return list;
	}

	/**
	 * 管径范围解析
	 * @param val dict_detail里面配置的管径范围
	 * @return
	 * @throws Exception
	 */
	public static Object[] splitCaliberType(String val) throws BizException {
		if (Objects.isNull(val)) {
			throw new BizException("水管口径类型参数值为空！");
		}
		String pattern = "([\\[|\\(])(\\d+)(.)(\\d+)([\\)|\\]])";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(val);
		Object[] result = null;
		if(Objects.nonNull(m)) {
			if (m.groupCount() == 5){
				if (m.find()) {
					result = new Object[4];
					result[0] = m.group(1);
					result[1] = m.group(2);
					result[2] = m.group(4);
					result[3] = m.group(5);
				}
			} else {
				throw new BizException("水管口径类型参数值格式不正确");
			}
		} else {
			throw new BizException("水管口径类型参数值格式不正确");
		}
		return result;
	}

	/**
	 * arcgis发布图层服务httppost 请求
	 * @param url
	 * @param map 参数
	 * @return
	 * @throws IOException
	 */
	public static String httpPost(String url, Map<String, String> map) throws IOException {
		OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build();
		FormBody.Builder builder = new FormBody.Builder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			builder.add(entry.getKey(), entry.getValue());
		}
		RequestBody requestBody = builder.build();
		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();
		Response response = httpClient.newCall(request).execute();
		return response.body().string();
	}

	/**
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String httpGet(String url) throws IOException {
		OkHttpClient httpClient = new OkHttpClient();
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		Response response = httpClient.newCall(request).execute();
		return response.body().string();
	}

	/**
	 * 把条件值转换成jsonb，依赖于gis_dev_ext表的data_info字段固定
	 * @param fieldName 属性查询的英文字段名称
	 * @param criteria 条件值
	 * @param categoryCode 数据类型的分类
	 * @return
	 * @throws BizException
	 */
	public static String processAttrField(String fieldName, String criteria, String categoryCode) throws BizException{
		try {
			if (Objects.nonNull(criteria)) {
				criteria = criteria.trim();
			}
			StringBuffer pz = new StringBuffer().append(" (data_info :: jsonb ->> '" + fieldName + "') :: ");
			String temp;
			if (EPGDataTypeCategory.N.getCode().equals(categoryCode)) {
				temp = String.valueOf(pz.append(" numeric "));
			} else if (EPGDataTypeCategory.S.getCode().equals(categoryCode)) {
				temp = String.valueOf(pz.append(" varchar "));
			} else if (EPGDataTypeCategory.D.getCode().equals(categoryCode)) {
				temp = String.valueOf(pz.append(" timestamp "));
			} else {
				throw new BizException("属性的筛选条件格式不支持！");
			}

			criteria = criteria.replaceAll(fieldName, temp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("属性的筛选条件格式不正确！");
		}
		return criteria;
	}

	/**
	 * 验证是否为数字
	 * @param target
	 * @return
	 */
	public static boolean verifyNumber(String target) {
		String regex = "\\-?\\d*\\.?\\d*";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);
		return matcher.matches();
	}

	/**
	 * 验证数据导入时，Excel里面的日期或时间格式是否正确，只做格式验证，不做数据准确性验证
	 * 格式为规定为 yyyy-mm-dd hh:mm:ss或yyyy/mm/dd hh:mm:ss，其它格式一概让他们更改后再传
	 * @param target
	 * @return
	 */
	public static boolean verifyExcelInputDateTime(String target) {
		String regex = "\\d{4}[/-]\\d{1,2}[/-]\\d{1,2}( \\d{1,2}:\\d{1,2}:\\d{1,2})?";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);
		return matcher.matches();
	}

	/**
	 * map转换成PGObject
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static PGobject convertMaptoPGObject(Map<String, String> map) throws Exception {
		String jsonStr = JSONObject.toJSONString(map,SerializerFeature.WriteMapNullValue);
		PGobject jsonObject = new PGobject();
		jsonObject.setValue(jsonStr);
		jsonObject.setType("json");
		return jsonObject;
	}

	/**
	 * 替换字符串中某个子串，该子串以忽略大小写的方式替换
	 * @param target        需要替换的目标字符串
	 * @param orginStr      被替换的子串，忽略大小写，并且是边界，如share_dev_type 中替换share_dev不成功
	 * @param replaceStr    替换旧子串的新子串
	 * @return
	 */
	public static String replaceTargetIngorCase(String target, String orginStr,String replaceStr) {
		try {
			Pattern pattern = Pattern.compile(orginStr + "\\b", Pattern.CASE_INSENSITIVE);
			Pattern pattern1 = Pattern.compile(orginStr);
			Matcher matcher = pattern.matcher(target);
			target = matcher.replaceAll(replaceStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return target;
	}

}