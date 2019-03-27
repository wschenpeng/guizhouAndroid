package com.example.wzy.guizhouandroid.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.Map;

public class PreferencesService {

	private Context context;	//	上下文对象
	
	public PreferencesService(Context context) { //通过构造函数引入上下文对象
		this.context = context;
	};

	/**
	 * 保存参数
	 * @param serviceIP 服务器ip
	 * @param netIP 网关ip
	 * @param netPort 网关端口
	 * @param vedioIP 视频ip
	 * @param vedioPort 视频端口
	 * @param vedioUser 视频用户名
	 * @param vedioPsw  shipinm
	 */
	public void save(String serviceIP, String vedioIP,
                     String vedioPort, String vedioUser, String vedioPsw) {
		//itcast  保存参数的文件的名称，后缀名省略    文件操作模式
		SharedPreferences preferences  =context.getSharedPreferences("netpara", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();	//取得编辑器对象
		editor.putString("serviceIP", serviceIP);		//存放数据在内存中
//		editor.putString("netIP", netIP);
//		editor.putString("netPort", netPort);
		editor.putString("vedioIP", vedioIP);
		editor.putString("vedioPort", vedioPort);
		editor.putString("vedioUser", vedioUser);
		editor.putString("vedioPsw", vedioPsw);
		editor.commit();	//提交数据到xml文件中
	};
	
	/**
	 * 获取参数
	 * @return
	 */
	public Map<String, String> getPreferences(){
		Map<String, String> params = new HashMap<String, String>();
		SharedPreferences preferences  =context.getSharedPreferences("netpara", Context.MODE_PRIVATE);
		params.put("serviceIP", preferences.getString("serviceIP", ""));	//获取参数   如果xml不存储在参数就会返回第二个参数
		params.put("netIP", preferences.getString("netIP", ""));
		params.put("netPort", preferences.getString("netPort", ""));
		params.put("vedioIP", preferences.getString("vedioIP", ""));
		params.put("vedioPort", preferences.getString("vedioPort", ""));
		params.put("vedioUser", preferences.getString("vedioUser", ""));
		params.put("vedioPsw", preferences.getString("vedioPsw", ""));
		return params;
	};

}
