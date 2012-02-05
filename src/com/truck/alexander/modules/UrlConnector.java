package com.truck.alexander.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;


import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.gwttest.server.socket.serializable.AuthData;
import com.truck.alexander.db.DBConnector;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log; 



public class UrlConnector {
	// server address
	private final String HOST = "107.21.98.186";
	// server port
	private final int PORT = 15883;
	// socket
	Socket client = null;
	// sending data
	LinkedList coordData;
	Context context;
	// db connectors
	SQLiteDatabase db;
	DBConnector dbOpenHelper;
	// result flag
	int flag = 1;
	// error trace
	String trace = null;
	
	/*
	 * start/stop constructor
	 */
	public UrlConnector(){
		
	}
	
	Handler hnd;
	String infoCompany;
	String infoUnit;
	 public UrlConnector(LinkedList cData, Context parent, Cursor cr) {
		coordData = cData;
		context = parent;
		cr.moveToFirst();
		infoCompany = cr.getString(0);
		infoUnit = cr.getString(1);
			connect();
	}
 

	public UrlConnector(LinkedList cData, Context parent, Handler mHandler) {
		coordData = cData;
		context = parent;
		hnd = mHandler;
		connect();
	}
	
	/*
	 * on authorization request
	 */
	// fields for first step
	String company;
	String unitid;
	String pinNumber;
	
	public UrlConnector(String comp, String unit, String pin){
		company = comp;
		unitid = unit;
		pinNumber = pin;
	}

	// fields for second step
	String driver1;
	String driver2;
	String pin1;
	String pin2;

	public UrlConnector(String dr1str, String dr1pinstr, String dr2str,String dr2pinstr, String company, String unit, String unpin) {
		this.company = company;
		unitid = unit;
		driver1 = dr1str;
		driver2 = dr2str;
		pin1 = dr1pinstr;
		pin2 = dr2pinstr;
		pinNumber = unpin;
	}

    int driverCase;
	public UrlConnector(String company, String unit, String pin,
			String string, String string2) {
		this.company = company;
		this.unitid = unit;
		this.pinNumber = pin;
		this.driver1 = string;
		this.pin1 = string2;
	}


	/*
	 * error procession
	 */
	private String connect() {		
		try {
			// data oterator
			Iterator ite = coordData.iterator();
			// send data to server
			send(ite);
			
		} catch (Exception e) {
			setFlag(0);
			e.printStackTrace();
			Log.e("Sending error. There is no internet connection.","");
			StringWriter w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			trace = w.toString();			
		} finally {
			try {
				if (client != null) {
					client.close();
				}
			} catch (IOException e) {
				setFlag(0);
				Log.e("Sending error. Socket error.","");
				e.printStackTrace();
				StringWriter w = new StringWriter();
				e.printStackTrace(new PrintWriter(w));
				trace = w.toString();
			}
		}
		return ""; 
	}
	
	/*
	 * send messages to server by short parts of data, less then 1024byte
	 */
	private void send(Iterator ite) throws IOException{		
		// total coords count, sent to server
		int mTotal = 0;
		// buffer
		byte buf[] = new byte[64 * 1024];
		// i/o streams
		OutputStream os = null;
		InputStream is = null;
		// try connect to server socket
		while (ite.hasNext()) {
			try {
				client = new Socket(HOST, PORT);
				System.out.println("connected to host");
			} catch (UnknownHostException e) {
				setTrace(e);
				e.printStackTrace();
				break;
			} catch (IOException e) {
				setTrace(e);
				e.printStackTrace();
				break;
			}

			try {
				// socket connection timeout
				client.setSoTimeout(10000);
			} catch (SocketException e) {
				setTrace(e);
				e.printStackTrace();
				break;
			}

			try {
				// get i/o streams
				os = client.getOutputStream();
				is = client.getInputStream();
			} catch (IOException e) {
				setTrace(e);
				e.printStackTrace();
				continue;
			}
			// result string
			String resultText = "";
			// Choose coars count
			int count = 0;
			String coars = "{" + 
				"'COMMAND':'SEND_COORS',"+
			    "\"COMPANY_ID\":'"+infoCompany+"'," + 
				"\"VEHICLE_ID\":'"+infoUnit+"',"+ 
				"\"TRIPID\":\"NEW\"," + 
				"\"COORS\": [ ";
				// create message (8 or less coords)
				while(ite.hasNext()){
					String [] row = (String[]) ite.next();
					coars += "{" + "\"X\":\""+row[0]+"\","+
							"\"Y\":\""+row[1]+"\"," + "\"T\":\""+row[2]+"\"" + "}";
				if (ite.hasNext() & count < 8) {
					coars += ", ";
					count++;
				} else
					break;
			}
			/*
			 * coars =
			 * "{\"VEHICLE_ID\":\"9\",\"TRUCKID\":\"666KAMAZ\",\"TRIPID\":\"NEW\",\"COORS\": [ {\"X\":\"44.596707252310985\",\"Y\":\"33.4658862686758\",\"T\":\"2012-01-02 20:54:13\"}, {\"X\":\"44.596558793841865\",\"Y\":\"33.46521026187586\",\"T\":\"2012-01-02 20:54:39\"}]}"
			 * ;
			 */
			Log.e("Length:", coars.getBytes().length + "");
			// end message
			coars += "]" + "}";
				
			Log.e("Send data", coars);
			// send message to server
			os.write(coars.getBytes());
			// receive response from server
			int r = is.read(buf);
			resultText = new String(buf, 0, r);
			System.out.println("resultText=" + resultText);
			// os.close();
			os.close();
			is.close();
			client.close();
			// increase total sent coars
			mTotal += count + 1;
			Message msg = hnd.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("total", mTotal);
			msg.setData(b);
			hnd.sendMessage(msg);
		
			if (resultText.equals("success")) {
				setFlag(1);
				toast("Coordinates have been sent!");
			} else {
				setFlag(2);
			}
		}
	}
	
