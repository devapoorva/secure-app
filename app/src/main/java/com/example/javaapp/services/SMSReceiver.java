package com.example.javaapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.example.javaapp.api.ApiClient;
import com.example.javaapp.api.ApiInterface;
import com.example.javaapp.models.SmsModel;
import com.example.javaapp.models.SmsResponseModel;
import com.example.javaapp.utils.AppPref;
import com.example.javaapp.utils.AppUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            // Retrieve the SMS messages from the intent
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];

            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            // Extract the sender and message body
            String sender = messages[0].getOriginatingAddress();
            String messageBody = messages[0].getMessageBody();
            String date = String.valueOf(messages[0].getTimestampMillis());
            String userId = getUserId(context);
            if(!userId.isEmpty()){
                ApiInterface apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
                Call<SmsResponseModel> call = apiInterface.createSms(new SmsModel(userId,messageBody, AppUtil.stringToLocalDate(date),sender));
                call.enqueue(new Callback<SmsResponseModel>() {
                    @Override
                    public void onResponse(Call<SmsResponseModel> call, Response<SmsResponseModel> response) {
                        System.out.println("Response "+response.message());
                    }

                    @Override
                    public void onFailure(Call<SmsResponseModel> call, Throwable t) {
                        System.out.println("Error "+t.getMessage());
                    }
                });
            }

        }
    }

    private String getUserId(Context context) {
        return AppPref.getInstance(context).getUserId();
    }
}

