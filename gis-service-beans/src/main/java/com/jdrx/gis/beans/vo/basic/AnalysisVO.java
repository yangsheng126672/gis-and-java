package com.jdrx.gis.beans.vo.basic;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author ys
 * @Time 2019/12/4
 */
@Data
public class AnalysisVO implements Serializable {
    //设备名称
    String name;

    //编码
    String code;

    //空间信息
    String geom;

}
