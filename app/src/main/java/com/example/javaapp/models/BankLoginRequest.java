package com.example.javaapp.models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankLoginRequest {
    private String bankUsername;
    private String bankPassword;
    private String bankName;
    private String userName;
    private UUID userId;

}