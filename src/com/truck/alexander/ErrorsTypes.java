package com.truck.alexander;

public class ErrorsTypes {
	
	public ErrorsTypes() {
	}
	
	public static String getErrorDiscr(int number){
		switch(number){
		case -1:
			return "not correct JSON structure";
		case -2:
			return "There is not such Driver#1 in DB";
		case -3:
			return "There is no such Driver#2 in DB";
		case -4:
			return "There in no such Company in DB";
		case -5:
			return "There in no such Unit in DB";
		case -6:
			return "Unit pin is wrong";
		case -7:
			return "Driver#1 pin is wrong";
		case -8:
			return "Driver#2 pin is wrong";
		case -9:
			return "Not such command";
		case -10:
			return "Error 10";
		case -11:
			return "Error 11";
		case -12:
			return "12";
		case -13:
			return "13";
		case -14:
			return "14";
		case -15:
			return "15";
		case -16:
			return "16";
		case -17:
			return "17";
		case -18:
			return "18";
		case -19:
			return "19";
		case 20:
			return "20";
		case 21:
			return "Socket Error (internet connection)";
		case 22:
			return "Unknown Host";
		case 23:
			return "Input/Output Error";
		case 24:
			return "";
		case 25:
			return "";
		case 26:
			return "";
		case 27:
			return "";
		case 28:
			return "";
		case 29:
			return "";
		case 30:
			return "";
			
		case 100:
			return "Unknown Error";
		}
		return "Unknown Error";
	}
}
