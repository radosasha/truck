package com.gwttest.client.mapping;

import java.io.Serializable;


public class Cost implements Serializable {

	private static final long serialVersionUID = -5021762460096064551L;
	
	private int id;
	private int tripId;
	private String date;
	private int odometer;
	private String city;
	private String state;
	private String title;
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
	 * @return the date
	 */
	public String getDate() {
		return date;
	}


	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
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
		return city;
	}


	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}


	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}


	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}


	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}


	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
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
}
