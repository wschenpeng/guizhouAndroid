package com.example.wzy.guizhouandroid.model;

/**
@Name:flag类 *
@Description:登录之后set一下进行保存，在Activity中需要用到flag直接调用该静态方法 flag=Flag.getFlag();* 
@author wuzhuoyu * 
@Version:V1.00 * 
@Create Date:2018-09-08 *
*/
public  class Flag   
{
	public static String flag;
		
	public  static String getFlag() {
		return flag;
	}

	public static void setFlag(String flag) {
		Flag.flag=flag;
	}


}
