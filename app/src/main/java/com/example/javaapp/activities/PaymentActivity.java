package com.example.javaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaapp.R;

public class PaymentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);

        Spinner spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        Button btnPay = findViewById(R.id.btnPay);

        // Set up payment methods
        String[] paymentMethods = {"Net Banking", "Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedMethod = spinnerPaymentMethod.getSelectedItem().toString();
                if (selectedMethod.equals("Net Banking")) {
                    startActivity(new Intent(PaymentActivity.this, NetBankingActivity.class));
                } else if (selectedMethod.equals("Card")) {
                    startActivity(new Intent(PaymentActivity.this, CardActivity.class));
                }
            }
        });
    }
}
