
package com.example.bookeep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * The users of the app will all be associated with a user object. Used to keep track of owners,borrowers
 * of the books in the app.
 * @author Nafee Khan, Nolan Brost, Jeff Kirker, Dusan Krstic, Hugh Bagan, Kyle Fujishige
 * @see PhoneNumber
 * @see Address
 * @see Book
 * @version 1.0.1
 */
public class User implements Serializable {

    private String userName;
    private String firstname;
    private String lastname;
    private ArrayList<String> borrowedIds = new ArrayList<>();
    private ArrayList<String> ownedIds = new ArrayList<>();
    private Address address;
    private PhoneNumber phoneNumber;
    private String email;
    private String userId;
    private String imageURL;





    /**
     * creates a user with a random uuid
     * @param userName username string
     * @param email email string
     * @param firstname firstname string
     * @param lastname lastname string
     */
    public User(String userName, String email, String firstname, String lastname){

        this.userName = userName;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userId = UUID.randomUUID().toString();

    }

    /**
     * creates a user with a specified uuid
     * @param userName username string
     * @param email email string
     * @param firstname firstname string
     * @param lastname lastname string
     * @param userId uuid
     */
    public User(String userName, String email, String firstname, String lastname, String userId){

        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userId = userId;
        this.userName = userName;
    }

    /**
     * empty constructor
     */
    public User(){
        //this.userId = UUID.randomUUID();
    }

    /**
     * gets username
     * @return username string
     */
    public String getUserName(){
        return userName;
    }

    /**
     * sets the username
     * @param userName new username string
     */
    public void setUserName(String userName){
        this.userName = userName;
    }

    /**
     * gets the image url
     * @return image url string
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * sets the image url
     * @param imageURL new url string
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * gets the user id
     * @return the userid string
     */
    public String getUserId() {
        return userId;
    }

    /**
     * sets new userid
     * @param userId new user id string
     */
    public void setUserId(String userId){
        this.userId = userId;
    }

    /**
     * gets users first name
     * @return first name string
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * sets users first name
     * @param firstname string of firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * gets users last name
     * @return lastname string
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * sets new last name
     * @param lastname string of new lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * gets borrowed  book ids
     * @return array list of book id strings
     */
    public ArrayList<String> getBorrowedIds() {
        return this.borrowedIds;
    }

    /**
     * adds a book id to borrowed
     * @param bookId string of book id
     */
    public void addToBorrowed(String bookId){
        this.borrowedIds.add(bookId);
    }

    /**
     * removes book id from borrowed
     * @param bookId id string to be removed
     */
    public void removeFromOwned(String bookId){

        for(int i = 0; i < ownedIds.size(); i++){
            if(ownedIds.get(i).equals(bookId)){

                ownedIds.remove(i);
            }
        }

    }
    public void removeFromBorrowed(String bookId){

        for(int i = 0; i < borrowedIds.size(); i++){
            if(borrowedIds.get(i).equals(bookId)){

                borrowedIds.remove(i);
            }
        }

    }

    /**
     * gets owned book ids
     * @return array list of book id strings
     */
    public ArrayList<String> getOwnedIds() {
        return this.ownedIds;
    }

    /**
     * adds book id to owned
     * @param bookId string id of book to be added
     */
    public void addToOwned(String bookId){
        this.ownedIds.add(bookId);
    }

    /**
     * gets the users address
     * @return address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * sets new address
     * @param address new address
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * gets phone number
     * @return phone number
     */
    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * sets phone numner
     * @param phoneNumber new phone number
     */
    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * gets the users email
     * @return users email
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets users email
     * @param email new email
     */
    public void setEmail(String email) {
        this.email = email;
    }


}