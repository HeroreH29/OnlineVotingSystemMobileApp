package com.example.onlinevotingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProfileSettingAdapter extends RecyclerView.Adapter<ProfileSettingAdapter.ProfileSettingHolder> {

    private ArrayList<ProfileSettingItem> settingList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ProfileSettingHolder extends RecyclerView.ViewHolder {
        public TextView settingTitle;

        public ProfileSettingHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            settingTitle = itemView.findViewById(R.id.settingTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public ProfileSettingAdapter(ArrayList<ProfileSettingItem> settingList) {
        this.settingList = settingList;
    }

    @NonNull
    @Override
    public ProfileSettingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profilesetting_item, parent, false);
        ProfileSettingHolder psh = new ProfileSettingHolder(v, listener);
        return psh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileSettingHolder holder, int position) {
        ProfileSettingItem currentItem = settingList.get(position);

        holder.settingTitle.setText(currentItem.getSettingTitle());

    }

    @Override
    public int getItemCount() {
        return settingList.size();
    }

}