	/*
	 * show message on display
	 */
	private void toast(String msg){
		//Toast.makeText(context, msg , Toast.LENGTH_SHORT).show();
	}
	
	/*
	 * set result flag
	 */
	private void setFlag(int i){
		flag = i;
	}
	
	/*
	 * @return result flag
	 */
	public int getFlag(){
		return flag;
	}

	/*
	 * @return error trace 
	 */
	public String getTrace() {		
		return trace;
	}
	
	/*
	 * read error trace from buffer
	 * @return error trace
	 */
	public String setTrace(Exception e){
		e.printStackTrace();
		StringWriter w = new StringWriter();
		e.printStackTrace(new PrintWriter(w));
		trace = w.toString();
		//set flag to error
		setFlag(0);
		return trace;
	}


	//send authorization data to server
	//type == 1 - first step, sending company id, unit id, unit pin
	//type == 2 - second steP, sending dr1 id, dr2 id, dr1 pin, dr2 pin
	public int sendData(int type) {	
		
		int connectRes = connectToSocket();
		// if connect socket  error occurred, break this method
		if(connectRes != 0) return connectRes;
		String data = "";
		Object dataObject= null;
		//create request
		switch(type){
		// bind vehicle
		case 1:
		 data = 
		 "{"
			+"'COMMAND':'BIND_VEHICLE',"
			+"'COMPANY_ID':'"+company+"'," 
			+"'VEHICLE_ID':'"+unitid+"'," 
			+"'VEHICLE_PIN':'"+pinNumber+"'" 
			+"}";
		 
		 break;
		case 2:
			// bind drivers
			// receive company + driver info from DB			
			data = "{"
					+"'COMMAND':'BIND_DRIVERS',"
					+"'COMPANY_ID':'"+company+"'," 
					+"'VEHICLE_ID':'"+unitid+"'," 
					+"'VEHICLE_PIN':'"+pinNumber+"'," 
					+"'DRIVERS_ID':["
						+"{"
					      	+"'DRIVER_ID':'"+driver1+"'," 
					      	+"'DRIVER_PIN':'"+pin1+"'"
					    +"}"
					    +((driver2.length()==0 | pin2.length()==0)==true ? "" : (", {"
					    +"'DRIVER_ID':'"+driver2+"'," 
				      	+"'DRIVER_PIN':'"+pin2+"'"
					      	+"}"))
					      	+"] }";
			
			break;
		case 3:
			data = "{"
					+"'COMMAND':'UNBIND_DRIVERS',"
					+"'COMPANY_ID':'"+company+"'," 
					+"'VEHICLE_ID':'"+unitid+"'," 
					+"'VEHICLE_PIN':'"+pinNumber+"'," 
					+"'DRIVERS_ID':["
						+"{"
					      	+"'DRIVER_ID':'"+driver1+"'," 
					      	+"'DRIVER_PIN':'"+pin1+"'"
					    +"}"
					+"] }";
			break;
		}
		Log.e("Send data", data);
		/*OutputStream os = null;
		InputStream is = null;*/
		ObjectOutputStream out;
		ObjectInputStream in; 
		int responseCode = 0;
		try {
			/*os = client.getOutputStream();
			is = client.getInputStream();
			// send message to server
			os.write(data.getBytes());
			// receive response from server
			byte buf[] = new byte[64 * 1024];
			int r = is.read(buf);
			String resultText = new String(buf, 0, r);*/			
			out = new ObjectOutputStream(client.getOutputStream());			
			
			AuthData authData = new AuthData();
			authData.command = "AUTHENTICATION";
			authData.companyId = 8;
			authData.vehicleId = 10;
			authData.vehiclePin = 123456;
			authData.firstDriverId = 1;
			authData.firstDriverPin = 0;
			authData.secondDriverId = 2;
			authData.secondDriverPin = 0;
			dataObject = authData;
			
			
			//out.writeObject(data); 
			out.writeObject(dataObject);  			
			out.flush();
			
			in = new ObjectInputStream(client.getInputStream());
			String resultText = (String)in.readObject();			        
			System.out.println("result = " + resultText);
			
			/*out = new ObjectOutputStream(client.getOutputStream());			
			out.writeObject(data);  
			out.flush();
			
			in = new ObjectInputStream(client.getInputStream());
			resultText = (String)in.readObject();	
			System.out.println("result = " + resultText);*/
			
			try {
				JSONObject json = (JSONObject) JSONSerializer.toJSON(resultText);
				responseCode = json.getInt("RESPONCE_CODE");
			} catch (Exception e) {
				//return 0;
				responseCode = 0;
			}
			in.close();
			out.close();
			/*os.close();
			is.close();*/
			client.close();
		} catch (IOException e) {			
			e.printStackTrace();
			return 100;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// its OK
		return responseCode;
	}
	
	public int connectToSocket(){
		try {
			client = new Socket(HOST, PORT);
			client.setSoTimeout(10000);
		} catch (SocketException e) {			
			e.printStackTrace();
			return 1+20;
		} catch (UnknownHostException e) {			
			e.printStackTrace();
			return 2+20;
		} catch (IOException e) {
			e.printStackTrace();
			return 3+20;
		}
		return 0;
	}
}
