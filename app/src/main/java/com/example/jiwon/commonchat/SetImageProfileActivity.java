package com.example.jiwon.commonchat;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SetImageProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private static final String TAG = "MainActivity";
    final int GET_PICTURE_URI = 100;

    Uri filePath;

    private ImageButton mImage;
    private Button mSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_image_profile);

        init();


        findViewById(R.id.setImageProfile).setOnClickListener(this);
        findViewById(R.id.setProfileBtn).setOnClickListener(this);

    }

    public void init() {
        mImage = (ImageButton) findViewById(R.id.setImageProfile);
        mSetup = (Button) findViewById(R.id.setProfileBtn);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            // 이미지 선택 이벤트
            case R.id.setImageProfile:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
                break;

                // 설정 버튼 이벤트
            case R.id.setProfileBtn:
                saveImg();
                startActivity(new Intent(SetImageProfileActivity.this, MenuActivity.class));
                break;

        }
    }

    //결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if (requestCode == 0 && resultCode == RESULT_OK) {
            filePath = data.getData();
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageButton에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mImage.setImageBitmap(bitmap);
                mImage.setImageURI(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void saveImg() {


        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            // 파일명 만들기
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss", Locale.KOREA);
            Date now = new Date();
            String filename = formatter.format(now) + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            final StorageReference storageRef = storage.getReferenceFromUrl("gs://commonchat-58d3d.appspot.com").child("images/" + filename);

            //파일 올리기
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
//                            mImage.setImageResource(0);

                            try {
                                final File localFile = File.createTempFile("images", "jpg");
                                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    //성공시 이미지 출력
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                        mImage.setImageBitmap(bitmap);
                                        Toast.makeText(getApplicationContext(), "다운로드 완료!", Toast.LENGTH_SHORT).show();
                                    }// 실패시 메세지 출력
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(getApplicationContext(), "다운로드 실패!", Toast.LENGTH_SHORT).show();
                                    }// progress 진행 상황
                                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        @SuppressWarnings("VisibleForTests")
                                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();


                            if (mAuth != null) {
                                // DATABASE 넣는 부분
                                String s = filePath.toString();
                                mDatabase.child("users").child("name").push().setValue(s);
                            }

                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });

        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }


}
