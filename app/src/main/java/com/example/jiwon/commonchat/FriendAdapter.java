package com.example.jiwon.commonchat;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendAdapter extends BaseAdapter {
    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView stateTextView;

    Context context;
    ArrayList<FriendDTO> list_itemArrayList;

    public FriendAdapter (Context context, ArrayList<FriendDTO> list_itemArrayList) {
        this.context = context;
        this.list_itemArrayList = list_itemArrayList;

    }

    @Override
    public int getCount() {
        return list_itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return list_itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend, null);
            profileImageView = (ImageView) convertView.findViewById(R.id.profileImageView);
            nameTextView = (TextView) convertView.findViewById(R.id.profileTextView);
            stateTextView = (TextView) convertView.findViewById(R.id.stateTextView);
        }

        profileImageView.setImageResource(list_itemArrayList.get(position).getProfileImageView());
        nameTextView.setText(list_itemArrayList.get(position).getNameTextView().toString());
        stateTextView.setText(list_itemArrayList.get(position).getStateTextView().toString());


        return convertView;
    }

}
