package com.jdrx.gis.beans.dto.analysis;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author lr
 * @Time 2019/7/30 0030 下午 9:24
 */
@Data
public class WaterUserInfoDTO {
    @ApiModelProperty("用戶id")
    protected String userId;
    @ApiModelProperty("用戶類型")
    protected String useType;
    @ApiModelProperty("用戶姓名")
    protected String userName;
    @ApiModelProperty("用户电话")
    protected String tel;
    @ApiModelProperty("用户地址")
    protected String address;
}
