package com.jdrx.gis.service.dataManage;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jdrx.gis.beans.dto.dataManage.MediaDTO;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.util.FileUtil;
import com.jdrx.gis.util.JavaFileToFormUpload;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * @Author: liaosijun
 * @Time: 2019/12/23 11:30
 */
@Service
public class MultiMediaService {

	@Autowired
	private PathConfig pathConfig;

	@Autowired
	private GISDevExtPOMapper gisDevExtPOMapper;


	/**
	 * 保存设备的图片和视频
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public int saveMultiMedia(MediaDTO dto) throws BizException{
		String devId = dto.getDevId();
		if (StringUtils.isEmpty(devId)) {
			throw new BizException("设备ID不能为空！");
		}
		int aff;
		try {
			String pics = Joiner.on(",").join(dto.getPicUrls());
			String videos = Joiner.on(",").join(dto.getVedioUrls());
			aff = gisDevExtPOMapper.updateMultiVideo(pics, videos, devId);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return aff;
	}

	/**
	 * 上传文件并获取url
	 * @param files
	 * @return
	 * @throws IOException
	 */
	public List<String> uploadFilesAndGetUrls(MultipartFile[] files) throws IOException {
		List<String> urls = Lists.newArrayList();
		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			if (Objects.isNull(fileName)) {
				return urls;
			}
			String outPath = pathConfig.getDownloadPath() + File.separator + fileName;
			InputStream inputStream = file.getInputStream();
			FileUtil.bufferedWrite(inputStream, outPath);
			String remoteUrl = JavaFileToFormUpload.send(pathConfig.getUploadFileUrl(), outPath);
			urls.add(remoteUrl);
		}
		return urls;
	}
}
