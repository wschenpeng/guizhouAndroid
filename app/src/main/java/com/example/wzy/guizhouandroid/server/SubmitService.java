package com.example.wzy.guizhouandroid.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.Map;

public class SubmitService {

	private Context context;	//	上下文对象
	
	public SubmitService(Context context) { //通过构造函数引入上下文对象
		this.context = context;
	};

	/**
	 * 保存参数
	 * @param name姓名
	 * @param age年龄
	 */
	public void save(String serviceip) {
		//itcast  保存参数的文件的名称，后缀名省略    文件操作模式
		SharedPreferences preferences  =context.getSharedPreferences("example", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();	//取得编辑器对象
		editor.putString("serviceip", serviceip);		//存放数据在内存中
		editor.commit();	//提交数据到xml文件中
	};
	
	/**
	 * 获取参数
	 * @return
	 */
	public Map<String, String> getPreferences(){
		Map<String, String> params = new HashMap<String, String>();
		SharedPreferences preferences  =context.getSharedPreferences("example", Context.MODE_PRIVATE);
		params.put("serviceip", preferences.getString("serviceip", ""));	//获取参数   如果xml不存储在参数就会返回第二个参数
		return params;
	};

}
