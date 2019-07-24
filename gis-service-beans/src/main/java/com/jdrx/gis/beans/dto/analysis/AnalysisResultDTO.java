package com.jdrx.gis.beans.dto.analysis;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author lr
 * @Time 2019/7/19 0019 下午 3:01
 */
@Data
public class AnalysisResultDTO {
    @ApiModelProperty("必须关闭的阀门列表")
    protected List<NodeDTO> fmlist;
    @ApiModelProperty("影响区域范围空间信息")
    protected String geom;
}
