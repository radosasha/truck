package com.truck.alexander.db;

import java.util.HashMap;
import java.util.LinkedList;

import com.truck.alexander.PageMainGUI;
import com.truck.alexander.modules.ReceiveCoordinateModule;
import com.truck.alexander.modules.ReportsService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;


public class DBConnector extends SQLiteOpenHelper{
	// db constants
	public static SQLiteDatabase db = null;
	public static DBConnector dbOpenHelper = null;
	public static LinkedList synchList = new LinkedList();

	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "gps";
	public static String currentTripID = null;
	Context context;
	
	private static final int FUEL = 1;
	private static final int COST = 2;
	private static final int CHECKPOINT = 3;
	
	
	/*
	 * authirization table
	 */
	public static final String TABLE_AUTH = "auth";
	public static final String field1 = "field1";// companyID/ driver(1/2)
	public static final String field2 = "field2";// unitID/ driverID
	public static final String field3 = "field3";// unitPin/ DriverPin
	public static final String field4 = "field4"; // vehicle/driver
	
	/*
	 * first table fields "tripinfo" table
	 */
	public static final String TABLE_TRIPINFO = "tripinfo";
	public static final String tripid = "tripid";	// 0
	public static final String date = "date";	// 1
	public static final String odometer = "odometer";	// 2
	public static final String destination = "destination";// 3
	public static final String memo = "memo";	// 4
	//public static final String frequency = "frequency";// 
	//public static final String command = "command"; //  start/stop
	public static final String status = "status";	// 5 pause/resume
	public static final String sendstatus = "sendstatus";	// 6 (0 - need send all, 1 - start sended, 2 - coors sended, 3 - all sended);
	
/*	public static final String startstatus = "startstatus"; // start send status //6
	public static final String coorsstatus = "coorsstatus"; // coors send status //7
	public static final String stopstatus = "stopstatus"; // stop send status 	 //8
*/	  
	/*
	 * coordinates table fields "
	 */
	public static final String TABLE_COORDS = "coords";
	public static final String xCoordinate = "x"; // 0 - coordinate X
	public static final String yCoordinate = "y"; // 1 - coordinate Y
//	public static final String address = "address"; // 2 - address
	public static final String accuracy = "accuracy"; // 3 - accuracy
	public static final String speed = "speed"; // 4 - speed
//	public static final String tripName = "tripname"; // 5 - trip name
	  
	/*
	 * report table fields
	 */
	private static final String TABLE_REPORTS = "reports";
	private static final String repotrsField = "report";
	public static final String odometerend = "odometerend";
	public static final String dateend = "dateend";
	/*
	 * fuel table fields
	 */
	public static final String TABLE_FUELINFO = "fuel"; // table name
	public static final String id = "id";// 0
	// already exists fields
	// "date";//1
	// "odometer";//2
	public static final String odometerplus = "odometerplus";// 3
	public static final String price = "price";// 4
	public static final String totalcost = "totalcost";// 5 
	public static final String mpg = "mpg";// 6
	public static final String fuel = "fuel";// 7
	// "memo";//7
	   
	/*
	 * cost table fields
	 */
	public static final String TABLE_COSTINFO = "cost"; // table name
	// "id";//0
	// "tripid"//1
	// "date";//2
	public static final String title = "title";// 3
	// "odometer";//4
	// "totalcost";//5
	// "memo";//6
	   
	   
	/*
	 * check point table fields
	 */
	public static final String TABLE_CHECKPOINTINFO = "checkpoint"; // table
																	// name
	// "id";//0
	// "tripid"//1
	// "date";//2
	public static final String city = "city";// 3
	// "memo";//4

	/*
	 * table "TRIP INFO" request
	 */
	private static final String CREATE_TABLE_AUTH = "create table "
			+ TABLE_AUTH + " (" +
			field1 + " TEXT, "+
			field2 + " TEXT, " + 
			field3  + " TEXT," + 
			field4  + " TEXT" + 
					");";
	  
	/*
	 * table "TRIP INFO" request
	 */
	private static final String CREATE_TABLE_TRIP_INFO = "create table "
		+ TABLE_TRIPINFO + " (" +
	   tripid+ " TEXT, "+
	   date + " TEXT, " + 
	   odometer + " INTEGER," + 
	   destination + " TEXT, " + 
	   memo + " TEXT," + 
	// frequency + " TEXT, " + 
    // command + " TEXT," +
	   status + " TEXT," +
	   sendstatus+" INTEGER"+
    //   startstatus +" INTEGER,"+
	//   coorsstatus +" INTEGER,"+
    //   stopstatus +" INTEGER"+
       	");";
	  
	
	
	private static final String CREATE_REPORTS_LIST = "create table "+TABLE_REPORTS+"("+
			
