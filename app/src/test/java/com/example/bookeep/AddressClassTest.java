package com.example.bookeep;

import org.junit.Test;
import static org.junit.Assert.*;

public class AddressClassTest {

    //Tests the constructor when not given arguments
    @Test
    public void constructorNoArgumentTest(){
        Address address = new Address();
        assertEquals("", address.getCity());
        assertEquals("", address.getProvince());
        assertEquals("", address.getStreetAddress());
        assertEquals("", address.getZipCode());
    }

    //Tests the constructor when given arguments
    @Test
    public void constructorTest(){
        String city = "Edmonton";
        String province = "Alberta";
        String street = "50st";
        String zip = "T0G 2A2";
        Address address = new Address(street,city,province,zip);
        assertEquals(city, address.getCity());
        assertEquals(province, address.getProvince());
        assertEquals(street, address.getStreetAddress());
        assertEquals(zip, address.getZipCode());
    }

    // Tests the toString method of address
    @Test
    public void toStringTest(){
        String city = "Edmonton";
        String province = "Alberta";
        String street = "50st";
        String zip = "T0G 2A2";
        String testString = street + ", "  + city + ", " + province + ", " + zip + ", ";
        Address address = new Address(street, city, province, zip);
        assertEquals(testString, address.toString());
    }

    //Tests the getter and setter methods
    @Test
    public void getterSetterTest(){
        Address address = new Address();
        String city = "Edmonton";
        String province = "Alberta";
        String street = "50st";
        String zip = "T0G 2A2";

        assertFalse(address.getCity()== city);
        address.setCity(city);
        assertEquals(city, address.getCity());//tests the get/setCity methods

        assertFalse(province==address.getProvince());
        address.setProvince(province);
        assertEquals(province, address.getProvince());//tests the get/setCity methods

        assertFalse(street == address.getStreetAddress());
        address.setStreetAddress(street);
        assertEquals(street, address.getStreetAddress());//tests the get/setStreetAddress methods

        assertFalse(zip == address.getZipCode());
        address.setZipCode(zip);
        assertEquals(zip, address.getZipCode());//tests the get/setZipCode methods
    }
}
