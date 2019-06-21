package com.jdrx.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jdrx.beans.constants.basic.GISConstants;
import org.postgresql.util.PGobject;

import java.util.Map;
import java.util.Objects;

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

}