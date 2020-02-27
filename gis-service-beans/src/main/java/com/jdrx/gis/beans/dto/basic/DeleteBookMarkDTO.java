package com.jdrx.gis.beans.dto.basic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName DeleteBookMarkDTO
 * @Description TODO
 * @Author yangsheng
 * @Date 2020/2/24 13:30
 * @Version 1.0
 */
@Data
public class DeleteBookMarkDTO {
    @ApiModelProperty("删除id")
    private Long id;

    @ApiModelProperty("书签图片url")
    private String url;
}
