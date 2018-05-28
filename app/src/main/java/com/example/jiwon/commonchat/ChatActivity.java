package com.example.jiwon.commonchat;
//[참조]https://www.androidtutorialpoint.com/firebase/real-time-android-chat-application-using-firebase-tutorial/

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.jiwon.commonchat.R.drawable.gradient_edittext;

//채팅화면
public class ChatActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "ChatActivity";       // 오류가 뜰것인가
    private Uri filePath;

    SingleTouchView stv;
    private LinearLayout linear;


    private ScrollView scrollView;
    private LinearLayout layout;
    private ImageView sendButton;   // messagearea.xml
    private EditText messageArea;   // messagearea.xml
    private DatabaseReference ref1, ref2;    // 데이터베이스를 참조하기 위한 선언
    private UserDTO uDTO;
    private FriendDTO fDTO;

    private DatabaseReference ref;
    private FirebaseAuth mAuth;

    // db 방 이름
    String roomname = "";
    String roomother = "";

    String myname;
    String username;
    //유저이름과 메세지를 넣어줄 Map 정의
    MessageDTO message;
    Map<String, String> map = new HashMap<String, String>();
    String messageText;

    boolean flag = false;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        roomother = intent.getExtras().getString("other");
        myname = intent.getExtras().getString("myName");
        roomname = myname+"_"+roomother;
        Log.d("getNickname", roomname);

        ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        ref1 = ref.child("messages").child(myname+"_"+roomother);
        ref2 = ref.child("messages").child(roomother+"_"+myname);


        layout = (LinearLayout) findViewById(R.id.layout);     // activity_chat의 LinearLayout
        sendButton = (ImageView) findViewById(R.id.sendButton);  // activity_chat의 LinearLayout
        messageArea = (EditText) findViewById(R.id.messageArea); // message_area의 EditText
        scrollView = (ScrollView) findViewById(R.id.scrollView); // activity_chat의 scrollView
        linear = (LinearLayout) findViewById(R.id.linear);

        // 그림메모 뷰를 현재 채팅뷰에 추가하는 부분
        stv = new SingleTouchView(this);
        stv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,500));
        linear.addView(stv);

        if (mAuth != null) {

            ref.child("messages").child(roomname).addChildEventListener(new com.google.firebase.database.ChildEventListener() {
                @Override
                public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                    MessageDTO messageDTO = dataSnapshot.getValue(MessageDTO.class);
                    if (messageDTO.getUser().equals(myname))
                        addMessageBox(messageDTO.getMessage(),1);
                    else
                        addMessageBox(messageDTO.getMessage(), 2);
                }

                public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {    }
                public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {   }
                public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {   }
                public void onCancelled(DatabaseError databaseError) {  }
            });

        }

        // sendButton을 눌렀을 때에 입력된 문자를 messageArea에 setText해줌
        sendButton.setOnClickListener(this);

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendButton:

                messageText = messageArea.getText().toString();
                // 메세지 내용이 존재할 경우
                if (!messageText.equals("")) {

                    ref.child("users").orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail()).addChildEventListener(new com.google.firebase.database.ChildEventListener() {
                        @Override
                        public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                            UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                            username = userDTO.getName();
                            message = new MessageDTO(messageText, username);
                            ref1.push().setValue(message);   //데이터베이스에 값넣어주기
                            ref2.push().setValue(message);
                            messageArea.setText("");
                        }

                        @Override
                        public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) { }

                        @Override
                        public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) { }

                        @Override
                        public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) { }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });

                }
        }
    }



    public void onSendMemo(View  v) {

        ref.child("messages").child(roomname).addChildEventListener(new com.google.firebase.database.ChildEventListener() {

            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                MessageDTO messageDTO = dataSnapshot.getValue(MessageDTO.class);
                if (messageDTO.getUser().equals(myname))
                    addImgMessageBox(filePath,1);       //
                else
                    addImgMessageBox(filePath,2);

            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {    }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {   }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {   }

            @Override
            public void onCancelled(DatabaseError databaseError) {  }
        });
    }



    // 채팅내용을 TextView에 담아 채팅방에 출력
    public void addMessageBox(String message, int type) {
        // ChatActivity클래스에 텍스트뷰 생성(보내지는 대화메세지들을 화면에 띄우기 위해)
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);

        // LayoutParams : 여백의 값(너비, 높이) 설정할 수 있게 해줌     // 현재 설정되어 있는 레이아웃 파라미터를 조사
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;  // 가중치 주기

        if (type == 1) {   //LinearLayout에 해당하는 속성은 오른쪽정렬
            lp2.gravity = Gravity.RIGHT;

        } else {   //LinearLayout에 해당하는 속성은 왼쪽정렬
            lp2.gravity = Gravity.LEFT;
        }

        // 폰트 설정
        Typeface typeface = getResources().getFont(R.font.font_bmjua);
        textView.setTypeface(typeface);
        textView.setTextSize(24);
        textView.setPadding(10,10,10,10);

        textView.setLayoutParams(lp2);      // textView(메세지)에 새로운 파라미터 적용
        layout.addView(textView);           // LinearLayout에 textView 추가 시키기(주고받는 메시지 영역에 추가)
        scrollView.fullScroll(ScrollView.FOCUS_DOWN); // 스크롤 가장 아래로 보내기(채팅시, 가장 최근 대화내용이 보일 수 있도록)
    }


    public void addImgMessageBox(Uri uri, int type) {
        // ChatActivity클래스에 텍스트뷰 생성(보내지는 대화메세지들을 화면에 띄우기 위해)
        final ImageView imgView = new ImageView(ChatActivity.this);
        stv = new SingleTouchView(this);
        File screenShot = ScreenShot(stv);      //
        filePath = Uri.fromFile(screenShot);
        imgView.setImageURI(filePath);

        // LayoutParams : 여백의 값(너비, 높이) 설정할 수 있게 해줌     // 현재 설정되어 있는 레이아웃 파라미터를 조사
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;  // 가중치 주기

        if (type == 1) {   //LinearLayout에 해당하는 속성은 오른쪽정렬
            lp2.gravity = Gravity.RIGHT;

        } else {   //LinearLayout에 해당하는 속성은 왼쪽정렬
            lp2.gravity = Gravity.LEFT;
        }

        imgView.setLayoutParams(lp2);      // textView(메세지)에 새로운 파라미터 적용
        layout.addView(imgView);           // LinearLayout에 textView 추가 시키기(주고받는 메시지 영역에 추가)
        scrollView.fullScroll(ScrollView.FOCUS_DOWN); // 스크롤 가장 아래로 보내기(채팅시, 가장 최근 대화내용이 보일 수 있도록)

        if (screenShot != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //Storage에 파일 올리기
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기

                            try {   //파일 내려받기
                                final File localFile = File.createTempFile("images", "jpg");
                                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    //성공시 이미지 출력
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Log.d("dev.hover", "업로드 성공, 디비 넣기전");

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
                                        double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                                    }
                                });

                            } catch (IOException e ) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
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
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {                  ////////
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();           ////
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });

        } else {
            Toast.makeText(getApplicationContext(), "그림을 먼저 그려주세요.", Toast.LENGTH_SHORT).show();
        }

        // 이미지를 데이터베이스에 넣기
        final String imgUri = filePath.toString();
        ref.child("users").orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail()).addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                username = userDTO.getName();
                message = new MessageDTO(imgUri, username);
                ref1.push().setValue(message);   //데이터베이스에 값넣어주기
                ref2.push().setValue(message);
                messageArea.setText("");
                ref.child("users").orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail()).removeEventListener(this);
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });


    }



    // 계산기, 그림메모 메뉴 보여주기
    public void moreMenu(View v) {
        LinearLayout selectMenu = findViewById(R.id.selectMenu);
        selectMenu.setVisibility(v.VISIBLE);
    }

    public void onMemo(View v) {
        LinearLayout selectMenu = (LinearLayout) findViewById(R.id.selectMenu);
        selectMenu.setVisibility(v.GONE);

        linear = (LinearLayout) findViewById(R.id.linear);
        linear.setVisibility(v.VISIBLE);

        Button sendMemo = (Button) findViewById(R.id.sendMemo);
        sendMemo.setVisibility(v.VISIBLE);
    }


    // 계산기 화면 보여주기
    public void onCal(View v) {
//        LinearLayout selectMenu = (LinearLayout) findViewById(R.id.selectMenu);
//        selectMenu.setVisibility(v.GONE);
//
//        LinearLayout calculatorXML = (LinearLayout) findViewById(R.id.calculatorXML);
//        calculatorXML.setVisibility(v.VISIBLE);
    }


    //storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    // 그림메모 저장하는 파일 이름명 정해주기
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
    Date now = new Date();
    String filename = formatter.format(now) + ".png";
    // 그림메모 이미지 파일 저장해주는 url 경로
    StorageReference storageRef = storage.getReferenceFromUrl("gs://commonchat-58d3d.appspot.com").child("images/" + filename); //ok

    private File ScreenShot(View view) {
        // 1. 캐쉬(Cache)를 허용시킨다.
        // 2. 그림을 Bitmap 으로 저장.
        // 3. 캐쉬를 막는다.
        stv.setDrawingCacheEnabled(true);    // 캐쉬허용

        // 캐쉬에서 가져온 비트맵을 복사해서 새로운 비트맵(스크린샷) 생성
//        Bitmap screenshot = Bitmap.createBitmap(stv.getDrawingCache());     //
        Bitmap screenshot = view.getDrawingCache();

        // SDCard(ExternalStorage) : 외부저장공간
        File file = new File(Environment.getExternalStorageDirectory()+"/Pictures", filename);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Toast.makeText(this, "저장 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
        }

        stv.setDrawingCacheEnabled(false);   // 캐쉬닫기
        return file;
    }



    // 그림메모가 가능한 뷰
    class SingleTouchView extends View {
        Paint paint = new Paint();
        Path path = new Path();
        public SingleTouchView(Context context) {
            super(context);

            paint.setAntiAlias(true);
            paint.setStrokeWidth(10f);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN :
                    path.moveTo(eventX, eventY);
                    return true;
                case MotionEvent.ACTION_MOVE :
                    path.lineTo(eventX, eventY);
                    break;
                case MotionEvent.ACTION_UP :
                    break;
                default:
                    return false;
            }
            invalidate();
            return true;
        }
    }

}
