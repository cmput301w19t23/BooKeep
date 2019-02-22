package com.example.bookeep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Book implements Serializable {

    private ArrayList<String> authors;
    private String title;
    private String description;
    private Integer ISBN;
    private UUID bookId;
    private UUID ownerId;
    private BookStatus status;
    private UUID currentBorrowerId;
    private ArrayList<UUID> requesterIds = new ArrayList<UUID>();
    private String imageURL;

    public Book(Integer ISBN, UUID ownerId){
        this.ISBN = ISBN;
        this.ownerId = ownerId;
        this.bookId = UUID.randomUUID();
        this.status = BookStatus.AVAILABLE;
    }

    public Book(){
        this.bookId = UUID.randomUUID();
        this.status = BookStatus.AVAILABLE;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public ArrayList<UUID> getRequesterIds(){
        return this.requesterIds;
    }

    public void addRequest(UUID requesterId){

        if(this.status == BookStatus.AVAILABLE || this.status == BookStatus.REQUESTED) {
            this.requesterIds.add(requesterId);
        }

    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthor(ArrayList<String> authors) {
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

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId){
        this.bookId = bookId;
    }

    public UUID getOwner() {
        return ownerId;
    }

    public void setOwner(UUID ownerId) {

        this.ownerId = ownerId;

    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public UUID getCurrentBorrowerId() {
        return currentBorrowerId;
    }

    public void setCurrentBorrower(UUID currentBorrowerId) {
        this.currentBorrowerId = currentBorrowerId;
    }



}
