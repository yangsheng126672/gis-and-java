package com.jdrx.gis.beans.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 加了此注解，mapper中的方法就不会在HeaderInterceptor被替换，即不会加权限（belong_to=?）
 * @Author: liaosijun
 * @Time: 2020/1/2 11:23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NoAuthData {
}
