package com.example.jiwon.commonchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinUsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Context mContext;
    private final String TAG = "회원가입 액티비티";

    private EditText mEmail;
    private EditText mPassword;
    private EditText mPassword2;
    private Button mJoin;
    private TextView mReturnLogin;

    private FirebaseDatabase database;
    private DatabaseReference Ref;

    // 사용자의 로그인 상태 변화에 따라서 이벤트를 받을 리스너 객체
    private FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_us);

        mEmail = (EditText) findViewById(R.id.editJoinEmail);
        mPassword = (EditText) findViewById(R.id.editJoinPassword);
        mPassword2 = (EditText) findViewById(R.id.editJoinPassword2);
        mJoin = (Button) findViewById(R.id.btnJoin);
        mReturnLogin = (TextView) findViewById(R.id.btnReturnJoin);


        // 초기화
        mContext = this;
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // 로그인이 되어있는 상태
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // 로그인X
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        database = FirebaseDatabase.getInstance();
        Ref = database.getReference();


        // 로그인 페이지 이동 리스너 추가
        mReturnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinUsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String password2 = mPassword2.getText().toString();
                createAccount(email, password, password2);
            }
        });

    }

    private void createAccount(final String email, final String password, String password2) {
        if (!isValidEmail(email)) {
            Log.e(TAG, "createAccount: email is not valid ");
            Toast.makeText(JoinUsActivity.this, "이메일이 유효하지 않습니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (isValidPassword(password)) {
            Log.e(TAG, "createAccount: password is not valid ");
            Toast.makeText(mContext, "비밀번호가 유효하지 않습니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEqualPassword(password, password2)) {
            Log.e(TAG, "createAccount: Passwords do not match.");
            Toast.makeText(mContext, "비밀번호가 일치하지 않습니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, "회원가입에 실패했습니다.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "회원가입 성공!",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(JoinUsActivity.this, SetProfileActivity.class));
                        }

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // 비밀번호 유효성 검사 메소드
    // 6자리 이상, 한글 미포함
    private boolean isValidPassword(String target) {
        Pattern p = Pattern.compile("(^.*(?=.{6,100})(?=.*[0–9])(?=.*[a-zA-Z]).*$)");

        Matcher m = p.matcher(target);
        if (m.find() && !target.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
            return true;
        } else {
            return false;
        }
    }

    // 비밀번호 중복 입력 후 검사
    private boolean isEqualPassword(String pw, String pw2) {
        if (pw.equals(pw2)) {
            return true;
        } else {
            return false;
        }
    }

    // 이메일 유효성 검사 메소드
    private boolean isValidEmail(String target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}