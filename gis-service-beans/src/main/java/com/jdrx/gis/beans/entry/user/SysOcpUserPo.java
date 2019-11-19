package com.jdrx.gis.beans.entry.user;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @Description: ocp用户信息
 * @Author: liaosijun
 * @Time: 2019/7/29 15:21
 */
@Data
@ToString
public class SysOcpUserPo {

	/** id */
	private Long id;

	/** 部门ID */
	private Long deptId;

	/** 部门名称 */
	private String deptName;

	/** 用户名称 */
	private String name;

	/** 创建时间 */
	private Date createAt;

	/** 备注 */
	private String remark;

	/** 状态 */
	private Integer status;

	/** 角色名称 */
	private String roleName;

	/** 联系电话 */
	private String phone;

	/** 邮箱 */
	private String email;

	/** 真实名称 */
	private String realName;
}