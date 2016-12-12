package com.dariydiary.newdiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
	
	public final static String TABLE_NAME = "notes";
	public final static String ID = "_id";
	public final static String CONTENT = "content";
	public final static String TIME = "time";
	public final static String IMAGE_PATH = "image_path";
	public final static String BACKGROUNDIMG_PATH = "background_image_path";
	
	public Database(Context context) {
		super(context, "notes", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table "+TABLE_NAME+"("
		+ID+" integer primary key autoincrement,"
		+CONTENT+" text,"
		+TIME+" text,"
		+IMAGE_PATH+" text,"
		+BACKGROUNDIMG_PATH+" text"+")");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub		
	}
}

