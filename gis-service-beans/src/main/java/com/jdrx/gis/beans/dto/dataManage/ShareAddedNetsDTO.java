package com.jdrx.gis.beans.dto.datamanage;


import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/21 0021 下午 2:36
 */
@Data
public class ShareAddedNetsDTO {
    //管点list
    List<SharePointDTO> pointList;
    //管线list
    List<ShareLineDTO> lineList;
}
