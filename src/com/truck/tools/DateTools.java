package com.truck.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTools {
	static SimpleDateFormat timeFormatter;
	static {
		timeFormatter = (SimpleDateFormat) DateFormat.getDateTimeInstance(
				DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
		timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT-05:00"));
		timeFormatter.applyPattern("yyyy-MM-dd HH:mm:ss");
	}

	public static String getTime() {
		return timeFormatter.format(new Date());
	}
}
