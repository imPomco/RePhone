package com.example.rephone.Activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.rephone.Fragment.MoreFragment;
import com.example.rephone.Fragment.NewPhoneHomeFragment;
import com.example.rephone.Fragment.NewPhoneNotificationFragment;
import com.example.rephone.Fragment.OldPhoneFunctionSettingFragment;
import com.example.rephone.Fragment.OldPhoneHomeFragment;
import com.example.rephone.R;
import com.example.rephone.Thread.DBThread;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    public SharedPreferences savedToken;
    public BottomNavigationView mainMenuNavigation;
    public LinearLayout mainTitleBar;
    private Fragment homeFragment, functionSettingFragment, notificationFragment, moreFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private TextView mainTitleText;

    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        savedToken = getSharedPreferences("savedToken", MODE_PRIVATE);
        SharedPreferences.Editor editor = savedToken.edit();
        boolean isOldPhone = savedToken.getBoolean("isOldPhone", true);
        editor.putBoolean("isEnteredMainActivity", true);
        editor.apply();

        mainMenuNavigation = findViewById(R.id.main_menu_navigation);
        mainTitleBar = findViewById(R.id.main_titlebar_layout);
        mainTitleText = findViewById(R.id.main_title_text);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        moreFragment = new MoreFragment();
        fragmentTransaction.add(R.id.main_framelayout, moreFragment);
        fragmentTransaction.hide(moreFragment);

        if (isOldPhone) {
            mainMenuNavigation.getMenu().removeItem(R.id.main_menu_bottom_new_phone_notification);
            homeFragment = new OldPhoneHomeFragment();
            functionSettingFragment = new OldPhoneFunctionSettingFragment();

            fragmentTransaction.add(R.id.main_framelayout, homeFragment);
            fragmentTransaction.add(R.id.main_framelayout, functionSettingFragment);

            fragmentTransaction.hide(functionSettingFragment);
        }
        else {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    SharedPreferences savedToken = getSharedPreferences("savedToken", MODE_PRIVATE);
                    try {
                        Log.d("TOKEN", task.getResult());
                        DBThread db = new DBThread("set_fcm_token", savedToken.getString("token", ""), task.getResult());
                        db.start();
                        db.join(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mainMenuNavigation.getMenu().removeItem(R.id.main_menu_bottom_old_phone_function_setting);
            homeFragment = new NewPhoneHomeFragment();
            notificationFragment = new NewPhoneNotificationFragment();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String id = "id";
                String channelName = "움직임 / 소리 감지시 알림";
                String channelDescription = "움직임이나 소리가 감지될 경우 알림을 수신합니다.";
                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel channel = new NotificationChannel(id, channelName, importance);
                channel.setDescription(channelDescription);

                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }

            fragmentTransaction.add(R.id.main_framelayout, homeFragment);
            fragmentTransaction.add(R.id.main_framelayout, notificationFragment);

            fragmentTransaction.hide(notificationFragment);
        }

        mainTitleText.setText("홈");
        fragmentTransaction.commit();

        mainMenuNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                fragmentTransaction = fragmentManager.beginTransaction();
                int itemId = item.getItemId();
                if (itemId == R.id.main_menu_bottom_home) {
                    mainTitleText.setText("홈");
                    if (isOldPhone)
                        fragmentTransaction.hide(functionSettingFragment);
                    else
                        fragmentTransaction.hide(notificationFragment);
                    fragmentTransaction.hide(moreFragment);
                    fragmentTransaction.show(homeFragment);
                }
                else if (itemId == R.id.main_menu_bottom_old_phone_function_setting) {
                    mainTitleText.setText("기능 설정");
                    fragmentTransaction.hide(homeFragment);
                    fragmentTransaction.hide(moreFragment);
                    fragmentTransaction.show(functionSettingFragment);

                }
                else if (itemId == R.id.main_menu_bottom_new_phone_notification) {
                    mainTitleText.setText("알림");
                    fragmentTransaction.hide(homeFragment);
                    fragmentTransaction.hide(moreFragment);
                    fragmentTransaction.show(notificationFragment);
                }
                else if (itemId == R.id.main_menu_bottom_more) {
                    mainTitleText.setText("더보기");
                    if (isOldPhone)
                        fragmentTransaction.hide(functionSettingFragment);
                    else
                        fragmentTransaction.hide(notificationFragment);
                    fragmentTransaction.hide(homeFragment);
                    fragmentTransaction.show(moreFragment);
                }
                fragmentTransaction.commit();
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!savedToken.getBoolean("isOldPhone", true))
             notificationManager.cancelAll();
    }

    public boolean checkSavedToken() {
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
                Toast.makeText(this, "서버에 연결하지 못했어요.", Toast.LENGTH_LONG).show();
                finishAffinity();
                System.runFinalization();
            }
        }
        return false;
    }
}