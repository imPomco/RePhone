package com.example.rephone.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.rephone.R;
import com.example.rephone.Thread.DBThread;

public class MoreFragment extends Fragment {
    private Button resetApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        resetApp = view.findViewById(R.id.more_reset_app);

        resetApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResetDialog();
            }
        });

        return view;
    }

    public void showResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle("정말 초기화하시겠어요?")
                .setMessage("모든 연결과 기록이 제거되며, 앱이 종료됩니다.")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences isNewUser = requireActivity().getSharedPreferences("isNewUser", Context.MODE_PRIVATE);
                        SharedPreferences savedToken = requireActivity().getSharedPreferences("savedToken", Context.MODE_PRIVATE);

                        SharedPreferences.Editor isNewUserEdit = isNewUser.edit();
                        SharedPreferences.Editor savedTokenEdit = savedToken.edit();

                        String token = savedToken.getString("token", "");
                        try {
                            DBThread db = new DBThread("reset_app", token);
                            db.start();
                            db.join(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        isNewUserEdit.clear();
                        isNewUserEdit.apply();

                        savedTokenEdit.clear();
                        savedTokenEdit.apply();

                        requireActivity().finish();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}