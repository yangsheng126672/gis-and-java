package com.jdrx.gis.filter.assist;

import com.jdrx.platform.commons.rest.exception.BizException;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: Huangxg
 * @Time: 2019/11/28 10:38
 */
public class OcpService {
	/**
	 * 用户所属机构父级路径，约定规则如下：第一级为集团机构信息、集团之下也就是第二级为水厂机构信息，
	 * 最后一级为当前用户直属机构信息
	 */
	private String[] deptIds;

	public OcpService setDeptPath(String deptPath) throws BizException {
		if (StringUtils.isEmpty(deptPath)) {
			throw new BizException("用户所属机构异常！");
		}
		this.deptIds = deptPath.split("/");
		return this;
	}

	/**
	 * 获取用户直属机构ID
	 *
	 * @return
	 */
	public Long getUserDeptId() {
		return Long.valueOf(this.deptIds[this.deptIds.length - 1]);
	}

	/**
	 * 获取用户集团机构ID
	 *
	 * @return
	 */
	public Long getUserTopDeptId() {
		return Long.valueOf(this.deptIds[0]);
	}

	/**
	 * 判断用户是否是集团机构(true-是，false-不是)
	 *
	 * @return
	 */
	public boolean isTopDept() {
		return getUserTopDeptId().equals(getUserDeptId());
	}

	/**
	 * 获取用户水厂机构ID , 当无水厂机构ID时返回null
	 *
	 * @return
	 */
	public Long getUserWaterworksDeptId() {
		if (this.deptIds.length >= 2) {
			return Long.valueOf(this.deptIds[1]);
		}
		return null;
	}
}
