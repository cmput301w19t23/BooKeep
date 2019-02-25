package com.example.bookeep;

import java.io.Serializable;

public class PhoneNumber implements Serializable {

    private Integer area;
    private Integer exchange;
    private Integer extension;

    public PhoneNumber(Integer area, Integer exchange, Integer extension){
        this.area = area;
        this.exchange = exchange;
        this.extension = extension;
    }

    public String toString() {
        return getArea().toString()+"-"+getExchange().toString()+"-"+getExtension().toString();
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public Integer getExchange() {
        return exchange;
    }

    public void setExchange(Integer exchange) {
        this.exchange = exchange;
    }

    public Integer getExtension() {
        return extension;
    }

    public void setExtension(Integer extension) {
        this.extension = extension;
    }


}
