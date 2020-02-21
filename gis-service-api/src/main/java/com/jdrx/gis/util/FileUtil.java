package com.jdrx.gis.util;

import sun.misc.BASE64Decoder;

import java.io.*;
import java.util.Objects;

/**
 * @Author: liaosijun
 * @Time: 2019/12/23 15:51
 */
public class FileUtil {

	/**
	 * 读入inputStream, 写出到outPath文件中
	 * @param inputStream
	 * @param outPath
	 */
	public static void bufferedWrite(InputStream inputStream, String outPath){
		BufferedInputStream buffInStream = null;
		BufferedOutputStream buffOutStream = null;
		try {

			buffInStream = new BufferedInputStream(inputStream);
			File outFile = new File(outPath);
			buffOutStream = new BufferedOutputStream(new FileOutputStream(outFile));
			int len;
			byte b [] = new byte[1024];
			while((len = buffInStream.read(b))!=-1){
				buffOutStream.write(b,0,len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(buffInStream!=null)
					buffInStream.close();
				if(buffOutStream!=null)
					buffOutStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将base64位的String转化为图片写入到本地
	 */
	public static boolean GenerateImage(String imgStr, String localPath) {
		if (Objects.isNull(imgStr)) {
			return false;
		}
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; i++) {
				if (b[i] < 0) {
					//调整异常数据
					b[i] += 256;
				}
			}
			File file = new File(localPath);
			if (!file.exists()) {
				file.getParentFile().mkdir();
			}
			OutputStream out = new FileOutputStream(file);
//			BufferedOutputStream bufferOut = new BufferedOutputStream(out);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}


}
