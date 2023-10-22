package com.example.rephone.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rephone.R;
import com.example.rephone.Thread.DBThread;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class SelectNewPhoneQRActivity extends AppCompatActivity {
    private Button qrButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_new_phone_qr);

        qrButton = findViewById(R.id.select_new_phone_qr_button);

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanOptions options = new ScanOptions();
                options.setBeepEnabled(false);
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                options.setPrompt("QR코드를 인식시켜 주세요.");
                barcodeLauncher.launch(options);
            }
        });
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String token = result.getContents();
            try {
                DBThread db = new DBThread("check_old_phone", token);
                db.start();
                db.join(2000);
                if (db.checkOldPhone()) {
                    Toast.makeText(SelectNewPhoneQRActivity.this, "연결됨", Toast.LENGTH_LONG).show();
                    SharedPreferences savedToken = getSharedPreferences("savedToken", MODE_PRIVATE);
                    SharedPreferences.Editor editor = savedToken.edit();
                    editor.putString("token", token);
                    editor.putBoolean("isEnteredMainActivity", false);
                    editor.putBoolean("isOldPhone", false);
                    editor.apply();

                    Intent intent = new Intent(SelectNewPhoneQRActivity.this, StartActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
}