			repotrsField+" TEXT," +//0
			date + " TEXT, " +//1
			odometer + " INTEGER," +//2
			destination + " TEXT, " +//3
			memo + " TEXT," +//4
			"sendstatus INTEGER,"+//5
			odometerend+" INTEGER,"+//6
			dateend+" TEXT,"+//7
			"id INTEGER PRIMARY KEY AUTOINCREMENT"+ //8
					");";
	           
	

	  public DBConnector(Context context) {
	    super(context, DB_NAME, null,DB_VERSION);
	    this.context = context;	    
	  }

	  
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		Log.e("БД", "Запрос создания таблиц");
		// create tables		
		sqLiteDatabase.execSQL(CREATE_TABLE_AUTH);
		sqLiteDatabase.execSQL(CREATE_TABLE_TRIP_INFO);
//		sqLiteDatabase.execSQL(CREATE_TABLE_COORDINATES);
		sqLiteDatabase.execSQL(CREATE_REPORTS_LIST);
		//sqLiteDatabase.execSQL(CREATE_TABLE_FUEL_INFO);
		//sqLiteDatabase.execSQL(CREATE_TABLE_COST_INFO);
		//sqLiteDatabase.execSQL(CREATE_TABLE_CHECKPOINT_INFO);
		Log.e("БД ", "Таблицы созданы");
	} 
 
	public void createReportTables(SQLiteDatabase db, String tableName){
		String sql = null;		 
	
		// FUEL
		//
			sql = "create table fuel"+ tableName + " (" +
				//	tripName+ " TEXT, "+
					date + " TEXT, " + //0
					odometer + " INTEGER," + //1
					//odometerplus + " INTEGER," +
					odometerplus + " TEXT,"+//2
					price +" INTEGER," +//3
					totalcost + " INTEGER," +//4
					fuel + " INTEGER, " +//5
					//mpg +" INTEGER," +
					mpg + " TEXT,"+//6
					memo + " TEXT, " +//7
					"id INTEGER PRIMARY KEY AUTOINCREMENT"+//8
		        ");";
			db.execSQL(sql);
			Log.e("Report Table created",sql);
		//COST
		//
			sql ="create table cost"+ tableName + " (" +
				     // tripName + " TEXT, "+
				      date + " TEXT, " + //0
				      title + " TEXT," + //1
				      odometer +" INTEGER," +//2
				      //odometerplus +" INTEGER," +
				      //odometerplus +" TEXT," +
				      totalcost + " INTEGER," +//3
				      memo + " TEXT," +//4
				      "id INTEGER PRIMARY KEY AUTOINCREMENT"+
				        ");";
			db.execSQL(sql);
			Log.e("Report Table created",sql);
		//CHECK POINT
		//
			sql =     "create table checkp"+ tableName + " (" +
				     // tripName + " TEXT, "+
				      date + " TEXT, " + //1
				      city +" TEXT," +//2
				      memo + " TEXT," +//3
				      "id INTEGER PRIMARY KEY AUTOINCREMENT"+
				        ");";
		   db.execSQL(sql);
		   Log.e("Report Table created",sql);
		//COORDINATES
		//
		sql = "create table coors"+ tableName + " (" +
					  xCoordinate+ " REAL, "+
					  yCoordinate + " REAL, " + 
//					  address + " TEXT, " + 
					  accuracy + " REAL, " + 
					  speed + " REAL, " +  
//					  tripName + " TEXT, " +  
					  date + " TEXT"  +
					   		");"; 
		Log.e("Coors Table created",sql);
		db.execSQL(sql);
	}
	
	public void dropTables(SQLiteDatabase db,String tableName) {
		db.execSQL("DROP TABLE fuel" + tableName);
		Log.e("DataBase","DROP TABLE fuel" + tableName);
		db.execSQL("DROP TABLE cost" + tableName); 
		Log.e("DataBase","DROP TABLE cost" + tableName);
		db.execSQL("DROP TABLE checkp" + tableName);
		Log.e("DataBase","DROP TABLE checkp" + tableName);
		db.execSQL("DROP TABLE coors" + tableName);
		Log.e("DataBase","DROP TABLE coors" + tableName);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		Log.e("БД", "ON UPDATE");
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// Log.e("БД","ON OPEN");
		super.onOpen(db);
	}
	  
	/*
	 * insert record into TRIPINFO table
	 */
	/*public void insertConfig(SQLiteDatabase db, HashMap<String, String> ic) {
		// clear table
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
		
	}*/
	  

	/*
	 * @return 0 if TRIPINFO table is empty, else 1
	 */
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
		  //5 status
		  //6 startstatus
		  //7 coorsstatus
		  //8 stopstatus
		  Cursor cursor = db.query("tripinfo", null,
		 			null, null, null, null, null);
		  cursor.moveToNext();
		  String ret = cursor.getString(index);
		  cursor.close();
		  return ret;
	  }
	  
	@Deprecated
	/*
	 * select records from TRIPINO table
	 */
	public HashMap<String, String> selectFromConfigTable(SQLiteDatabase db) {
		Cursor cursor = db
				.query("tripinfo", null, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			// cursor not empty
			cursor.moveToNext();
			HashMap<String, String> ret = new HashMap<String, String>();
			ret.put("tripid", cursor.getString(0));
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
		cursor.close();
		// cursor is empty
		return null;
	}
	  
	
	/*
	 * insert new record into COORDS table
	 */
	public void insertCoords(SQLiteDatabase db, String x, String y,
			String address, String accuracy, String speed, 
			String date) {
		String row = "INSERT INTO coors" + ReceiveCoordinateModule.currTripName + " VALUES(" +
	                                                     ""+ x  +", "+ // x
	                                                     ""+y+", "+  // y
	                                                  //   "\""+address+"\", "+
	                                                     ""+accuracy+", "+
	                                                     ""+speed+", "+
	                                                   //  "\""+trip+"\", "+
	                                                     "\""+date+"\");";   // trip name
		Log.e("INSERT ROW", row);
		db.execSQL(row);
	}
	  
	/*
	 * collects statistics
	 */
	public String[] getStat(SQLiteDatabase db) {
		String[] res = new String[8];
		Cursor cursor = db.query("coors"+PageMainGUI.tripid, null, null, null, null, null, null);
		res[0] = "" + cursor.getCount(); // 0 - количество координат в БД
		if (Integer.parseInt(res[0]) > 0) {
			cursor.moveToLast();
			//res[1] = cursor.getString(0); // x
			//res[2] = cursor.getString(1); // y
			res[1] = ReportsService.lastSendedTime;
			res[3] = cursor.getString(2); // accuracy
			res[4] = cursor.getString(3); // speed
			res[5] = cursor.getString(4); // date
			res[6] = getAverageSpeed(cursor);
			res[7] = getAverageDistance(cursor);
		}
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return res;
	}

	/*
	 * cacl average distance between all points in DB
	 */
	private String getAverageDistance(Cursor cursor) {
		double averageDistance = 0.0; // return value
		cursor.moveToFirst();
		if (cursor.getCount() <= 1)
			return 0.0 + "";
		else {
			while (true) {
				Location originLocation = new Location("gps");
				Location destinationLocation = new Location("gps");
				// point A 
				originLocation.setLatitude(Double.parseDouble(cursor.getString(0)));
				originLocation.setLongitude(Double.parseDouble(cursor.getString(1)));
				if (cursor.moveToNext() == false)
					break;
				// point B
				destinationLocation.setLatitude(Double.parseDouble(cursor.getString(0)));
				destinationLocation.setLongitude(Double.parseDouble(cursor.getString(1)));
				double distance = originLocation.distanceTo(destinationLocation);
				averageDistance += distance;
			}
		}
		return averageDistance+"";
	}

	// method for calc average speed
	// summ all values and devide by values count
	private String getAverageSpeed(Cursor cursor) {

		double averageSpeed = 0.0; // return value
		int denominator = cursor.getCount(); // values count
		if (denominator <= 1)
			return 0.0 + "";
		else {
			cursor.moveToFirst();
			do {
				averageSpeed += Double.parseDouble(cursor.getString(3));
			} while (cursor.moveToNext());
			return (averageSpeed / (denominator - 1)) + "";
		}
	}
	
	/*
	 * select all coords from DB
	 */
	public LinkedList getAllCoordinatesFromDB(SQLiteDatabase db, String tripName) {
		Cursor cursor = db.query("coors"+tripName, null, null, null, null, null, null);
		LinkedList retData = new LinkedList();
		if (cursor.moveToFirst()) {
			do {
				LinkedList data = new LinkedList();
				data.add(cursor.getDouble(0));
				data.add(cursor.getDouble(1));
				data.add(cursor.getString(4));
				retData.add(data);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return retData;
	}

	/*
	 * clear records from request table
	 */
	public void deleteFrom(SQLiteDatabase db, String table) {
		Log.e("Request", "DELETE FROM " + table);
		db.execSQL("DELETE FROM " + table);
	}

	/*
	 * insert record into required table
	 */
	public void insertInto(SQLiteDatabase db, String table, String values) {
		db.execSQL("INSERT INTO " + table + " VALUES(" + values + ");");
	}

	/*
	 * set field "status" into trip info table
	 */
	public void setStatus(SQLiteDatabase db, String status) {
		db.execSQL("UPDATE tripinfo SET status = \"" + status + "\"");
	}


	/*
	 * select from request table
	 * @return Cursor of selected data
	 */
	public Cursor select(SQLiteDatabase db, String table) {		 
		return db.query(table, null, null, null, null, null, null);
	}


	public void sqlRequest(SQLiteDatabase db, String sql) {
		db.execSQL(sql);
	}
	  
	}
