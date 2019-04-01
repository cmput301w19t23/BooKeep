package com.example.bookeep;

import java.io.Serializable;

/**
 * creates a request object
 * @author Nafee Khan, Nolan Brost, Jeff Kirker, Dusan Krstic, Hugh Bagan, Kyle Fujishige
 * @see User
 * @see Book
 * @version 1.0.1
 */
public class Request implements Serializable {
    private String requesterId;
    private String bookId;
    private String requesterName;
    private String requesterUsername;

    public Request(){

    }

    /**
     * creates a book request
     * @param bookId string of id of book
     * @param requesterId string of id of requester
     */
    public Request(String bookId, String requesterId){
        this.bookId = bookId;
        this.requesterId = requesterId;
    }

    /**
     * gets the requesters id
     * @return requester id string
     */
    public String getRequesterId(){
        return this.requesterId;
    }

    /**
     * gets the book id
     * @return string of book id
     * @see Book
     */
    public String getBookId(){
        return this.bookId;
    }

    /**
     * set requester id
     * @param requesterId string of requester id
     */
    public void setRequesterId(String requesterId){
        this.requesterId = requesterId;
    }

    /**
     * sets book id
     * @param bookId new book id string
     */
    public void setBookId(String bookId){
        this.bookId = bookId;
    }
}
