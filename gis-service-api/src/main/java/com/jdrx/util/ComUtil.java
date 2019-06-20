package com.jdrx.util;

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


}