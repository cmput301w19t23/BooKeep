package com.example.bookeep;

import java.io.Serializable;

public class PhoneNumber implements Serializable {

    private String area;
    private String exchange;
    private String extension;

    public PhoneNumber(String area, String exchange, String extension){
        this.area = area;
        this.exchange = exchange;
        this.extension = extension;
    }

    public PhoneNumber(){
        
    }

    public String toString() {
        //if area is 001 handle: do later
        //return getArea().toString()+"-"+getExchange().toString()+"-"+getExtension().toString();
        return area + exchange + extension;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }


}
