package com.jdrx.gis.beans.dto.analysis;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description 爆管记录
 * @Author lr
 * @Time 2019/7/30 0030 下午 5:12
 */

@Data
public class AnalysisRecordDTO {
    @ApiModelProperty("爆管点编号")
    protected String code;
    @ApiModelProperty("爆管点空间信息")
    protected String pointgeom;
    @ApiModelProperty("第一次关阀列表")
    protected List<NodeDTO> valves_first;
    @ApiModelProperty("第一次关阀影响区域范围空间信息")
    protected String area_first;
    @ApiModelProperty("关阀失败的阀门列表")
    protected List<String> valve_failed;
    @ApiModelProperty("第二次关阀列表")
    protected List<NodeDTO> valve_second;
    @ApiModelProperty("第二次关阀影响区域范围空间信息")
    protected String area_second;
    @ApiModelProperty("时间")
    protected String datatime;
}
