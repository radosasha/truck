package com.gwttest.client.mapping;

import java.io.Serializable;

public class Coordinate implements Serializable {

    private static final long serialVersionUID = -5285392170314346050L;

    private int id;
    private int userId;
    private int vehicle_id;
    private int tripId;
    private double coorX;
    private double coorY;
    private String date;


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
     * @return the vehicle_id
     */
    public int getVehicle_id() {
            return vehicle_id;
    }


    /**
     * @param vehicle_id
     *            the vehicle_id to set
     */
    public void setVehicle_id(int vehicle_id) {
            this.vehicle_id = vehicle_id;
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
     * @return the coorX
     */
    public double getCoorX() {
            return coorX;
    }


    /**
     * @param coorX
     *            the coorX to set
     */
    public void setCoorX(double coorX) {
            this.coorX = coorX;
    }


    /**
     * @return the coorY
     */
    public double getCoorY() {
            return coorY;
    }


    /**
     * @param coorY
     *            the coorY to set
     */
    public void setCoorY(double coorY) {
            this.coorY = coorY;
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
}