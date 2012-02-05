package com.gwttest.client.mapping;

import java.io.Serializable;


public class Trip implements Serializable {

	private static final long serialVersionUID = 5761289573726962056L;

	private int tripId;
	private int userId;
	private int vehicleId;
	private String title;
	private String destination;
	private int odometerStart;
	private int odometerEnd;
	private String dateStart;
	private String dateEnd;
	private boolean isStoped;
	private String memo;


	/**
	 * Default Constructor. The Default Constructor's explicit declaration is
	 * required for a serializable class.
	 */
	public Trip() {
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


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}


	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}


	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}


	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}


	/**
	 * @return the odometerStart
	 */
	public int getOdometerStart() {
		return odometerStart;
	}


	/**
	 * @param odometerStart
	 *            the odometerStart to set
	 */
	public void setOdometerStart(int odometerStart) {
		this.odometerStart = odometerStart;
	}


	/**
	 * @return the odometerEnd
	 */
	public int getOdometerEnd() {
		return odometerEnd;
	}


	/**
	 * @param odometerEnd
	 *            the odometerEnd to set
	 */
	public void setOdometerEnd(int odometerEnd) {
		this.odometerEnd = odometerEnd;
	}


	/**
	 * @return the dateStart
	 */
	public String getDateStart() {
		return dateStart;
	}


	/**
	 * @param dateStart
	 *            the dateStart to set
	 */
	public void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}


	/**
	 * @return the dateEnd
	 */
	public String getDateEnd() {
		return dateEnd;
	}


	/**
	 * @param dateEnd
	 *            the dateEnd to set
	 */
	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}


	/**
	 * @return the isStoped
	 */
	public boolean isStoped() {
		return isStoped;
	}


	/**
	 * @param isStoped
	 *            the isStoped to set
	 */
	public void setStoped(boolean isStoped) {
		this.isStoped = isStoped;
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
	 * @return the vehicleId
	 */
	public int getVehicleId() {
		return vehicleId;
	}


	/**
	 * @param vehicleId
	 *            the vehicleId to set
	 */
	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}
}
