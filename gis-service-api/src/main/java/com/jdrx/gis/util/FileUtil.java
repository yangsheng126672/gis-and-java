package com.jdrx.gis.util;

import java.io.*;

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
}
