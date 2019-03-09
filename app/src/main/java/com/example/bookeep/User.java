
package com.example.bookeep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;


public class User implements Serializable {

    private String firstname;
    private String lastname;
    private ArrayList<String> borrowedIds = new ArrayList<String>();
    private ArrayList<String> ownedIds = new ArrayList<String>();
    private Address address;
    private PhoneNumber phoneNumber;
    private String email;
    private String userId;
    private String imageURL;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;


    public User(String email, String firstname, String lastname){
        super();
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userId = UUID.randomUUID().toString();

    }

    public User(String email, String firstname, String lastname, String userId){
        super();
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userId = userId;

    }

    public User(String userName, String email, String firstname, String lastname, String userId){

        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userId = userId;
        this.userName = userName;
    }

    public User(){
        //this.userId = UUID.randomUUID();
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
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

    public ArrayList<String> getBorrowedIds() {
        return this.borrowedIds;
    }

    public void addToBorrowed(String bookId){
        this.borrowedIds.add(bookId);
    }

    public ArrayList<String> getOwnedIds() {
        return this.ownedIds;
    }

    public void addToOwned(String bookId){
        this.ownedIds.add(bookId);
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