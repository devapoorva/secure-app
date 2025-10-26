package com.example.javaapp.models;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String name;
    private String firmName;
    private String panNumber;
    private String gst;
    private String email;
}