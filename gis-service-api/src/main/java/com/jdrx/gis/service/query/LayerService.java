package com.jdrx.gis.service.query;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.gis.util.ComUtil;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: TODO
 * @Author: liaosijun
 * @Time: 2019/7/4 16:52
 */
@Service
public class LayerService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(LayerService.class);

	@Autowired
	private DictConfig dictConfig;

	@Autowired
	private DictDetailService dictDetailService;

	private static Map<String, String> layerParamMap = Maps.newConcurrentMap();
	static {
		layerParamMap.put(GISConstants.F, GISConstants.F_VALUE);
		layerParamMap.put(GISConstants.OUT_FIELDS, GISConstants.DEV_ID);
	}
	/**
	 * 获取arcgis发布的点或线的图层服务url
	 * @param pl 点或线的常量值
	 * @return
	 * @throws BizException
	 */
	private String getUrlByPL(String pl) throws BizException {
		String url = "";
		try {
			List<DictDetailPO> dictDetailPOs = dictDetailService.findDetailsByTypeVal(dictConfig.getPlLayerUrl());
			if (Objects.nonNull(dictDetailPOs)) {
				for (DictDetailPO po : dictDetailPOs) {
					if (pl.equals(po.getName())) {
						url = po.getVal();
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("获取图层url失败！");
		}
		return url;
	}

	/**
	 * 获取系统参数配置的点或线的图层url
	 * @return
	 * @throws BizException
	 */
	public List<String> getLayerUrls() throws BizException {
		try {
			List<String> list = Lists.newArrayList();
			List<DictDetailPO> dictDetailPOs = dictDetailService.findDetailsByTypeVal(dictConfig.getPlLayerUrl());
			if (Objects.nonNull(dictDetailPOs)) {
				for (DictDetailPO po : dictDetailPOs) {
						list.add(po.getVal());
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("获取图层url失败！");
		}
	}

	/**
	 * 根据经纬度范围，获取设备ID(dev_id)集合
	 * @param lngLat  经纬度范围
	 * @param inSR
	 * @return
	 * @throws BizException
	 */
	public List<Long> findDevIdsByAreaRange(String lngLat, String inSR) throws BizException {
		Long start = System.currentTimeMillis();
		List<Long> devIds = Lists.newArrayList();
		try {
			List<String> urlList = getLayerUrls();
			if (Objects.nonNull(urlList)) {
				for (String url : urlList) {
					layerParamMap.put(GISConstants.INSR, inSR);
					layerParamMap.put(GISConstants.GEOMETRY, lngLat);
					String result = ComUtil.httpPost(url, layerParamMap);
					List<Long> list = getDevIds(result);
					devIds.addAll(list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("获取dev_id列表失败！");
		}
		Long end = System.currentTimeMillis();
		Logger.debug("请求图层数据并转换耗时：" + (end - start) + " ms");
		return devIds;
	}

	/**
	 * 获取dev_id的集合
	 * @param src json格式的数据
	 * @return
	 */
	private List<Long> getDevIds(String src) {
		try {
			JSONObject jsonObject = JSONObject.parseObject(src);
			JSONArray jsonArray = jsonObject.getJSONArray(GISConstants.FEATURES);
			List<Long> list = Lists.newArrayList();
			jsonArray.forEach(feature -> {
				Object attributes = ((JSONObject) feature).get(GISConstants.ATTRIBUTES);
				Object dev_id = ((JSONObject)attributes).get(GISConstants.DEV_ID);
				list.add(Long.parseLong(String.valueOf(dev_id)));
			});
			Logger.debug("范围内设备个数为：{} ", list.size());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("从arcgis服务获取的JSON数据解析失败, {} ", e.getMessage());
		}
		return Lists.newArrayList();
	}

	/**
	 *  传经纬度范围和inSR获取设备列表
	 * @param range
	 * @param inSR
	 * @return
	 * @throws BizException
	 */
	public String getDevIdsArray(String range, String inSR) throws BizException{
		try {
			List<Long> devIds;
			String devStr = null;
			if (Objects.nonNull(range) && !StringUtils.isEmpty(range)) {
				devIds = findDevIdsByAreaRange(range, inSR);
				devStr = Joiner.on(",").join(devIds);
			}
			return devStr;
		} catch (Exception e){
			e.printStackTrace();
			Logger.error("从arcgis服务获取设备列表失败！");
			throw new BizException("从arcgis服务获取设备列表失败！");
		}
	}
}