package com.example.wzy.guizhouandroid.server;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class StreamTool {

	/**
	 * 读取流中数据
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] read(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		//循环读取 直到数据长度 为-1 
		while((len = inStream.read(buffer))!= -1){
			outStream.write(buffer, 0, len);	//读到的数据放入内存
		}
		inStream.close();
		
		return outStream.toByteArray();
	};
	/*
	 * JSON数据
	 */
	public static String parseJson(InputStream inStream) throws Exception {
		byte[] data = read(inStream);
		String json = new String(data, "GB2312");
		Log.e("收到JSON",json);
		return json;
	}
}