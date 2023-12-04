package com.example.rephone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rephone.R;

public class SelectPhoneActivity extends AppCompatActivity {
    private Button oldPhone, newPhone;
    private TextView usedBefore;

    private Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_phone);

        oldPhone = findViewById(R.id.intro_button_old_phone);
        newPhone = findViewById(R.id.intro_button_new_phone);

        newPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(SelectPhoneActivity.this, SelectNewPhoneQRActivity.class);
                startActivity(intent);
                //String result = new DBThread().startConnection("select_new_phone");
            }
        });

        oldPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(SelectPhoneActivity.this, SelectOldPhoneQRActivity.class);
                startActivity(intent);
            }
        });
    }
}