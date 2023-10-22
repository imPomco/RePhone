package com.example.rephone.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.rephone.R;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {

    SharedPreferences isNewUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setColorTransitionsEnabled(true);
        showStatusBar(true);
        setIndicatorEnabled(true);
        setSystemBackButtonLocked(true);
        setWizardMode(true);
        //setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);

        addSlide(AppIntroFragment.createInstance("환영합니다!", "RePhone은 쓰이지 않는 구형 휴대전화를 센서로 사용할 수 있도록 만들어주는 앱입니다.", 0, R.color.green));
        addSlide(AppIntroFragment.createInstance("시작하기 전에..", "앱을 사용하려면 다음 권한이 필요합니다. (카메라, 저장소 읽기/쓰기, 마이크)", 0, R.color.orange));
        addSlide(AppIntroFragment.createInstance("이제 시작해볼까요?", "완료를 눌러 다음으로 진행하세요.", 0,  R.color.blue));

    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        isNewUser = getSharedPreferences("isNewUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = isNewUser.edit();
        editor.putBoolean("shouldShowIntro", false);
        editor.apply();

        Intent intent = new Intent(IntroActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPageSelected(int position) {
        super.onPageSelected(position);
        if (position == 1) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 0);

        }
        Log.d("debug", position + "");
    }
}
