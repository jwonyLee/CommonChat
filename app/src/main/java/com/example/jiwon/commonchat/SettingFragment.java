package com.example.jiwon.commonchat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingFragment extends Fragment {
    public SettingFragment() {};
    LinearLayout setImage;
    LinearLayout setLogout;
    LinearLayout setAlram;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup settingView = (ViewGroup)  inflater.inflate(R.layout.fragment_setting, container, false);

        // 프로필 설정 누를시, 이미지 프로필 사진 설정하는 화면으로 이동
        setImage = (LinearLayout) settingView.findViewById(R.id.setImage);
        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SetImageProfileActivity.class));
            }
        });


        return settingView;
    }
}
