package com.example.bookeep;

import java.io.Serializable;

/**
 * Represents a User's physical Address
 * @author Nafee Khan, Nolan Brost, Jeff Kirker, Dusan Krstic, Hugh Bagan, Kyle Fujishige
 * @see User
 * @version 1.0.1
 *
 */
public class Address implements Serializable {

    private String streetAddress;
    private String city;
    private String province;
    private String zipCode;

    /**
     * Creates an address object with streetaddress, city, province and zip code given as strings
     * @param streetAddress String
     * @param city String
     * @param province String
     * @param zipCode String
     */
    public Address(String streetAddress, String city, String province, String zipCode){
        this.streetAddress = streetAddress;
        this.city = city;
        this.province = province;
        this.zipCode = zipCode;
    }

    /**
     * Blank constructor
     */
    public Address(){
        this.streetAddress = "";
        this.city = "";
        this.province = "";
        this.zipCode = "";
    }

    /**
     * Returns a string of the address
     * @return AddressString
     */
    public String toString(){
        return streetAddress + ", " + city + ", " + province + ", " + zipCode + ", ";
    }

    /**
     * returns street address as a string
     * @return streetAddress
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * Sets the street address to a new address
     * @param streetAddress String
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * gets the city
     * @return cityString
     */
    public String getCity() {
        return city;
    }

    /**
     * sets thet city to a new city
     * @param city String
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * returns the province String
     * @return provinceString
     */
    public String getProvince() {
        return province;
    }

    /**
     * sets the province to a new province
     * @param province String
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * returns the zipcode string
     * @return zipCodeString
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets a new zipcode
     * @param zipCode String
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

}
