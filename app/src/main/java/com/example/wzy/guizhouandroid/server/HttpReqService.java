package com.example.wzy.guizhouandroid.server;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public class HttpReqService {
	
	/**
	 * 向服务器发送请求参数
	 * @param path  请求路径
	 * @param params 请求参数
	 * @param ecoding 编码方式
	 * @return
	 * @throws Exception
	 */ 
	public static String postRequest(String path, Map<String, Object> reqparams, String ecoding) throws Exception {
		 
		 StringBuffer data = new StringBuffer();
		 Iterator it = reqparams.entrySet().iterator();
		 while (it.hasNext()) {  
	            Map.Entry element = (Map.Entry) it.next();
	            data.append(element.getKey());  
	            data.append("=");  
	            data.append(element.getValue());  
	            data.append("&");  
	     }  
	     if (data.length() > 0) {  
	    	 data.deleteCharAt(data.length() - 1);  
	     }
	     byte[] entity = data.toString().getBytes();// 生成实体数据
	     
	     //发送请求到服务器
		 try{
			URL url = new URL(path);
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();//基于http协议连接对象,打开和URL之间的连接
			conn.setConnectTimeout(5000); //请求超时时间为5s
			conn.setRequestMethod("POST"); //请求方式
			// 发送POST请求必须设置如下两行  
			conn.setDoOutput(true);  //URL 连接可用于输入和/或输出。如果打算使用 URL 连接进行输出，则将 DoOutput 标志设置为 true；
			conn.setDoInput(true);  //如果打算使用 URL 连接进行输入，则将 DoInput 标志设置为 true；
			
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//设置文件类型
			conn.setRequestProperty("Content-Length", String.valueOf(entity.length));//设置文件长度
			
			OutputStream outStream = conn.getOutputStream();//获得输出流对象（其实通过这个就可以往这个请求里面写数据，这样网站那就可以获得数据了）
			outStream.write(entity);	// 数据写到内部缓存，还未往WEB发送
			//conn.getOutputStream().write(entity);
			Log.d("debugTest","CODE -- "+conn.getResponseCode());
			if(conn.getResponseCode() == 200){
				InputStream inStream = conn.getInputStream();	//得到输入流
				byte[]  outdata = StreamTool.read(inStream);
				String json = new String(outdata, "GB2312"); //转化为中文字符 GB2312：表示简体中文字符集。
				//Log.i("从服务器上收到JSON数据",json);
				conn.disconnect();
				return json;
			}
			if(conn.getResponseCode() != 200) {
				return "1";
			}
			
			conn.disconnect();
			return null;
		 }catch (Exception e){
			 System.out.println("发送POST请求出现异常！" + e);
	         e.printStackTrace();  
	         return "1";	//只要有异常捕获我就返回一个"1"字符串。这样界面就可以获得网络错误的结果。
		 }
	};
}
