package com.example.javaapp.models;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private UUID id;
    private String username;
    private String password;
    private boolean enabled;
    private String email;
    private String firstName;
    private String lastName;
    private String panNumber;
    private String state;
    private String lastDigit;
    private String commodity;

}
