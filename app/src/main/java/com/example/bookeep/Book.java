package com.example.bookeep;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.bookeep.BookStatus;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Nafee Khan, Nolan Brost, Jeff Kirker, Dusan Krstic, Hugh Bagan, Kyle Fujishige
 * @see BookStatus
 * @version 1.0.1
 */
public class Book implements Serializable {

    //private String authors;
    private ArrayList<String> authors;
    private String title;
    private String description;
    private String ISBN;
    private String bookId;
    private String ownerId;
    private BookStatus status;
    private String currentBorrowerId;
    private ArrayList<String> requesterIds = new ArrayList<String>();
    //private Bitmap bookImage;
    private String bookImageURL;
    private Boolean newRequest;



    /**
     * gets the books image url
     * @return bookImageUrl
     */
    public String getBookImageURL() {
        return bookImageURL;
    }

    /**
     * sets a new bookImageUrl
     * @param bookImageURL String of the image url
     */
    public void setBookImageURL(String bookImageURL) {
        this.bookImageURL = bookImageURL;
    }


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
    }

    /**
     * blank constructor
     */
    public Book(){
        super();
        this.bookId = UUID.randomUUID().toString();
        this.status = BookStatus.AVAILABLE;
    }

    //public Bitmap getBookImage() {
      //  return bookImage;
    //}

    //public void setBookImage(Bitmap bookImage) {
        //this.bookImage = bookImage;
    //}

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

    //public String getAuthors() {
        /*
        String authorsString = null;
        for(String author: authors){
            authorsString = authorsString+author;
        }
        return authorsString;*/
      //  return authors;
    //}

    /**
     * gets authors of books
     * @return An array list of authors of the books
     */
    public ArrayList<String> getAuthors() {
        return authors;
    }

    //public void setAuthor(String authors){//ArrayList<String> authors) {
        //this.authors = authors;
    //}

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
    public void setOwner(String ownerId) {

        this.ownerId = ownerId;

    }

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
    public void removeRequester(String reqId){

        this.requesterIds.remove(reqId);

    }

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

    public void setNewRequest(){
        this.newRequest = true;
    }

    public void clearNewRequest(){
        this.newRequest = false;
    }

    public Boolean getNewRequest(){
        return this.newRequest;
    }


}