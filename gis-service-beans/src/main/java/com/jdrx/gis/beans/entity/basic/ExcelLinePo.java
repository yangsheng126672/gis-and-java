package com.jdrx.gis.beans.entity.basic;

import lombok.Data;

@Data
public class ExcelLinePo {
    /** 起点编码*/
    private String StartCode;

    /** 终点编码 */
    private String endCode;

    /** 材质
     */
    private String material;

    /** 管径 */
    private String caliber;

    /** 起点埋深*/
    private String startDepth;

    /** 终点埋深(m)
     */
    private String endDepth;

    /** 埋设类型
     */
    private String buryType;

    /** 勘测单位
     */
    private String surveyCompany;

    /** 勘测日期
     */
    private String surveyDate;

    /** 权属单位*/

    private String belong_to;

    /** 备注
     */
    private String remark;
    /**
     * 道路名称
     */
    private String address;
}
