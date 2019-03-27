package com.example.wzy.guizhouandroid.server;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class PreferencesUsers {

	private Context  context;	//	上下文对象

	public PreferencesUsers(Context context) { //通过构造函数引入上下文对象
		this.context = context;
	};

	/**
	 * 保存参数
	 * @param user 系统用户名
	 * @param pswd 密码
	 */
	public void save(String user,String pswd) {
		//itcast  保存参数的文件的名称，后缀名省略    文件操作模式
		SharedPreferences  preferences  =context.getSharedPreferences("usermanage", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();	//取得编辑器对象
		editor.putString("user", user);		//存放数据在内存中
		editor.putString("pswd", pswd);
		editor.commit();	//提交数据到xml文件中
	};

	/**
	 * 获取参数
	 * @return
	 */
	public Map<String, String> getPreferences(){
		Map<String, String> params = new HashMap<String, String>();
		SharedPreferences  preferences  =context.getSharedPreferences("usermanage", Context.MODE_PRIVATE);
		params.put("user", preferences.getString("user", ""));	//获取参数   如果xml不存储在参数就会返回第二个参数
		params.put("pswd", preferences.getString("pswd", ""));
		return params;
	};
	public Map<String, String> getPreferences2(){
		Map<String, String> params = new HashMap<String, String>();
		SharedPreferences  preferences  =context.getSharedPreferences("userdata", Context.MODE_PRIVATE);
		params.put("user", preferences.getString("username", ""));	//获取参数   如果xml不存储在参数就会返回第二个参数
		params.put("pswd", preferences.getString("password", ""));
		return params;
	};

}
