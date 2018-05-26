package com.example.jiwon.commonchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SetProfileActivity extends AppCompatActivity {
    private EditText mSetName;
    private Button mStartbtn;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference Ref;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        // 초기화
        mSetName = (EditText) findViewById(R.id.setName);
        mStartbtn = (Button) findViewById(R.id.btnStart);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        Ref = database.getReference();

        mStartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSetName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "이름이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(SetProfileActivity.this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SetProfileActivity.this, android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SetProfileActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permissio0n. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                String email = mAuth.getCurrentUser().getEmail();
                String name = mSetName.getText().toString();
                String tel = mgr.getLine1Number().toString();
                String state = "";

                UserDTO user = new UserDTO(email, state, name, tel);
                Ref.child("users").push().setValue(user);
                startActivity(new Intent(SetProfileActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}