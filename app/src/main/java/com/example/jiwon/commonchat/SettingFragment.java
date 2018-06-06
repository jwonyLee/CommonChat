package com.example.jiwon.commonchat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class SettingFragment extends Fragment implements View.OnClickListener{
    public SettingFragment() {};
    LinearLayout setImage;
    LinearLayout setLogout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup settingView = (ViewGroup)  inflater.inflate(R.layout.fragment_setting, container, false);

        // 프로필 설정 누를시, 이미지 프로필 사진 설정하는 화면으로 이동
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
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
                break;
        }
    }
}
