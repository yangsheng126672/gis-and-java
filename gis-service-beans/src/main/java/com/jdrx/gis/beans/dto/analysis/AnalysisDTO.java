package com.jdrx.gis.beans.dto.analysis;

import com.jdrx.gis.beans.dto.base.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author lr
 * @Time 2019/8/26 0026 上午 11:27
 */
@Data
public class AnalysisDTO extends PageDTO{
    @ApiModelProperty("爆管线设备id")
    private Long dev_id;
}
