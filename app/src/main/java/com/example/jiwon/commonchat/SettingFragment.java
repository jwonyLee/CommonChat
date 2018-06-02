package com.example.jiwon.commonchat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SettingFragment extends Fragment implements View.OnClickListener{
    public SettingFragment() {};
    LinearLayout setImage;
    LinearLayout setLogout;
    LinearLayout setAlram;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup settingView = (ViewGroup)  inflater.inflate(R.layout.fragment_setting, container, false);

        setImage = (LinearLayout) settingView.findViewById(R.id.setImage);
        setImage.setOnClickListener(this);
        settingView.findViewById(R.id.setLogout).setOnClickListener(this);


        return settingView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setImage:
                startActivity(new Intent(getActivity(), SetImageProfileActivity.class));
                break;
            case R.id.setLogout:
                startActivity(new Intent(getActivity(), TestActivity.class));
                break;
        }
    }
}
