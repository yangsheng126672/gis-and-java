package com.jdrx.gis.beans.dto.basic;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

/**
 * @ClassName BookMarkDTO
 * @Description TODO
 * @Author yangsheng
 * @Date 2020/2/12 14:20
 * @Version 1.0
 */
@Data
public class BookMarkDTO {
    @ApiModelProperty("书签名称")
    private String name;

    @ApiModelProperty("书签图片url")
    private String url ;

    @ApiModelProperty("坐标x")
    private Double x;

    @ApiModelProperty("坐标y")
    private  Double y;

    @ApiModelProperty("地图显示级别")
    private  Integer zoom;

    @ApiModelProperty("权限值")
    private Long belongTo;

    @ApiModelProperty("创建人")
    private String creatBy;

    @ApiModelProperty("创建时间")
    private Date creatAt;

    @ApiModelProperty("修改人")
    private String updateBy;

    @ApiModelProperty("修改时间")
    private Date updateAt;
}
