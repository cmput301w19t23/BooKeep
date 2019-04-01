package com.example.bookeep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This class holds all of the necessary data that defines a Book in BooKeep.
 * @author Nafee Khan, Nolan Brost, Jeff Kirker, Dusan Krstic, Hugh Bagan, Kyle Fujishige
 * @see BookStatus
 * @version 1.0.1
 */
public class Book implements Serializable {

    private ArrayList<String> authors;
    private String title;
    private String description;
    private String ISBN;
    private String bookId;
    private String ownerId;
    private BookStatus status;
    private String currentBorrowerId;
    private ArrayList<String> requesterIds = new ArrayList<String>();
    private String bookImageURL;
    private Boolean newRequest;
    private Boolean newAccepted;
    private String borrowLocation;
    private String returnLocation;
    private String calendarDate;
    private boolean inTransaction;
    private String authorsString;

    /**
     * Creates a book object
     * @param ISBN String of the isbn
     * @param ownerId String of the owner id
     */
    public Book(String ISBN, String ownerId){
        super();
        this.ISBN = ISBN;
        this.ownerId = ownerId;
        this.bookId = UUID.randomUUID().toString();
        this.status = BookStatus.AVAILABLE;
        this.newRequest = false;
        this.newAccepted = false;
        this.inTransaction = false;

    }

    /**
     * blank constructor
     */
    public Book(){
        super();
        this.bookId = UUID.randomUUID().toString();
        this.status = BookStatus.AVAILABLE;
        this.newRequest = false;
        this.newAccepted = false;
        this.inTransaction = false;
    }

    /**
     * gets the requester ids
     * @return an arraylist of the ids of all users who have requested the book
     */
    public ArrayList<String> getRequesterIds(){
        return this.requesterIds;
    }

    /**
     * adds a new requester
     * @param requesterId A string of the requester to be added to requester ids
     */
    public void addRequest(String requesterId){
        if(this.status == BookStatus.AVAILABLE || this.status == BookStatus.REQUESTED) {
            this.requesterIds.add(requesterId);
        }
    }

    /**
     * gets authors of books
     * @return An array list of authors of the books
     */
    public ArrayList<String> getAuthors() { return authors; }

    /**
     * sets new authors
     * @param authors An array list of authors
     */
    public void setAuthor(ArrayList<String> authors) {
        this.authors = authors;
    }

    /**
     * gets the title
     * @return title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * sets new title
     * @param title String of title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * gets the description
     * @return a string of the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * sets a new description
     * @param description string of description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * gets isbn
     * @return string of the isbn
     */
    public String getISBN() {
        return ISBN;
    }

    /**
     * sets new isbn
     * @param ISBN string of the isbn
     */
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    /**
     * gets the books id
     * @return string of book id
     */
    public String getBookId() {
        return bookId;
    }

    /**
     * sets new book id
     * @param bookId string of book id
     */
    public void setBookId(String bookId){
        this.bookId = bookId;
    }

    /**
     * gets owner
     * @return owner string
     */
    public String getOwner() {
        return ownerId;
    }

    /**
     * sets new owner
     * @param ownerId string of owner id
     */
    public void setOwner(String ownerId) { this.ownerId = ownerId; }

    /**
     * gets the books status
     * @return bookStatus of the books status
     * @see BookStatus
     */
    public BookStatus getStatus() {
        return status;
    }

    /**
     * sets new book status
     * @param status BookStatus
     * @see BookStatus
     */
    public void setStatus(BookStatus status) {
        this.status = status;
    }

    /**
     * gets current owners id
     * @return string of the current owners id
     */
    public String getCurrentBorrowerId() {
        return currentBorrowerId;
    }

    /**
     * sets new current borrower
     * @param currentBorrowerId string of new borrowers id
     */
    public void setCurrentBorrower(String currentBorrowerId) {
        this.currentBorrowerId = currentBorrowerId;
    }

    /**
     * clears requests
     */
    public void clearRequesters (){
        this.requesterIds.clear();
    }

    /**
     * removes a requester by their id
     * @param reqId string of the user id to be removed from requesters
     */
    public void removeRequester(String reqId){ this.requesterIds.remove(reqId); }

    /**
     * gets a string of all authors
     * @return string of all authors
     */
    public String getAuthorsString(){
        String authorsString = "";
        for(String author: this.authors){

            authorsString = authorsString + author;

        }
        return authorsString;
    }

    public void setAuthorsString(String string) {
        this.authorsString = string;
    }

    /**
     * Sets the new request status to true.
     */
    public void setNewRequest(){
        this.newRequest = true;
    }

    /**
     * Sets the new request status to false.
     */
    public void clearNewRequest(){
        this.newRequest = false;
    }

    /**
     * Gets the status of the new request.
     * @return Whether or not there is a new request.
     */
    public Boolean getNewRequest(){
        return this.newRequest;
    }

    /**
     * Sets the newly accepted status to true.
     */
    public void setNewAccepted(){
        this.newAccepted = true;
    }

    /**
     * Sets the newly accepted status to false.
     */
    public void clearNewAccepted(){
        this.newAccepted = false;
    }

    /**
     * Gets the newly accepted status.
     * @return Whether or not the book has been recently accepted.
     */
    public Boolean getNewAccepted(){
        return this.newAccepted;
    }

    /**
     * Gets the agreed upon on location of the accepted borrow.
     * @return The location of the exchange.
     */
    public String getBorrowLocation() {
        return borrowLocation;
    }

    /**
     * Sets the location of the accepted borrow.
     * @param borrowLocation The location to borrow at.
     */
    public void setBorrowLocation(String borrowLocation) {
        this.borrowLocation = borrowLocation;
    }

    /**
     * Gets the location that the book will be returned at.
     * @return The location to be returned at.
     */
    public String getReturnLocation() { return returnLocation; }

    /**
     * Sets the lcoation that the book will be returned at.
     * @param returnLocation The location for the book to be.
     */
    public void setReturnLocation(String returnLocation) { this.returnLocation = returnLocation; }

    /**
     * Gets the calendar date.
     * @return
     */
    public String getCalendarDate() { return calendarDate; }

    /**
     * Sets the calendar date.
     * @param calendarDate The new date to set to.
     */
    public void setCalendarDate(String calendarDate) { this.calendarDate = calendarDate; }

    /**
     * gets the books image url
     * @return bookImageUrl
     */
    public String getBookImageURL() { return bookImageURL; }

    /**
     * sets a new bookImageUrl
     * @param bookImageURL String of the image url
     */
    public void setBookImageURL(String bookImageURL) { this.bookImageURL = bookImageURL; }

    /**
     * Sets the transaction status to true.
     */
    public void startTransaction(){ this.inTransaction = true; }

    /**
     * Sets the transaction status to false.
     */
    public void endTransaction(){ this.inTransaction = false; }

    /**
     * Gets the status of the transaction.
     * @return Whether or not a transaction is occurring.
     */
    public boolean isInTransaction(){ return this.inTransaction; }
}