package com.gwttest.server.socket.serializable;

import java.io.Serializable;
import java.util.ArrayList;

import com.gwttest.client.mapping.Cost;
import com.gwttest.client.mapping.Fuel;
import com.gwttest.client.mapping.Trip;


@SuppressWarnings("serial")
public class TripData implements Serializable {

	public String command;
	public Trip trip;
	public ArrayList<Fuel> fuelList;
	public ArrayList<Cost> costList;

}
