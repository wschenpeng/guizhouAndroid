package com.example.wzy.guizhouandroid.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public DBOpenHelper(Context context) {
		super(context, "itcast.db", null, 1);	//	创建数据库 以及版本号，当版本升级时变成2调用onUpgrade，默认保存在当前应用所在pakage.database下
		// TODO Auto-generated constructor stub
	};

	/**
	 * 数据库第一次被创建的时候调用，生产数据表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE person(personid integer primary key autoincrement,name varchar(20),logtime varchar(20))");	// varchar(20)可以省略,存储任何类型 的数据并且长度不受限制
		db.execSQL("CREATE TABLE house(personid integer primary key autoincrement,name varchar(20),logtime varchar(20))");	// varchar(20)可以省略,存储任何类型 的数据并且长度不受限制
	
	};
	

	/**
	 * 数据库版本号发生变更时被调用
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL("ALTER TABLE person ADD amount varchar(20) null");	//	额外的给person表添加一个字段

	};

}
