package com.jdrx.gis.beans.dto.analysis;

import com.jdrx.gis.beans.dto.analysis.NodeDTO;
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
    protected String name;
    @ApiModelProperty("爆管点编号")
    protected String code;
    @ApiModelProperty("爆管点空间信息")
    protected Double[] point;
    @ApiModelProperty("关阀失败的阀门列表")
    protected List<String> valveFailed;
    @ApiModelProperty("一次关阀可关阀门列表")
    protected List<String> valveFirst;
    @ApiModelProperty("二次关阀可关阀门列表")
    protected List<String> valveSecond;
    @ApiModelProperty("影响区域范围空间信息")
    protected String area;

}
