package com.example.rephone.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rephone.Activity.MainActivity;
import com.example.rephone.Activity.StartActivity;
import com.example.rephone.R;
import com.example.rephone.Thread.DBThread;

import java.util.Objects;

public class NewPhoneHomeFragment extends Fragment {
    private SharedPreferences savedToken;
    private TextView textTitle, textRunning, textMotion, textSound;
    private ImageView imageTitle;
    private DBThread db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_phone_home, container, false);

        textTitle = view.findViewById(R.id.new_phone_home_title_textview);
        textRunning = view.findViewById(R.id.new_phone_home_running_textview);
        textMotion = view.findViewById(R.id.new_phone_home_motion_textview);
        textSound = view.findViewById(R.id.new_phone_home_sound_textview);
        imageTitle = view.findViewById(R.id.new_phone_home_title_image);

        setTextView();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setTextView();
        }
    }

    private void setTextView() {

        String token = ((MainActivity) requireActivity()).savedToken.getString("token", "");
        try {
            db = new DBThread("get_function", token);
            db.start();
            db.join(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] function = db.getFunction();
        if (function != null) {
            if (function[1].equals("1")) {
                textMotion.setText("동작 중");
                textMotion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.resize_image_check_mark, 0);
            }
            else {
                textMotion.setText("사용 안함");
                textMotion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.resize_image_minus_mark, 0);
            }

            if (function[2].equals("1")) {
                textSound.setText("동작 중");
                textSound.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.resize_image_check_mark, 0);
            }
            else {
                textSound.setText("사용 안함");
                textSound.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.resize_image_minus_mark, 0);
            }

            if (function[0].equals("1")) {
                textTitle.setText("정상 동작 중");
                imageTitle.setImageResource(R.drawable.image_check_mark);
                textRunning.setText("실행 중");
                textRunning.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.resize_image_check_mark, 0);

            }
            else {
                textTitle.setText("동작 중지됨");
                imageTitle.setImageResource(R.drawable.image_x_mark);
                textRunning.setText("실행 중지 됨");
                textRunning.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.resize_image_x_mark, 0);
                textMotion.setText("중지 됨");
                textMotion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.resize_image_x_mark, 0);
                textSound.setText("중지 됨");
                textSound.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.resize_image_x_mark, 0);
            }
        }
    }
}