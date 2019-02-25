package com.example.bookeep;


import org.junit.Test;
import static org.junit.Assert.*;

public class PhoneNumberClassTest {

    //Tests the constructor
    @Test
    public void constructorTest(){
        int area = 123;
        int exchange = 456;
        int extension = 7890;
        PhoneNumber phoneNumber = new PhoneNumber(area, exchange, extension);

        assertTrue(area == phoneNumber.getArea());
        assertTrue(exchange == phoneNumber.getExchange());
        assertTrue(extension == phoneNumber.getExtension());
    }

    //Tests the getter and setters
    @Test
    public void getterSetterTest(){
        int area = 123;
        int exchange = 456;
        int extension = 7890;
        PhoneNumber phoneNumber = new PhoneNumber(area, exchange, extension);

        phoneNumber.setArea(321);
        assertTrue(321 == phoneNumber.getArea());//tests get/set area

        phoneNumber.setExchange(654);
        assertTrue(654 == phoneNumber.getExchange());//tests get/set exchange

        phoneNumber.setExtension(987);
        assertTrue(987 == phoneNumber.getExtension());//test get/set extension
    }

    //tests toString method
    @Test
    public void toStringTest(){
        PhoneNumber phoneNumber = new PhoneNumber(0,0,0);
        assertEquals("000-000-0000", phoneNumber.toString());
    }
}
