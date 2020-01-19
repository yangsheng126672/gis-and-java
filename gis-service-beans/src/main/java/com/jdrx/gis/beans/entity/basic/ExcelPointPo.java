package com.jdrx.gis.beans.entity.basic;

import lombok.Data;

@Data
public class ExcelPointPo {
    /** 点编码 */
    private String pointCode;

    /** 点X坐标 */
    private String pointX;

    /** 点Y坐标 */
    private String pointY;

    /** 地面高程(m) */
    private String groundHeight;

    /** 埋深(m)  */
    private String depth;

    /** 材质
     */
    private String material;

    /** 名称 */
    private String name;

    /** 道路名称 */
    private String address;

    /** 规格
     */
    private String spec;

    /** 勘测单位
     */
    private String surveyCompany;

    /** 勘测日期
     */
    private String surveyDate;
    /** 权属单位
     */
    private String belongTo;
    /**
     * 备注
     */
    private String remark;
}
