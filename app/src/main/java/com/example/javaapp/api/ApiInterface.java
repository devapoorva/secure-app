package com.example.javaapp.api;

import com.example.javaapp.models.ApiResponse;
import com.example.javaapp.models.LoginRequest;
import com.example.javaapp.models.LoginResponse;
import com.example.javaapp.models.SmsModel;
import com.example.javaapp.models.SmsResponseModel;
import com.example.javaapp.models.ContactModel;
import com.example.javaapp.models.ContactResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("api/v1/sms")
    Call<SmsResponseModel> createSms(@Body SmsModel user);

    @POST("api/v1/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("api/v1/contact")
    Call<ContactResponseModel> createContact(@Body ContactModel contact);

}
