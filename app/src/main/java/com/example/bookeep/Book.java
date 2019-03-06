package com.example.bookeep;

import com.example.bookeep.BookStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Book implements Serializable {

    private String authors;
    private String title;
    private String description;
    private Integer ISBN;
    private String bookId;
    private String ownerId;
    private BookStatus status;
    private String currentBorrowerId;
    private ArrayList<String> requesterIds = new ArrayList<String>();
    private String imageURL;

    public Book(Integer ISBN, String ownerId){
        this.ISBN = ISBN;
        this.ownerId = ownerId;
        this.bookId = UUID.randomUUID().toString();
        this.status = BookStatus.AVAILABLE;
    }

    public Book(){
        this.bookId = UUID.randomUUID().toString();
        this.status = BookStatus.AVAILABLE;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public ArrayList<String> getRequesterIds(){
        return this.requesterIds;
    }

    public void addRequest(String requesterId){

        if(this.status == BookStatus.AVAILABLE || this.status == BookStatus.REQUESTED) {
            this.requesterIds.add(requesterId);
        }

    }

    public String getAuthors() {
        /*
        String authorsString = null;
        for(String author: authors){
            authorsString = authorsString+author;
        }
        return authorsString;*/
        return authors;
    }

    public void setAuthor(String authors){//ArrayList<String> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getISBN() {
        return ISBN;
    }

    public void setISBN(Integer ISBN) {
        this.ISBN = ISBN;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId){
        this.bookId = bookId;
    }

    public String getOwner() {
        return ownerId;
    }

    public void setOwner(String ownerId) {

        this.ownerId = ownerId;

    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public String getCurrentBorrowerId() {
        return currentBorrowerId;
    }

    public void setCurrentBorrower(String currentBorrowerId) {
        this.currentBorrowerId = currentBorrowerId;
    }

    public void clearRequesters (){
        this.requesterIds.clear();
    }

    public void removeRequester(String reqId){

        this.requesterIds.remove(reqId);

    }



}