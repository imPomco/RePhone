package com.example.rephone.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rephone.R;
import com.example.rephone.Thread.DBThread;

public class StartActivity extends AppCompatActivity {
    SharedPreferences isNewUser, savedToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (checkNewUser()) {
            Intent intent = new Intent(StartActivity.this, IntroActivity.class);
            startActivity(intent);
        }
        else if (isEnteredMainActivity() && checkSavedToken()) {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else if (checkSavedToken()) {
            Intent intent = new Intent(StartActivity.this, PairingPhoneCompleteActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(StartActivity.this, SelectPhoneActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkNewUser() {
        isNewUser = getSharedPreferences("isNewUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = isNewUser.edit();

        if (isNewUser.getAll().isEmpty()) {
            editor.putBoolean("shouldShowIntro", true);
            editor.apply();
            return true;
        }
        return isNewUser.getBoolean("shouldShowIntro", true);
    }

    private boolean checkSavedToken() {
        savedToken = getSharedPreferences("savedToken", MODE_PRIVATE);
        String token = savedToken.getString("token", "");
        if (!token.isEmpty()) {
            try {
                DBThread db = new DBThread("check_token_validate", token);
                db.start();
                db.join(2000);
                if (db.checkTokenValidate())
                    return true;
                else {
                    Toast.makeText(this, "휴대전화와의 연결이 끊어졌어요. 다시 연결해 주세요.", Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean isEnteredMainActivity() {
        savedToken = getSharedPreferences("savedToken", MODE_PRIVATE);
        return savedToken.getBoolean("isEnteredMainActivity", false);
    }
}