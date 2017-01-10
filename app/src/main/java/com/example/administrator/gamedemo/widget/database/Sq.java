package com.example.administrator.gamedemo.widget.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Sq extends SQLiteOpenHelper{

	private String create_1 = "create table user1(_id integer primary key autoincrement,username text,pwd text,name_h text,user_id text)";
	
	public Sq(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL(create_1);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}
	
}
