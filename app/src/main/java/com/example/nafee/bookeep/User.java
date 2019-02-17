package com.example.nafee.bookeep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class User implements Serializable {

    private String firstname;
    private String lastname;
    private ArrayList<Book> borrowed;
    private ArrayList<Book> owned;
    private Address address;
    private PhoneNumber phoneNumber;
    private String email;
    private UUID userId;

    public User(String email, String firstname, String lastname){

        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userId = UUID.randomUUID();

    }

    public User(){
        this.userId = UUID.randomUUID();
    }

    public UUID getUserId() {
        return userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public ArrayList<Book> getBorrowed() {
        return borrowed;
    }

    public void addToBorrowed(Book book){
        this.borrowed.add(book);
    }

    public ArrayList<Book> getOwned() {
        return owned;
    }

    public void addToOwned(Book book){
        this.owned.add(book);
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
