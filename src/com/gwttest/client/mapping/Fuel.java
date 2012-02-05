package com.gwttest.client.mapping;

import java.io.Serializable;

public class Fuel implements Serializable {

	private static final long serialVersionUID = -5982727646708610166L;

	private int id;
	private int tripId;
	private String date;
	private String time;
	private int odometer;
	private String City;
	private String State;
	private String locationName;
	private int quantity;
	private float price;
	private float cost;
	private String memo;


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the tripId
	 */
	public int getTripId() {
		return tripId;
	}


	/**
	 * @param tripId
	 *            the tripId to set
	 */
	public void setTripId(int tripId) {
		this.tripId = tripId;
	}


	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}


	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}


	/**
	 * @return the odometer
	 */
	public int getOdometer() {
		return odometer;
	}


	/**
	 * @param odometer
	 *            the odometer to set
	 */
	public void setOdometer(int odometer) {
		this.odometer = odometer;
	}


	/**
	 * @return the city
	 */
	public String getCity() {
		return City;
	}


	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		City = city;
	}


	/**
	 * @return the state
	 */
	public String getState() {
		return State;
	}


	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		State = state;
	}


	/**
	 * @return the locationName
	 */
	public String getLocationName() {
		return locationName;
	}


	/**
	 * @param locationName
	 *            the locationName to set
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}


	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}


	/**
	 * @param quantity
	 *            the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}


	/**
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}


	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(float price) {
		this.price = price;
	}


	/**
	 * @return the cost
	 */
	public float getCost() {
		return cost;
	}


	/**
	 * @param cost
	 *            the cost to set
	 */
	public void setCost(float cost) {
		this.cost = cost;
	}


	/**
	 * @return the memo
	 */
	public String getMemo() {
		return memo;
	}


	/**
	 * @param memo
	 *            the memo to set
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}


	
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}


	
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
}
