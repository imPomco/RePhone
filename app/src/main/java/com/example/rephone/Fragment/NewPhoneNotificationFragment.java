package com.example.rephone.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rephone.Adapter.NotificationAdapter;
import com.example.rephone.Item.NotificationItem;
import com.example.rephone.R;
import com.example.rephone.Thread.DBThread;

import java.util.ArrayList;
import java.util.List;

public class NewPhoneNotificationFragment extends Fragment {
    private SharedPreferences savedToken;
    private RecyclerView notificationRecyclerView;
    private List<NotificationItem> notificationItems;
    private TextView notificationNoList;
    private DBThread db;
    private String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_phone_notification, container, false);
        notificationRecyclerView = view.findViewById(R.id.notification_recyclerview);
        notificationNoList = view.findViewById(R.id.notification_no_list);
        notificationItems = new ArrayList<>();

        savedToken = requireActivity().getSharedPreferences("savedToken", Context.MODE_PRIVATE);
        token = savedToken.getString("token", "");
        getNotificationList(token);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden)
            getNotificationList(token);
    }

    public void getNotificationList(String token) {
        String[] notificationArr;
        try {
            db = new DBThread("get_log", token);
            db.start();
            db.join(2000);

            notificationArr = db.getLog();

            if (notificationArr == null) {
                notificationArr = new String[0];
                notificationNoList.setVisibility(View.VISIBLE);
                notificationRecyclerView.setVisibility(View.GONE);
            }
            else {
                notificationNoList.setVisibility(View.GONE);
                notificationRecyclerView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            notificationArr = new String[0];
        }

        notificationItems.clear();
        for (String notification : notificationArr)
            notificationItems.add(new NotificationItem(notification));

        NotificationAdapter notificationAdapter = new NotificationAdapter(notificationItems);
        notificationRecyclerView.setAdapter(notificationAdapter);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}