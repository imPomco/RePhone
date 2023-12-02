package com.example.rephone.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rephone.R;
import com.google.android.material.slider.Slider;

public class OldPhoneFunctionSettingFragment extends Fragment {
    private SharedPreferences functionSettings;
    private LinearLayout motionLayout, soundLayout, screenLayout;
    private SwitchCompat motionSwitch, soundSwitch, screenSwitch;
    private Slider motionSlider, soundSlider;
    private TextView motionText, soundText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_old_phone_function_setting, container, false);
        motionLayout = view.findViewById(R.id.old_phone_function_setting_motion_layout);
        soundLayout = view.findViewById(R.id.old_phone_function_setting_sound_layout);
        screenLayout = view.findViewById(R.id.old_phone_function_setting_screen_black_layout);
        motionSwitch = view.findViewById(R.id.old_phone_function_setting_motion_switch);
        soundSwitch = view.findViewById(R.id.old_phone_function_setting_sound_switch);
        screenSwitch = view.findViewById(R.id.old_phone_function_setting_screen_black_switch);
        motionSlider = view.findViewById(R.id.old_phone_function_setting_motion_slider);
        soundSlider = view.findViewById(R.id.old_phone_function_setting_sound_slider);
        motionText = view.findViewById(R.id.old_phone_function_setting_motion_text);
        soundText = view.findViewById(R.id.old_phone_function_setting_sound_text);

        functionSettings = requireActivity().getSharedPreferences("functionSettings", Context.MODE_PRIVATE);

        motionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                motionSwitch.toggle();
            }
        });
        soundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundSwitch.toggle();
            }
        });
        screenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenSwitch.toggle();
            }
        });

        motionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                        motionSlider.setEnabled(true);
                    else {
                        compoundButton.toggle();
                        requireActivity().requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                        Toast.makeText(requireActivity(), "기능을 사용하려면 카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    motionSlider.setEnabled(false);

                }
                SharedPreferences.Editor editor = functionSettings.edit();
                editor.putBoolean("isMotionSwitchChecked", compoundButton.isChecked());
                editor.apply();
            }
        });

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                        soundSlider.setEnabled(true);
                    else {
                        compoundButton.toggle();
                        requireActivity().requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 100);
                        Toast.makeText(requireActivity(), "기능을 사용하려면 녹음 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    soundSlider.setEnabled(false);
                }
                SharedPreferences.Editor editor = functionSettings.edit();
                editor.putBoolean("isSoundSwitchChecked", compoundButton.isChecked());
                editor.apply();
            }
        });

        screenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = functionSettings.edit();
                editor.putBoolean("isScreenSwitchChecked", b);
                editor.apply();

            }
        });

        motionSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (value > 20)
                    motionText.setText("예민하게");
                else if (value < 20)
                    motionText.setText("둔하게");
                else
                    motionText.setText("보통");

                SharedPreferences.Editor editor = functionSettings.edit();
                editor.putFloat("motionSliderValue", value);
                editor.apply();
            }
        });

        soundSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (value > 16384)
                    soundText.setText("예민하게");
                else if (value < 16384)
                    soundText.setText("둔하게");
                else
                    soundText.setText("보통");

                SharedPreferences.Editor editor = functionSettings.edit();
                editor.putFloat("soundSliderValue", value);
                editor.apply();
            }
        });

        motionSwitch.setChecked(functionSettings.getBoolean("isMotionSwitchChecked", true));
        soundSwitch.setChecked(functionSettings.getBoolean("isSoundSwitchChecked", true));
        screenSwitch.setChecked(functionSettings.getBoolean("isScreenSwitchChecked", false));
        motionSlider.setValue(functionSettings.getFloat("motionSliderValue", 15));
        soundSlider.setValue(functionSettings.getFloat("soundSliderValue", 16384));

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            motionSwitch.toggle();
        if  (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            soundSwitch.toggle();

        return view;
    }
}