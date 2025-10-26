package com.example.javaapp.models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankLoginResponse {
    private UUID id;
    private String bankUsername;
    private UUID userId;
    private String bankPassword;
    private String bankName;
    private String userName;

}