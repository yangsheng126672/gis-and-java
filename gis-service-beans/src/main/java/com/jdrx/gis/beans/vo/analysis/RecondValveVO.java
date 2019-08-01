package com.jdrx.gis.beans.vo.analysis;

import com.jdrx.gis.beans.dto.analysis.NodeDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author lr
 * @Time 2019/8/1 0001 下午 1:29
 */
@Data
public class RecondValveVO {
    @ApiModelProperty("关阀成功的阀门")
    List<NodeDTO> valves;
    @ApiModelProperty("关阀失败的阀门")
    List<NodeDTO> failedValves;
}
