package com.example.javaapp.models;

public class SmsResponseModel {
    private boolean success;
    private String message;
    private SmsModel data;

    public SmsResponseModel(boolean success, String message, SmsModel data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public SmsResponseModel() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SmsModel getData() {
        return data;
    }

    public void setData(SmsModel data) {
        this.data = data;
    }
}
