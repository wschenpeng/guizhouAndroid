package com.example.wzy.guizhouandroid.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PersonService {
	
	private DBOpenHelper dbOpenHelper;
	
	public PersonService(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
	};
	
	/**
	 * 添加记录
	 * @param person
	 */
	public void save(Person person){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();	//	取得数据库操作实例
		
		//表示特殊字符转义，?站位符
		db.execSQL("insert into person(name,logtime) values(?,?)",
				new Object[]{person.getName(),person.getLogtime()});
		//db.close();	//	关闭数据库，也可以不关
	};
	
	/**
	 * 删除记录
	 * @param id   记录id
	 */
	public void delete(Integer id){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();	//	取得数据库操作实例
		
		//表示特殊字符转义，?站位符
		db.execSQL("delete from person where personid=?",new Object[]{id});
		
	};
	
	/**
	 * 更新记录 
	 * @param person
	 */
	public void update(Person person){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();	//	取得数据库操作实例
		
		//表示特殊字符转义，?站位符
		db.execSQL("update person set name=?,logtime=? where personid=?",
				new Object[]{person.getName(),person.getLogtime(),person.getId()});
	};
	
	/**
	 * 查找记录
	 * @param id
	 * @return
	 */
	public Person find(Integer id){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();	//读数据库操作实例    当数据库满时，可读，当数据库没满时，可读可写
		Cursor cursor = db.rawQuery("select * from person where personid=?", new String[]{id.toString()});
		if(cursor.moveToFirst())
		{
			int personid = cursor.getInt(cursor.getColumnIndex("personid"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String logtime = cursor.getString(cursor.getColumnIndex("logtime"));
			return new Person(personid,name,logtime);
		}
		cursor.close();
		return null;
	};
	
	
	
	/**
	 * 获取记录总数
	 * @return
	 */
	public int getCount(){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();	//读数据库操作实例    当数据库满时，可读，当数据库没满时，可读可写
		Cursor cursor = db.rawQuery("select count(*) from person ", null);
		cursor.moveToFirst();
		int result = (int) cursor.getLong(0);
		return result;
	};
	
	/**
	 * 分页查询
	 * @param offset  跳过前面多少条记录
	 * @param maxResult  每页获取多少条记录
	 * @return
	 */
	public Cursor getCursorScroolData(int offset, int maxResult){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();	//读数据库操作实例    当数据库满时，可读，当数据库没满时，可读可写
		Cursor cursor = db.rawQuery("select personid as _id,name,logtime from person order by personid asc limit ?,?",
				new String[]{String.valueOf(offset), String.valueOf(maxResult)}); //as 把主键id改成别名_id
		return cursor;
	}
}
