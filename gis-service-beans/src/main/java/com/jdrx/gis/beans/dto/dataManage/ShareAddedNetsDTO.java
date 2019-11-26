package com.jdrx.gis.beans.dto.dataManage;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/21 0021 下午 2:36
 */
@Data
public class ShareAddedNetsDTO {
    @ApiModelProperty("管点list")
    List<SharePointDTO> pointList;

    @ApiModelProperty("管线list")
    List<ShareLineDTO> lineList;
}
