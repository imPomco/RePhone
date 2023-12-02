package com.example.rephone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rephone.Item.NotificationItem;
import com.example.rephone.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<NotificationItem> items;

    public NotificationAdapter(List<NotificationItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_fragment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item =  items.get(position);
        holder.logType.setText(item.getLogType());
        holder.occurDate.setText(item.getOccurDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView logType;
        private TextView occurDate;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            logType = itemView.findViewById(R.id.item_notification_log_type);
            occurDate = itemView.findViewById(R.id.item_notification_occur_date);
        }
    }
}
