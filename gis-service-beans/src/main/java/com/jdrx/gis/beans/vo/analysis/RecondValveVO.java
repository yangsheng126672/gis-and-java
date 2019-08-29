package com.jdrx.gis.beans.vo.analysis;

import com.jdrx.gis.beans.dto.analysis.NodeDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description
 * @Author lr
 * @Time 2019/8/1 0001 下午 1:29
 */
@Data
public class RecondValveVO {
    @ApiModelProperty("爆管点编号")
    String code;
    @ApiModelProperty("爆管点经纬度")
    BigDecimal[] point;
    @ApiModelProperty("影响范围")
    String area;
    @ApiModelProperty("关阀成功的阀门")
    List<NodeDTO> valveFirst;
    @ApiModelProperty("关阀成功的阀门")
    List<NodeDTO> valveSecond;
    @ApiModelProperty("关阀失败的阀门")
    List<NodeDTO> valveFailed;
}
