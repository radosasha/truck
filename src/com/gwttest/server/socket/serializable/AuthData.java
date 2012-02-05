package com.gwttest.server.socket.serializable;

import java.io.Serializable;


@SuppressWarnings("serial")
public class AuthData implements Serializable {

	public AuthData() {
	}
	
	public String command;
	public int companyId;
	public int vehicleId;
	public int vehiclePin;
	public int firstDriverId;
	public int firstDriverPin;
	public int secondDriverId;
	public int secondDriverPin;
}
