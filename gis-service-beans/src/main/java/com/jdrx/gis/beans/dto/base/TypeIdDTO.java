package com.jdrx.gis.beans.dto.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author luobin
 * @Time 2019/12/6 0010 下午 3:57
 */
@Data
@ToString
public class TypeIdDTO {
    @ApiModelProperty("设备类型ID")
    @NotNull(message = "设备类型ID不能为空")
    Long id;

    @ApiModelProperty("时间戳")
    private String time;
}
