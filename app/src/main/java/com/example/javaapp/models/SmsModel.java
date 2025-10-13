package com.example.javaapp.models;

import java.util.Date;

public class SmsModel {
    private String userId;
    private String sms;
    private String date;
    private String address;

    public SmsModel(String userId, String sms, String date, String address) {
        this.userId = userId;
        this.sms = sms;
        this.date = date;
        this.address = address;
    }

    public SmsModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
