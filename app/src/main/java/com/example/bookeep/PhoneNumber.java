package com.example.bookeep;

import java.io.Serializable;

/**
 * Creates a phone number object
 * @author Nafee Khan, Nolan Brost, Jeff Kirker, Dusan Krstic, Hugh Bagan, Kyle Fujishige
 * @version 1.0.1
 */
public class PhoneNumber implements Serializable {

    private String area;
    private String exchange;
    private String extension;

    /**
     * creates the phone number object
     * @param area string of area
     * @param exchange string of exchange
     * @param extension string of extension
     */
    public PhoneNumber(String area, String exchange, String extension){
        this.area = area;
        this.exchange = exchange;
        this.extension = extension;
    }

    /**
     * empty constructor
     */
    public PhoneNumber(){

    }

    /**
     * creates a phone number from a single string
     * @param phoneNumberString phone number string
     */
    public PhoneNumber(String phoneNumberString){
        this.area = phoneNumberString.substring(0,3);
        this.exchange = phoneNumberString.substring(3,6);
        this.extension = phoneNumberString.substring(6);
    }

    /**
     * returns string of phone number
     * @return phonenumber string
     */
    public String toString() {
        return area + "-" + exchange + "-" + extension;
    }

    /**
     * get area
     * @return area string
     */
    public String getArea() {
        return area;
    }

    /**
     * sets area
     * @param area area string
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * gets exchange
     * @return exchange string
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * sets exchange
     * @param exchange exchange string
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    /**
     * gets the extension
     * @return extension string
     */
    public String getExtension() {
        return extension;
    }

    /**
     * sets the extension string
     * @param extension string of extension
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

}
