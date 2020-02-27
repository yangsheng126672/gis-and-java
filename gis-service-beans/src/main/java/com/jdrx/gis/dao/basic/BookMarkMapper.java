package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.dto.basic.BookMarkDTO;
import com.jdrx.gis.beans.entity.basic.BookMarkPO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


public interface BookMarkMapper {
    /**
     * 保存书签信息
     * @param dto
     * @return
     */
    int insertBookMark(BookMarkDTO dto);

    /**
     * 删除书签
     * @param
     * @return
     */
    int deleteBookMarkById(@Param("id") Long id,@Param("updateBy") String updateBy,@Param("date") Date date);
    /**
     * 获取书签信息
     * @param
     * @return
     */
    List<BookMarkPO> findBookMarkList(@Param("belongTo") Long belongTo);
}
