package com.eye.visualization;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDbAdapter extends SQLiteOpenHelper {

	private SQLiteDatabase db;
	private Cursor c;

	public MyDbAdapter(Context context) {
		super(context, "whwkfyd91", null, 1);		//DB: "whwkfyd91"
		db = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) { 		
		db.execSQL("CREATE TABLE LISTTABLE(date TEXT, hrv TEXT, filt TEXT,heartrate TEXT);");	
	}
	
	//***********************insert HRV Info *****************************
	public void hrvGenerator(String date, ArrayList<Double> arrayList, ArrayList<Double> filt, String heartrate) {
		StringBuffer sb=new StringBuffer();
		StringBuffer sb2=new StringBuffer();
		for(int i=0;i<arrayList.size();i++)
			sb.append(arrayList.get(i)+"\n");
		for(int i=0;i<filt.size();i++)
			sb2.append(filt.get(i)+"\n");
		
		db.execSQL("INSERT INTO LISTTABLE(date, hrv, filt,heartrate) " +
				"VALUES('" + date + "','" + sb.toString() + "','" + sb2.toString() + "','"+heartrate+"');");
	}
	
	//***********************return Cusor ********************************
	public Cursor hrvReading() {
		c = db.rawQuery("SELECT *FROM LISTTABLE;", null);
		return c;
	}
	
	//***********************delete HRV Info *****************************
	public void hrvRemover(String date) {
		db.execSQL("DELETE FROM LISTTABLE WHERE date = '"+ date + "';");
		
	}

	//***********************Upgrade ...   X ******************************
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
