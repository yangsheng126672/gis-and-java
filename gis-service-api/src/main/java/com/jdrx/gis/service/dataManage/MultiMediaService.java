package com.jdrx.gis.service.dataManage;

import com.google.common.base.Joiner;
import com.jdrx.gis.beans.constants.basic.EDBCommand;
import com.jdrx.gis.beans.dto.dataManage.MediaDTO;
import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.beans.entity.basic.ShareDevPO;
import com.jdrx.gis.beans.entity.dataManage.MultiMediaPO;
import com.jdrx.gis.beans.entity.log.GisDevVer;
import com.jdrx.gis.beans.entity.user.SysOcpUserPo;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.ShareDevPOMapper;
import com.jdrx.gis.dubboRpc.UserRpc;
import com.jdrx.gis.service.log.GisDevVerService;
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
import java.util.Date;
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

	@Autowired
	private UserRpc userRpc;

	@Autowired
	private GisDevVerService gisDevVerService;

	@Autowired
	private ShareDevPOMapper shareDevPOMapper;
	/**
	 * 保存设备的图片和视频
	 * @param dto
	 * @return
	 * @throws BizException
	 */
	public int saveMultiMedia(MediaDTO dto, Long userId, String token) throws BizException{
		String devId = dto.getDevId();
		if (StringUtils.isEmpty(devId)) {
			throw new BizException("设备ID不能为空！");
		}
		SysOcpUserPo sysOcpUserPo = userRpc.getUserById(userId, token);
		String loginUserName = sysOcpUserPo.getName();
		MultiMediaPO po = new MultiMediaPO();
		po.setUpdateAt(new Date());
		po.setUpdateBy(loginUserName);
		po.setDevId(devId);
		int aff;
		try {
			List<String> picUrls = dto.getPicUrls();
			List<String> vedioUrls = dto.getVideoUrls();
			String pics, videos;
			if (Objects.nonNull(picUrls) && picUrls.size() > 0) {
				pics = Joiner.on(",").join(picUrls);
				po.setPicUrls(pics);
			}
			if (Objects.nonNull(vedioUrls) && vedioUrls.size() > 0) {
				videos = Joiner.on(",").join(vedioUrls);
				po.setVideoUrls(videos);
			}
			ShareDevPO shareDevLog = shareDevPOMapper.selectByPrimaryKey(devId);
			GisDevVer gisDevVer = new GisDevVer();
			gisDevVer.setCreateAt(new Date());
			gisDevVer.setCreateBy(loginUserName);
			gisDevVer.setCommand(EDBCommand.UPDATE.getVal().shortValue());
			GISDevExtPO ext = gisDevExtPOMapper.getDevExtByDevId(devId);
			gisDevVerService.saveDevEditLog(gisDevVer, shareDevLog, ext);
			aff = gisDevExtPOMapper.updateMultiVideo(po);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return aff;
	}

	/**
	 * 上传文件并获取url
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public String uploadFilesAndGetUrls(MultipartFile file) throws IOException {
		String url = "";
		String fileName = file.getOriginalFilename();
		if (Objects.isNull(fileName)) {
			return url;
		}
		String outPath = pathConfig.getDownloadPath() + File.separator + fileName;
		InputStream inputStream = file.getInputStream();
		FileUtil.bufferedWrite(inputStream, outPath);
		String remoteUrl = JavaFileToFormUpload.send(pathConfig.getUploadFileUrl(), outPath);
		return remoteUrl;
	}
}
