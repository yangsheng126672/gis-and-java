package com.jdrx.gis.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @Description
 * @Author lr
 * @Time 2019/12/11 0011 下午 5:17
 */

public class ArcgisUtil {
    /**
     * 清除arcgis缓存
     * @return
     */
    public static Boolean clearArcgisCache() {
        String url="http://192.168.80.51:6080/arcgis/admin/login?username=siteadmin&password=123456&redirect=http://192.168.80.51:6080/arcgis/admin/system/handlers/rest/" ;
        //构造HttpClient的实例
        HttpClient httpClient = new HttpClient();
        //创建GET方法的实例
        GetMethod getMethod = new GetMethod(url);
        int statusCode;
        try {
            statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
