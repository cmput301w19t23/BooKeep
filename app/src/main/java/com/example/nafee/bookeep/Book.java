package com.example.nafee.bookeep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Book implements Serializable {

    private String author;
    private String title;
    private String description;
    private Integer ISBN;
    private UUID bookId;
    private User owner;
    private BookStatus status;
    private User currentBorrower;
    private ArrayList<User> requests;

    public Book(Integer ISBN, User owner){
        this.ISBN = ISBN;
        this.owner = owner;
        this.bookId = UUID.randomUUID();
        this.status = BookStatus.AVAILABLE;
    }

    public Book(){
        this.bookId = UUID.randomUUID();
        this.status = BookStatus.AVAILABLE;
    }

    public ArrayList<User> getRequests(){
        return this.requests;
    }

    public void addRequest(User requester){

        if(this.status == BookStatus.AVAILABLE || this.status == BookStatus.REQUESTED) {
            this.requests.add(requester);
        }

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public UUID getBookId() {
        return bookId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public User getCurrentBorrower() {
        return currentBorrower;
    }

    public void setCurrentBorrower(User currentBorrower) {
        this.currentBorrower = currentBorrower;
    }



}
