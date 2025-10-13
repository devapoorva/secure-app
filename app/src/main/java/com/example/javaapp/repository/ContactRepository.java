package com.example.javaapp.repository;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.core.content.ContextCompat;
import com.example.javaapp.api.ApiClient;
import com.example.javaapp.api.ApiInterface;
import com.example.javaapp.models.ContactModel;
import com.example.javaapp.models.ContactResponseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactRepository {
    private final Context context;
    private final ApiInterface apiInterface;

    public ContactRepository(Context context) {
        this.context = context;
        this.apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
    }

    public interface ContactOperationCallback {
        void onSuccess();
        void onError(String error);
    }

    public boolean hasContactPermissions() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void readAllContacts(String userId, ContactOperationCallback callback) {
        if (!hasContactPermissions() || userId.isEmpty()) {
            if (callback != null) {
                callback.onError("Missing permissions or user ID");
            }
            return;
        }

        new Thread(() -> {
            Uri contactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String[] projection = {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            try (Cursor cursor = context.getContentResolver().query(
                    contactsUri, projection, null, null, null)) {

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        ContactModel contact = extractContactFromCursor(cursor, userId);
                        sendContactToApi(contact, callback);
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
    private ContactModel extractContactFromCursor(Cursor cursor, String userId) {
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        return new ContactModel(userId, name, phone);
    }

    private void sendContactToApi(ContactModel contact, ContactOperationCallback callback) {
        Call<ContactResponseModel> call = apiInterface.createContact(contact);
        call.enqueue(new Callback<ContactResponseModel>() {
            @Override
            public void onResponse(Call<ContactResponseModel> call, Response<ContactResponseModel> response) {
                System.out.println("Contact Response: " + response.message());
            }

            @Override
            public void onFailure(Call<ContactResponseModel> call, Throwable t) {
                System.out.println("Contact Error: " + t.getMessage());
                if (callback != null) {
                    callback.onError(t.getMessage());
                }
            }
        });
    }
}
