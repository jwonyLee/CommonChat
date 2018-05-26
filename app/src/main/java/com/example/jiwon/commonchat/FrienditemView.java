package com.example.jiwon.commonchat;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FrienditemView extends LinearLayout {
    private TextView profileTextView;
    private TextView stateTextView;

    public FrienditemView(Context context) {
        super(context);

        init(context);
    }

    public FrienditemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_friend, this, true);

        profileTextView = (TextView) findViewById(R.id.profileTextView);
        stateTextView = (TextView) findViewById(R.id.stateTextView);
    }

    public void setName(String name) {
        profileTextView.setText(name);
    }

    public void setState(String state) {
        stateTextView.setText(state);
    }

}
