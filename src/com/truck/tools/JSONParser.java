package com.truck.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class JSONParser {
	public static int getResultParse(String msg){
		JSONObject json = (JSONObject) JSONSerializer.toJSON(msg);
		return json.getInt("RESPONCE_CODE");
	}
}
