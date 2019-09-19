package com.jdrx.gis.beans.dto.analysis;

import com.jdrx.gis.beans.dto.base.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * @Description
 * @Author lr
 * @Time 2019/8/1 0001 上午 9:42
 */

@Data
public class RecondParamasDTO extends PageDTO{
    @ApiModelProperty("爆管点编号")
    String code;
    @ApiModelProperty("开始时间")
    Date startTime;
    @ApiModelProperty("结束时间")
    Date endTime;
}
