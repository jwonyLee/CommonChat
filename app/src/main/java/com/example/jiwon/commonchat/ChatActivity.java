package com.example.jiwon.commonchat;
//[참조]https://www.androidtutorialpoint.com/firebase/real-time-android-chat-application-using-firebase-tutorial/

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

//채팅화면
public class ChatActivity extends AppCompatActivity {

    ScrollView scrollView;
    LinearLayout layout;
    ImageView sendButton;	// messagearea.xml
    EditText messageArea;	// messagearea.xml
    Firebase ref1, ref2;    // 데이터베이스를 참조하기 위한 선언
    UserDTO uDTO;
    FriendDTO fDTO;

    private FirebaseAuth mAuth;


    String u = "you";
    String f = "f";

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
////        DatabaseReference dataRef = reference.child("users").child("name");

        mAuth = FirebaseAuth.getInstance();

        layout = (LinearLayout) findViewById(R.id.layout);     // activity_chat의 LinearLayout
        sendButton = (ImageView)findViewById(R.id.sendButton);  // activity_chat의 LinearLayout
        messageArea = (EditText)findViewById(R.id.messageArea); // message_area의 EditText
        scrollView = (ScrollView)findViewById(R.id.scrollView); // activity_chat의 scrollView


        if (mAuth != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
            reference.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
                @Override
                public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                    UserDTO ooDTO = dataSnapshot.getValue(UserDTO.class);
                    Log.d("devdev","uDTO get Name"+ooDTO.getName());
                }

                @Override
                public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {

                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                    uDTO = dataSnapshot.getValue(UserDTO.class);
                    fDTO = dataSnapshot.getValue(FriendDTO.class);




//                    ref1 = new Firebase("https://commonchat-58d3d.firebaseio.com/messages/" + uDTO.getName() + "_" + fDTO.getNameTextView());
//                    ref2 = new Firebase("https://commonchat-58d3d.firebaseio.com/messages/" + fDTO.getNameTextView() + "_" + uDTO.getName());


                    // sendButton을 눌렀을 때에 입력된 문자를 messageArea에 setText해줌
                    sendButton.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            String messageText = messageArea.getText().toString();
                            // 메세지 내용이 존재할 경우
                            if (!messageText.equals("")) {
                                //유저이름과 메세지를 넣어줄 Map 정의
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("message", messageText);    //메세지 map에 넣기
                                map.put("user", uDTO.getName());  // 유저이름 map에 넣기
                                ref1.push().setValue(map);   //데이터베이스에 값넣어주기
                                ref2.push().setValue(map);
                                messageArea.setText("");
                            }
                        }
                    });


                    // 데이터 읽는 작업
                    ref1.addChildEventListener(new ChildEventListener() {
                        // 항목을 추가하기      / DataSnapshot:특정 데이터베이스 참조에 있던 데이터를 촬영한 사진과 비슷
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Map map = dataSnapshot.getValue(Map.class);
                            String message = map.get("message").toString(); //메세지 정보 얻어와, message에 넣기
                            String userName = map.get("user").toString();   //user이름 가져와, userName에 넣기

                            //userName이 나의 username과 일치한다면 오른쪽정렬 상태로 메세지 보내짐.
                            if (userName.equals(u)) {
                                addMessageBox("You:-\n" + message, 1);
                            }
                            //userName이 나의 username과 일치하지않는다면 왼쪽정렬 상태로 메세지 보내짐.
                            else {
                                addMessageBox(fDTO.getNameTextView() + ":-\n" + message, 2);
                            }
                        }

                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }

                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                        }

                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        public void onCancelled(FirebaseError firebaseError) {
                        }

                    });




                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        Firebase.setAndroidContext(this);




    }

    //
    public void addMessageBox(String message, int type){
        // ChatActivity클래스에 텍스트뷰 생성(보내지는 대화메세지들을 화면에 띄우기 위해)
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);

        // LayoutParams : 여백의 값(너비, 높이) 설정할 수 있게 해줌     // 현재 설정되어 있는 레이아웃 파라미터를 조사
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;  // 가중치 주기

        if(type == 1) {   //LinearLayout에 해당하는 속성은 오른쪽정렬
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{   //LinearLayout에 해당하는 속성은 왼쪽정렬
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }

        textView.setLayoutParams(lp2);      // textView(메세지)에 새로운 파라미터 적용
        layout.addView(textView);           // LinearLayout에 textView 추가 시키기(주고받는 메시지 영역에 추가)
        scrollView.fullScroll(View.FOCUS_DOWN); // 스크롤 가장 아래로 보내기(채팅시, 가장 최근 대화내용이 보일 수 있도록)
    }

}