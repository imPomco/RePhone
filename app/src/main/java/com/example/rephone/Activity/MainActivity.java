package com.example.rephone.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rephone.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView mainMenuNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences savedToken = getSharedPreferences("savedToken", MODE_PRIVATE);
        SharedPreferences.Editor editor = savedToken.edit();
        boolean isOldPhone = savedToken.getBoolean("isOldPhone", true);
        editor.putBoolean("isEnteredMainActivity", true);
        editor.apply();

        mainMenuNavigation = findViewById(R.id.main_menu_navigation);
        if (isOldPhone) {
            mainMenuNavigation.getMenu().removeItem(R.id.main_menu_bottom_new_phone_notification);
        }
        else {
            mainMenuNavigation.getMenu().removeItem(R.id.main_menu_bottom_old_phone_function_setting);
        }
    }
}