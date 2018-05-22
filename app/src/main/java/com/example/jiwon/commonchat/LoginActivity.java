package com.example.jiwon.commonchat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private final String TAG = "LoginActivity";

    private EditText mEmail;
    private EditText mPassword;
    private Button mLogin;
    private TextView mReturnJoin;
    private Button mGoogle;

    private CallbackManager callbackManager;
    private LoginButton mFacebook;

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 101;


    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){ // 만약 로그인이 되어있으면 다음 액티비티 실행
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();//
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkPermission();

        // 초기화
        mEmail = (EditText) findViewById(R.id.editEmail);
        mPassword = (EditText) findViewById(R.id.editPassword);
        mLogin = (Button) findViewById(R.id.btnLogin);
        mReturnJoin = (TextView) findViewById(R.id.btnReturnJoin);
        mGoogle = (Button) findViewById(R.id.btnGoogle);
        mFacebook = (LoginButton) findViewById(R.id.btnFacebook);

        // GoogleSignInOptions 생성
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder
                (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Google 로그인을 위한 객체 생성
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // 로그인 작업의 onCreate 메소드에서 FirebaseAuth 개체의 공유 인스턴스
        mAuth = FirebaseAuth.getInstance();

        // 구글 로그인 버튼 이벤트 > signInIntent 호출
        mGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                finish();
            }
        });

        // 이메일 로그인 버튼 이벤트
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginWithEmail(mEmail.getText().toString(), mPassword.getText().toString());

            }
        });

        // 페이스북 로그인
        callbackManager = CallbackManager.Factory.create();
        mFacebook.setReadPermissions("email");

        mFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Intent intent = new Intent(LoginActivity.this, SetProfileActivity.class);
                        startActivity(intent);
                        finish();
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {  }

                    @Override
                    public void onError(FacebookException exception) {  }
                });



        // 회원가입 페이지 이동 리스너 추가
        mReturnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, JoinUsActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    // 페이스북 계정 정보를 파이어베이스 user에 넘기기 위한 메소드
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공하면, 로그인 화면에서 프로필 설정 화면으로 전환
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, SetProfileActivity.class));
                            finish();
                        } else {
                            // 로그인 실패시, 로그인 실패 메세지 띄워줌
                            Toast.makeText(LoginActivity.this, "페이스북 로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    // 이메일로 로그인 처리 메소드
    private void LoginWithEmail(String email, String password) {
       mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공하면 로그인 화면에서 프로필 설정 화면으로 전환
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, SetProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // 로그인 실패시, 실패 메세지 띄워짐
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //페이스북 로그인
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //구글 로그인
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

                // 구글 로그인 성공시, 파이어베이스에 인증된 후 로그인화면에서 전환
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Intent intent = new Intent(LoginActivity.this, SetProfileActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(LoginActivity.this, "로그인 실패",
                        Toast.LENGTH_SHORT).show();

                // ...

            }

        }


    }

    // 사용자가 정상적으로 로그인한 후에 GoogleSignInAccount 개체에서 ID 토큰을 가져와서
    //Firebase 사용자 인증 정보로 교환하고 Firebase 사용자 인증 정보를 사용해 Firebase에 인증합니다.
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //로그인 실패시, 유저 메세지 화면에 띄워짐. 만약 성공한다면
                        //인증에 성공한 유저에게 메시지를 띄워주고 로그인 화면에서 프로필 화면으로 전환
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "FireBase 아이디 생성이 완료 되었습니다", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, SetProfileActivity.class));
                            finish();
                        }
                    }

                });


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("알림", "onConnectionFailed");
    }


    // 로그아웃
    public void signOut() {
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                mAuth.signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.v("알림", "로그아웃 성공");
                                setResult(1);
                            } else {
                                setResult(0);
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.v("알림", "Google API Client Connection Suspended");
                setResult(-1);
            }
        });

    }

    // 주소록과 전화 권한 설정 여부 메소드
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한요청 거부로 인한 어플리케이션 종료
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    android.Manifest.permission.READ_CONTACTS) || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    android.Manifest.permission.READ_PHONE_STATE)) {

                finish();

                // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다

                // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다



                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // 권한 요청 가능
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

                // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다


            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                // 권한 허가, 해당 권한을 사용해서 작업을 진행
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    // 권한 거부, 유저가 해당권한을 거부했을때 어플리케이션 종료
                } else {

                    moveTaskToBack(true);       // 이코드가 있는 액티비티 종료
                    finish();   // 현재 액티비티 종료
                    android.os.Process.killProcess(android.os.Process.myPid()); // 현재 서비스 및 프로세스 종료


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

}