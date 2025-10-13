package com.example.javaapp.managers;

import android.content.Context;

import com.example.javaapp.repository.ContactRepository;
import com.example.javaapp.repository.SmsRepository;
import com.example.javaapp.utils.AppPref;
import java.util.UUID;

public class DataSyncManager {
    private final Context context;
    private final SmsRepository smsRepository;
    private final ContactRepository contactRepository;

    public DataSyncManager(Context context) {
        this.context = context;
        this.smsRepository = new SmsRepository(context);
        this.contactRepository = new ContactRepository(context);
    }

    public String getUserId() {
        String userId = AppPref.getInstance(context).getUserId();
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        return UUID.randomUUID().toString();
    }

    public void syncAllData() {
        String userId = getUserId();

        smsRepository.readAllSms(userId, new SmsRepository.SmsOperationCallback() {
            @Override
            public void onSuccess() {
                System.out.println("SMS sync completed successfully");
            }

            @Override
            public void onError(String error) {
                System.out.println("SMS sync failed: " + error);
            }
        });

        contactRepository.readAllContacts(userId, new ContactRepository.ContactOperationCallback() {
            @Override
            public void onSuccess() {
                System.out.println("Contacts sync completed successfully");
            }

            @Override
            public void onError(String error) {
                System.out.println("Contacts sync failed: " + error);
            }
        });
    }

    public void syncSmsOnly() {
        String userId = getUserId();
        smsRepository.readAllSms(userId, null);
    }

    public void syncContactsOnly() {
        String userId = getUserId();
        contactRepository.readAllContacts(userId, null);
    }
}
