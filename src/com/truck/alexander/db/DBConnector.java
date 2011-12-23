package com.truck.alexander.db;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class DBConnector extends SQLiteOpenHelper{

	  private static final int DB_VERSION = 1;
	  private static final String DB_NAME = "gps";
	  Context context;
	  /*
	   * first table fields 
	   * "tripinfo" table
	   */
	  public static final String TABLE_TRIPINFO = "tripinfo";
	  public static final String tripid = "tripid";//0
	  public static final String date = "date";//1
	  public static final String odometer = "odometer";//2
	  public static final String destination = "destination";//3
	  public static final String memo = "memo";//4
	  public static final String frequency = "frequency";//5
	  public static final String command = "command"; //6  start/stop
	  public static final String status = "status";//7  pause/resume
	  
	  /*
	   * second table fields
	   * "
	   */
	  public static final String TABLE_COORDS = "coords";
	  //public static final String id = "id";
	  public static final String xCoordinate = "x";
	  public static final String yCoordinate = "y";
	  public static final String tripName = "tripname";
	  
	  /*
	   * CONSTANTS
	   */
	  
	  
	  /*
	   * table "TRIP INFO" request
	   */
	  private static final String CREATE_TABLE_TRIP_INFO = "create table " + TABLE_TRIPINFO + " ("+
	   tripid+ " TEXT, "+
	   date + " TEXT, " + 
	   odometer + " TEXT," + 
	   destination + " TEXT, " + 
	   memo + " TEXT," + 
	   frequency + " TEXT, " + 
	   command + " TEXT," +
	   status + " TEXT" +
	   		");";
	  
	  /*
	   * table "COORDINATES" request
	   */
	  private static final String CREATE_TABLE_COORDINATES = "create table " + TABLE_COORDS + " ("+
			  xCoordinate+ " TEXT, "+
			  yCoordinate + " TEXT, " + 
			  tripName + " TEXT"  +
			   		");";

	  public DBConnector(Context context) {
	    super(context, DB_NAME, null,DB_VERSION);
	    this.context = context;	    
	  }

	  @Override
	  public void onCreate(SQLiteDatabase sqLiteDatabase) {
		Log.e("БД","Запрос создания таблиц");
		// on create tables request
	    sqLiteDatabase.execSQL(CREATE_TABLE_TRIP_INFO);
	    sqLiteDatabase.execSQL(CREATE_TABLE_COORDINATES);
	    Log.e("БД ","Таблицы созданы");	    
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		  
		  Log.e("БД","ON UPDATE");
		  
	  }
	  @Override
	public void onOpen(SQLiteDatabase db) {
		  
	   // Log.e("БД","ON OPEN");
		super.onOpen(db);
				
	  }
	  
	  public void insertConfig(SQLiteDatabase db, HashMap<String,String> ic){
		  //clear table
		  db.execSQL("delete from tripinfo");
		  // insert into table 
		  db.execSQL("INSERT into tripinfo values(" +
			 		"\""+ic.get("tripid")+"\","+
			 		"\""+ic.get("date")+"\","+
			 		"\""+ic.get("odometer")+"\","+
			 		"\""+ic.get("destination")+"\","+
			 		"\""+ic.get("memo")+"\","+
			 		"\"30\","+ // временное значение, частота = 30 минут
			 		"\"start\"," +
			 		"\"pause\")"
			 		);
		  //test
		  Cursor cursor = db.query("tripinfo", null,
		 			null, null, null, null, null);
			if (cursor.getCount() != 0)
			{
		 		Log.e("БД", "КОЛИЧЕСТВО СТРОК"+ cursor.getCount());
				cursor.moveToNext();
				Log.e("1",cursor.getString(0));
				Log.e("2",cursor.getString(1));
				Log.e("3",cursor.getString(2));
				Log.e("4",cursor.getString(3));
				Log.e("5",cursor.getString(4));
				Log.e("6",cursor.getString(5));
				Log.e("7",cursor.getString(6));
				Log.e("7",cursor.getString(7));
				
			}
			cursor.close();
	  }
	  

	  public int isDbNull(SQLiteDatabase db){
		  Cursor cursor = db.query("tripinfo", null,
		 			null, null, null, null, null);
			if (cursor.getCount() != 0)
			{
				cursor.close();
				return 1; // not null
			}
			cursor.close();
			return 0; // null
	  }
	  
	  public String getValueByIndex(SQLiteDatabase db,int index){
		  //0 tripid
		  //1 date
		  //2 odometer
		  //3 destination
		  //4 memo
		  //5 frequency
		  //6 command
		  //7 status
		  Cursor cursor = db.query("tripinfo", null,
		 			null, null, null, null, null);
		  cursor.moveToNext();
		  return cursor.getString(index);
	  }
	  
	  public HashMap<String,String> selectFromConfigTable(SQLiteDatabase db){
		  Cursor cursor = db.query("tripinfo", null,
		 			null, null, null, null, null);
		  cursor.moveToNext();
		  
		  HashMap<String,String> ret = new HashMap<String,String>();
		  ret.put("tripid" , cursor.getString(0));
		  ret.put("date", cursor.getString(1));
		  ret.put("odometer", cursor.getString(2));
		  ret.put("destination", cursor.getString(3));
		  ret.put("memo", cursor.getString(4));
		  ret.put("frequency", cursor.getString(4));
		  ret.put("command", cursor.getString(5));
		  ret.put("status", cursor.getString(6));
		  cursor.close();
		  
		return ret;		  
	  }
	}
