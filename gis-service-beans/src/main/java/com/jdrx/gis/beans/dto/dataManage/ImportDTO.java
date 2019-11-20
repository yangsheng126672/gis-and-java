package com.jdrx.gis.beans.dto.datamanage;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: liaosijun
 * @Time: 2019/11/19 19:44
 */
@Data
public class ImportDTO {
	private Map<String, List> dataMap;
	private String batchNum;
}
