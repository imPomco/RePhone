package com.example.rephone.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rephone.R;
import com.example.rephone.Thread.DBThread;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class SelectOldPhoneQRActivity extends AppCompatActivity {
    private ImageView qrImage;
    private Bitmap qr;
    private CountDownTimer countDownTimer;
    private TextView refreshTextview;
    private Button refreshButton;

    private String token = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_old_phone_qr);

        qrImage = findViewById(R.id.select_old_phone_image_qr);
        refreshTextview =  findViewById(R.id.select_old_phone_refresh_textview);
        refreshButton = findViewById(R.id.select_old_phone_refresh_button);

        createQrImage();
        startCountDown();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                createQrImage();
                startCountDown();
            }
        });
    }
    private void startCountDown() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                checkNewPhoneConnected();
                refreshTextview.setText(String.format("%d초 후 자동으로 새로고침돼요.", l / 1000));
            }

            @Override
            public void onFinish() {
                createQrImage();
                startCountDown();
            }
        }.start();
    }

    private void createQrImage() {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            DBThread db = new DBThread("create_qr_code");
            db.start();
            db.join(2000);
            token = db.getToken();
            Log.d("Token Test", token);
            qr = barcodeEncoder.encodeBitmap(token, BarcodeFormat.QR_CODE, 400, 400);
        } catch (Exception e) {
            e.printStackTrace();
        }
        qrImage.setImageBitmap(qr);
    }

    private void checkNewPhoneConnected() {
        try {
            DBThread db = new DBThread("check_new_phone", token);
            db.start();
            db.join(1000);
            String str = db.checkNewPhone();
            if (str.equals("true")) {
                countDownTimer.cancel();
                Toast.makeText(SelectOldPhoneQRActivity.this, "연결됨", Toast.LENGTH_LONG).show();
                SharedPreferences savedToken = getSharedPreferences("savedToken", MODE_PRIVATE);
                SharedPreferences.Editor editor = savedToken.edit();
                editor.putString("token", token);
                editor.putBoolean("isEnteredMainActivity", false);
                editor.putBoolean("isOldPhone", true);
                editor.apply();

                Intent intent = new Intent(SelectOldPhoneQRActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
            else if (str.equals("fail")) {
                Toast.makeText(this, "서버와 연결하지 못했어요. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}