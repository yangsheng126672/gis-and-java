package com.jdrx.gis.util;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class JavaFileToFormUpload {
	/** 
     * 模拟form表单的形式 ，上传文件 以输出流的形式把文件写入到url中，然后用输入流来获取url的响应 
     *  
     * @param url 
     *            请求地址 form表单url地址 
     * @param filePath 
     *            文件在服务器保存路径 
     * @return String url的响应信息返回值a 
     * @throws IOException 
     */  
    public static String send(String url, String filePath) throws IOException {
        String result = null;  
        File file = new File(filePath);  
        if (!file.exists() || !file.isFile()) {  
            throw new IOException("文件不存在:"+filePath);  
        }  
        URL urlObj = new URL(url);  
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();  
        con.setRequestMethod("POST"); // 设置关键值,以Post方式提交表单，默认get方式  
        con.setDoInput(true);  
        con.setDoOutput(true);  
        con.setUseCaches(false); // post方式不能使用缓存  
        // 设置请求头信息  
        con.setRequestProperty("Connection", "Keep-Alive");  
        con.setRequestProperty("Charset", "UTF-8");  
        // 设置边界  
        String BOUNDARY = "----------" + System.currentTimeMillis();  
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary="+ BOUNDARY);  
        // 请求正文信息  
        // 第一部分：  
        StringBuilder sb = new StringBuilder();  
        sb.append("--"); // 必须多两道线  
        sb.append(BOUNDARY);  
        sb.append("\r\n");  
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\""+ file.getName() + "\"\r\n");  
        sb.append("Content-Type:application/octet-stream\r\n\r\n");  
        byte[] head = sb.toString().getBytes("utf-8");  
        // 获得输出流  
        OutputStream out = new DataOutputStream(con.getOutputStream());  
        // 输出表头  
        out.write(head);  
        // 文件正文部分  
        // 把文件已流文件的方式 推入到url中  
        DataInputStream in = new DataInputStream(new FileInputStream(file));  
        int bytes = 0;  
        byte[] bufferOut = new byte[1024];  
        while ((bytes = in.read(bufferOut)) != -1) {  
            out.write(bufferOut, 0, bytes);  
        }  
        in.close();  
        // 结尾部分  
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线  
        out.write(foot);  
        out.flush();  
        out.close();  
        StringBuffer buffer = new StringBuffer();  
        BufferedReader reader = null;  
        try {  
            // 定义BufferedReader输入流来读取URL的响应  
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));  
            String line = null;  
            while ((line = reader.readLine()) != null) {  
                buffer.append(line);  
            }  
            if (result == null) {  
                result = buffer.toString();  
            }
	        JSONObject object = JSONObject.parseObject(result);
            String data = object.get("data").toString();
            object = JSONObject.parseObject(data);
            result = object.get("url").toString();  
        } catch (IOException e) {  
            System.out.println("发送POST请求出现异常！" + e);  
            e.printStackTrace();  
            throw new IOException("数据读取异常");  
        } finally {  
            if (reader != null) {  
                reader.close();  
            }  
            file.delete();
        }  
        return result;  
    }  
  
    public static void main(String[] args) throws IOException {  
        String filePath = "E://1111111111111111111.jpg";  
        String sendUrl = "http://192.168.40.253:8688/cfs/oapi/0/cloud/disk/OpenFileUploadApi/upload";  
        String result = null;  
        JavaFileToFormUpload fileUpload = new JavaFileToFormUpload();  
        result = fileUpload.send(sendUrl, filePath); 
        System.out.println(result);  
        
    }  
}
