package com.jdrx.gis.service.basic;

import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.share.beans.dto.ShareDeviceCreateDTO;
import com.jdrx.share.beans.dto.ShareDeviceUpdateDTO;
import com.jdrx.share.service.DeviceService;
import org.springframework.stereotype.Service;

/**
 * @Author: liaosijun
 * @Time: 2019/11/13 20:54
 */
@Service
public class GISDeviceService implements DeviceService {
	@Override
	public boolean create(String uid, ShareDeviceCreateDTO dto) throws BizException {
		return false;
	}

	@Override
	public boolean update(String uid, ShareDeviceUpdateDTO dto) throws BizException {
		return false;
	}

	@Override
	public boolean delete(String id, String uid) throws BizException {
		return false;
	}
}
