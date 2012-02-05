package com.truck.alexander.modules;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import android.R.id;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.MailTo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.gwttest.client.mapping.Coordinate;
import com.gwttest.client.mapping.Cost;
import com.gwttest.client.mapping.Fuel;
import com.gwttest.client.mapping.Trip;
import com.gwttest.server.socket.serializable.AuthData;
import com.gwttest.server.socket.serializable.TripData;
import com.truck.alexander.ErrorsTypes;
import com.truck.alexander.PageMainGUI;
import com.truck.alexander.db.DBConnector;
import com.truck.tools.JSONParser;

public class ReportSender {
	// server address
	private final String HOST = "107.21.98.186";
	// server port
	private final int PORT = 15883;
	// socket
	Socket client = null;
	// db constants
	SQLiteDatabase db;
	DBConnector dbOpenHelper;
	Context ct;
	public ReportSender(Context ct) {
		this.ct = ct;
	}

	
	public int sendCoors(String tripName) {
		openDB();
		Iterator ite = dbOpenHelper.getAllCoordinatesFromDB(db,tripName).iterator();
		Cursor vehInfo = db.rawQuery("select field1, field2 from auth where field4 = 'vehicle'", null);
		vehInfo.moveToFirst();
		/*String coars = "{" + 
				"'COMMAND':'SEND_COORS',"+
				"'COMPANY_ID':'"+vehInfo.getString(0)+"',"+
				"'VEHICLE_ID':'"+vehInfo.getString(1)+"',"+
		        "'COORS': [ ";
		        		*/
         
        ArrayList<Coordinate> coordinateList = new ArrayList<Coordinate>();
		while (ite.hasNext()) {
			LinkedList crData = (LinkedList) ite.next();
			Coordinate coord = new Coordinate();
            coord.setCoorX((Double) crData.get(0));
            coord.setCoorY((Double) crData.get(1));
            coord.setDate((String) crData.get(2));
            coordinateList.add(coord);

			/*String[] row = (String[]) ite.next();			
			coars += "{" + "\"X\":" + row[0] + "," + 
			               "\"Y\":" + row[1] + "," + 
			               "\"T\":'" + row[2] + "'" + "}";
			if (ite.hasNext())
				coars += ", ";*/
		}
		//coars += "]" + "}";
		vehInfo.close();
		int result = writeData(1,1, coordinateList);
		if(result == 0) {
			//db.execSQL("delete from coors"+ReportsService.currTripName);
			db.execSQL("delete from coors"+tripName);			
			final Calendar c = Calendar.getInstance();
			ReportsService.lastSendedTime = c.get(Calendar.DAY_OF_MONTH)+"/"+ c.get(Calendar.MONTH)+"/"+c.get(Calendar.YEAR)+" "+
		    c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);
		}
		closeDB();
		return result;
	}
	
	/*
	 * @param tableType 0 - select from tripInfo , 1 - select from reports 
	 */
	public boolean sendStart(int tableType, int id){
		// open DataBase```````````````````````````````````
		openDB();
		/*
		 * receive "start" data from table
		 */		
		Cursor startInfo = null;
		switch(tableType){
		case 0:
			startInfo = db.rawQuery("select * from tripinfo", null);			
			break;
		case 1:
			startInfo = db.rawQuery("select * from reports where id = "+id, null);
			
		}		
		// select fields
		startInfo.moveToFirst();
	    String tripID = startInfo.getString(0);
	    String date = startInfo.getString(1);
	    int odometer = startInfo.getInt(2);
	    String destinaton = startInfo.getString(3);
	    String memo = startInfo.getString(4);
	    startInfo.close();	
	    // update handler 
	    if(tableType == 1){
	    	// notification
	    	Message msg = new Message();
	    	Bundle data = new Bundle();
	    	data.putInt("cmnd", 1);
	    	msg.setData(data);
	    	sendReportHandler.sendMessage(msg);
	    }
	    Trip trip = new Trip();
		trip.setTitle(tripID);
		trip.setDestination(destinaton);
		trip.setOdometerStart(odometer);
		trip.setDateStart(date);
		trip.setMemo(memo);

		TripData tripData = new TripData();
		tripData.command = "START_TRIP";
		tripData.trip = trip;

	    
		/*String startData = "{"+
		"'COMMAND':'START_TRIP',"+
		"'TRIP_TITLE':'"+tripID+"',"+
		"'DESTINATION':'"+destinaton+"',"+
		"'ODOMETER_START':"+odometer+","+
		"'START_DATE':'"+date+"',"+
		"'MEMO':'"+memo+"'"+
	   "}";*/
		
		// send start command
		int startResult = writeData(1,0,tripData);
		if(startResult != 0){
			Log.e("Report Sender","Write data error");
			closeDB();
			return false;
		}
		switch(tableType){
		case 0:
			db.execSQL("update tripinfo set sendstatus = 1");
			break;
		case 1:
			db.execSQL("update reports set sendstatus = 1 where id = "+id);
		}
		
		// finalize
        closeSocket();
        closeDB();
		return true;
	}
	
	private boolean sendReport(String tripName) {
		openDB();
		/*String reportCommand =
				"{"+
				"'COMMAND':'STOP_TRIP',"+
				"'FUEL' :[ ";*/
		TripData stopTripData = new TripData();
		stopTripData.command = "STOP_TRIP";
		Trip trip = new Trip();
		trip.setOdometerEnd(333234);
		trip.setDateEnd("2011-12-28 23:44:44");
		trip.setStoped(true);

		ArrayList<Fuel> fuelList = new ArrayList<Fuel>();
		Cursor cursor = db.rawQuery("select * from fuel"+tripName, null);
		while(cursor.moveToNext()){
			/*reportCommand += "{"+
						"'date':'"+cursor.getString(0)+"',"+
						"'odometer':"+cursor.getInt(1)+","+
						"'volumeprice':"+cursor.getInt(3)+","+
						"'totalcost':"+cursor.getInt(4)+","+
						"'fuel':"+cursor.getInt(5)+","+
						"'memo':'"+cursor.getString(7)+"'"+						
					"}" + ((cursor.isLast())?"":",");*/
			Fuel fuel = new Fuel();
			fuel.setDate(cursor.getString(0));
			fuel.setOdometer(cursor.getInt(0));
			fuel.setCity("City");
			fuel.setState("State");
			fuel.setLocationName("LocationName");
			fuel.setQuantity(cursor.getInt(5));
			fuel.setPrice(cursor.getInt(3));
			fuel.setCost(cursor.getInt(4));
			fuel.setMemo(cursor.getString(7));
			fuelList.add(fuel);			
		}
		//reportCommand += "], ";
		cursor.close();		
	//	reportCommand += "'COST' :[" ;
		ArrayList<Cost> costList = new ArrayList<Cost>();
		cursor = db.rawQuery("select * from cost" + tripName, null);		
		while (cursor.moveToNext()) {			
			/*reportCommand += "{"+
						"'date':'"+cursor.getString(0)+"',"+
						"'title':'"+cursor.getString(1)+"',"+
						"'odometer':"+cursor.getInt(2)+","+
						"'totalcost':"+cursor.getInt(3)+","+
						"'memo':'"+cursor.getString(4)+"'"+
						"}" + ((cursor.isLast())?"":",");*/
			Cost cost = new Cost();
			cost.setDate(cursor.getString(0));
			cost.setOdometer(cursor.getInt(2));
			cost.setCity("City");
			cost.setState("State");
			cost.setTitle(cursor.getString(1));
			cost.setCost(cursor.getInt(3));
			cost.setMemo(cursor.getString(4));
			costList.add(cost);			
		}
		//reportCommand += "], ";
		cursor.close();
		
		stopTripData.trip = trip;
		stopTripData.fuelList = fuelList;
		stopTripData.costList = costList;
		
		// send start command
		int startResult = writeData(1,2, stopTripData);
		if (startResult != 0) {
			Log.e("Report Sender", "Write data error");
			closeDB();
			return false;
		}
		closeDB();
		return true;
	}
	
	public int sendAllReports(){
		return 0;
	}
	
	AuthData getAuthorizationRequest() {

		AuthData authData = new AuthData();
		authData.command = "AUTHENTICATION";
		// auth table

		Cursor idInfo = db
				.rawQuery(
						"select field1, field2, field3 from auth where field4 = 'vehicle'",
						null);
		idInfo.moveToFirst();
		/*
		 * String companyID = idInfo.getString(0); // company String vehicleID =
		 * idInfo.getString(1); // vehicle String vehiclePin =
		 * idInfo.getString(2); // pin
		 */
		authData.companyId = Integer.parseInt(idInfo.getString(0));
		authData.vehicleId = Integer.parseInt(idInfo.getString(1));
		authData.vehiclePin = Integer.parseInt(idInfo.getString(2));
		idInfo.close();

		idInfo = db.rawQuery(
				"select field2, field3 from auth where field1 = 'driver1'",
				null);
		idInfo.moveToFirst();
		/*
		 * String driver1ID = idInfo.getString(0); // driver 1 String dr1Pin =
		 * idInfo.getString(1); // driver 1 pin
		 */
		authData.firstDriverId = Integer.parseInt(idInfo.getString(0));
		authData.firstDriverPin = Integer.parseInt(idInfo.getString(1));
		idInfo.close();

		idInfo = db.rawQuery(
				"select field2, field3 from auth where field1 = 'driver2'",
				null);
		/*if (idInfo.moveToFirst()) {
			authData.secondDriverId = Integer.parseInt(idInfo.getString(0));
			authData.secondDriverPin = Integer.parseInt(idInfo.getString(1));
		}*/
		authData.secondDriverId = 2;
		authData.secondDriverPin = 0;
		idInfo.close();
		return authData;
	}
	
	
	public boolean sendCurrentReport(ReportSender rs, String tripName, int odomVal, String currDate){
		openDB();
		int sendFlag = Integer.parseInt(dbOpenHelper.getValueByIndex(db, 6));
		closeDB();
		while (sendFlag != -1 & sendFlag != 3) {
			switch (sendFlag) {
			
			// send start command
			case 0:
				if(rs.sendStart(0,0)){
					openDB();
					 db.execSQL("update tripinfo set sendstatus = 1");
					 sendFlag = 1;
				}
				else {
					Toast.makeText(ct, "Some error occured", Toast.LENGTH_SHORT).show();
					sendFlag = -1;
				}
				break;
			
			// send coors
			case 1:
				if (rs.sendCoors(ReportsService.currTripName) == 0) {
					openDB();
					db.execSQL("update tripinfo set sendstatus = 2");
					closeDB();
					sendFlag = 2;
				}
				else  {
					Toast.makeText(ct, "Some error occured", Toast.LENGTH_SHORT).show();
					sendFlag = -1;
				}
				break;
			
			// send 'stop command, report's data
			case 2:
				if (rs.sendReport(PageMainGUI.tripid)) {
					openDB();
					db.execSQL("update tripinfo set sendstatus = 3");
					closeDB();
					sendFlag = 3;
				}
				else {
					Toast.makeText(ct, "Some error occured", Toast.LENGTH_SHORT).show();
					sendFlag = -1;
				}
				break;

			default:
				break;
			}			
		}
		if( sendFlag == -1 ){
			return false;
		}
		else{
			openDB();
			dbOpenHelper.dropTables(db,tripName);
			closeDB();
			return true;
		}
	}
	
	/*
	 * send report from 'report' tab
	 */
	Handler sendReportHandler;
	public boolean sendHistoryReport(int item, ReportSender rs, Handler sendReportHandler){
		this.sendReportHandler = sendReportHandler;
		openDB();
		Cursor idCursor = dbOpenHelper.select(db, "reports");
		idCursor.moveToPosition(item);
		int id =idCursor.getInt(8); 
		idCursor.close();
		Cursor historyRep = db.rawQuery("select sendstatus, report  from reports where id = "+id, null);
		Log.e("count",""+historyRep.getCount());
		historyRep.moveToFirst();		
		int sendFlag = historyRep.getInt(0);
		String repTripName = historyRep.getString(1);
		historyRep.close();
		Message changeTitle = new Message();
		Bundle data = new Bundle();
		data.putInt("cmnd", 3);
		data.putString("title", "Sending report '"+repTripName+"'");
		changeTitle.setData(data);
		sendReportHandler.sendMessage(changeTitle);
		closeDB();
		while (sendFlag != -1 & sendFlag != 3) {
			switch (sendFlag) {			
			// send start command
			case 0:
				if(rs.sendStart(1,id)){
					openDB();
					db.execSQL("update reports set sendstatus = 1 where id = "+ id);
					sendFlag = 1;
				}
				else {
					Toast.makeText(ct, "Some error occured", Toast.LENGTH_SHORT).show();
					sendFlag = -1;
				}
				break;
			
			// send coors
			case 1:
				if (rs.sendCoors(repTripName) == 0) {
					openDB();
					db.execSQL("update reports set sendstatus = 2 where id = "+id);
					closeDB();
					sendFlag = 2;
				}
				else  {
					Toast.makeText(ct, "Some error occured", Toast.LENGTH_SHORT).show();
					sendFlag = -1;
				}
				break;
			
			// send 'stop command, report's data
			case 2:
				if (rs.sendReport(repTripName)) {
					openDB();
					db.execSQL("update reports set sendstatus = 3 where id = "+id);
					closeDB();
					sendFlag = 3;
				}
				else {
					Toast.makeText(ct, "Some error occured", Toast.LENGTH_SHORT).show();
					sendFlag = -1;
				}
				break;

			default:
				break;
			}			
		}
		if( sendFlag == -1 ){
			data.clear();
			data.putInt("cmnd", 4);
			sendReportHandler.sendMessage(changeTitle);
			return false;
		}
		else{
			openDB();
			//dbOpenHelper.dropTables(db,tripName);
			closeDB();
			return true;
		}
	}
	
	
	// FIX 
	// json parser
	// return values
	/*
	 *@param authentification 0 don't need to send auth request, 1 - send auth data first
	 *@param data some request
	 */
	int writeData(int authentification,int dataType, Object data){
		// if bad connection, return error
				if(openSocket() != 0){
					//Log.e("Report Service","Socket error");
					return -1;
				}
		ObjectOutputStream out;
		ObjectInputStream in;
		try {
			if (authentification == 1) {
				// send auth request
				AuthData sendData = getAuthorizationRequest();
				//Log.e("send data",sendData);
				Log.e("send auth", "AUTHORIZATION");
				out = new ObjectOutputStream(client.getOutputStream());
				
				//out.writeObject(sendData);
				out.writeObject((Object)sendData);
				out.flush();
				in = new ObjectInputStream(client.getInputStream());
				String resultText = (String) in.readObject();
				System.out.println("result = " + resultText);
				//parse response
				int parseRes = JSONParser.getResultParse(resultText);
				if(parseRes !=0 ) {
					Log.e("Report Sender Error",ErrorsTypes.getErrorDiscr(parseRes));
					return -1;
				}
			}
			// for dyfferent data types
			switch(dataType){
			//start object 
			case 0:
				Log.e("sendr start","START");
				break;
			case 1:
				Log.e("sendr coords","COORDS");
				break;
			case 2:
				Log.e("sendr stop","STOP");
			    break;
			}
			//Log.e("send data",""/*data*/);
			out = new ObjectOutputStream(client.getOutputStream());
			out.writeObject(data);
			out.flush();
			in = new ObjectInputStream(client.getInputStream());
			String resultText = (String) in.readObject();
			System.out.println("result = " + resultText);
			//parse response
			int parseRes = JSONParser.getResultParse(resultText);
			if (parseRes != 0) {
				Log.e("Report Sender Error",
						ErrorsTypes.getErrorDiscr(parseRes));
				return -1;
			}
		} catch (IOException e) {
			// e.printStackTrace();
			Log.e("Report Sender", ErrorsTypes.getErrorDiscr(22));
			return -1;
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
			Log.e("Report Sender", ErrorsTypes.getErrorDiscr(22));
			return -1;
		}
		return 0;
	}
	
	int openSocket(){
		try {
			client = new Socket(HOST, PORT);
			client.setSoTimeout(30000);
		} catch (UnknownHostException e) {			
			//e.printStackTrace();
			Log.e("Report Sender",ErrorsTypes.getErrorDiscr(21)+"");
			return 21;
		} catch (IOException e) {			
			//e.printStackTrace();
			Log.e("Report Sender",ErrorsTypes.getErrorDiscr(22)+"");
			return 22;
		}
		return 0;		
	}
	
	void closeSocket(){
		if(client != null){
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("ERROR","SOCKET CLOSE");
			}
		}
	}
	
	void openDB() {
		dbOpenHelper = new DBConnector(ct);
		// open connection
		db = dbOpenHelper.getWritableDatabase();
	}

	void closeDB() {
		// close connection
		if(db!=null)db.close();
		if(dbOpenHelper!=null)dbOpenHelper.close();		
	}
	
}
