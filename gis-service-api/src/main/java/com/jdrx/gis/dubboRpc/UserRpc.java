package com.jdrx.gis.dubboRpc;

import com.google.common.collect.Maps;
import com.jdrx.gis.beans.entry.user.SysOcpUserPo;
import com.jdrx.platform.common.support.gateway.GwConstants;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.dubbo.common.beans.DubboRpcBean;
import com.jdrx.platform.dubbo.common.service.IDubboRpcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 通过ocp获取登录用户信息
 * @Author: liaosijun
 * @Time: 2019/7/29 15:20
 */
@Slf4j
@Component
public class UserRpc {

	@Reference(group = "${group.app.name.ocp}", check = false)
	private IDubboRpcService iDubboRpcService;

	/**
	 * 根据UserID查询用户信息
	 * @param userId
	 * @param token
	 * @return
	 */
	public SysOcpUserPo getUserById(Long userId, String token) {
		HashMap<String, Object> reqMap = Maps.newHashMap();
		reqMap.put("id", userId);
		DubboRpcBean rpcBean = DubboRpcBean.builder().setRpcPath("/oapi/0/queryUserById").setBody(reqMap).
				setHeader(GwConstants.REQ_AUTH_HEAD_FEILD, token).build();
		SysOcpUserPo sysOcpUserPo = null;
		try {
			sysOcpUserPo = SysOcpUserPo.class.newInstance();
			ResposeVO resposeVO = (ResposeVO) iDubboRpcService.callback(rpcBean);
			if (EApiStatus.SUCCESS.getStatus().equals(resposeVO.getStatus())) {
				Map<String, Object> data = (Map<String, Object>) resposeVO.getData();
				if (Objects.nonNull(data)) {
					BeanUtils.populate(sysOcpUserPo, data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("从OCP获取机构信息异常! userID= {}", userId);
		}
		return sysOcpUserPo;
	}
}