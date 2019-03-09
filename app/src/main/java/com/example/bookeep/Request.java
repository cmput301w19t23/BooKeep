package com.example.bookeep;

import java.io.Serializable;

public class Request implements Serializable {
    private String requesterId;
    private String bookId;
    private String requesterName;
    private String requesterUsername;


    public Request(){

    }

    public Request(String bookId, String requesterId){
        this.bookId = bookId;
        this.requesterId = requesterId;
    }

    public String getRequesterId(){
        return this.requesterId;
    }

    public String getBookId(){
        return this.bookId;
    }

    public void setRequesterId(String requesterId){
        this.requesterId = requesterId;
    }

    public void setBookId(String bookId){
        this.bookId = bookId;
    }
}
