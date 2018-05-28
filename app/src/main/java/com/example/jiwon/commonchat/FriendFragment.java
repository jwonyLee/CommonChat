package com.example.jiwon.commonchat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ListView listView;
    private FriendAdapter adapter;
    private ArrayList<FriendDTO> list_itemArrayList;

    Cursor cursor;      // 데이터를 순차적으로 액세스할 때 사용
    String myName;

    public FriendFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null && mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null)
            myName = mAuth.getCurrentUser().getEmail();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_friend, container, false);

        listView = (ListView) rootView.findViewById(R.id.friendListView);
        list_itemArrayList = new ArrayList<FriendDTO>();

        if (mAuth != null) {
            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS);

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                // 권한 없음
            } else {
                // 권한 있음
                mDatabase.child("users").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot != null) {
                            UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                            // 데이터 접근을 위한 메소드( uri:원하는 데이터를 가져오기 위해 정해진 주소, projecion:null일 경우, 모든 컬럼 목록, selection:조건절, selectionArgs:selectinon에 ?로 표시한 곳에 들어갈 데이터, sortOrder:정렬을 위한 구문(order by) )
                            cursor = getActivity().getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

                            int count = 0;
                            int end = cursor.getCount();
                            String[] name = new String[end];
                            String[] phoneNumber = new String[end];

                            if (cursor.moveToFirst()) {

                                // 컬럼명으로 컬럼 인덱스 찾기
                                int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                                int phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                                do {

                                    // 요소값 얻기
                                    int id = cursor.getInt(idIndex);
                                    name[count] = cursor.getString(nameIndex);
                                    phoneNumber[count] = cursor.getString(phoneNumberIndex);

                                    // 얻은 전화번호를 전화번호 저장 형식에 맞추어 가공
                                    String death = phoneNumber[count].replaceAll("\\D", "");
                                    if (!death.startsWith("+82"))
                                        death = death.replaceFirst("010", "+8210");

                                    if (death.startsWith("82"))
                                        death = death.replaceFirst("82", "+82");

                                    // 어플 회원이면서 내 연락처의 저장되어있으면 친구 목록에 추가
                                    if (userDTO.getTel() == death) {
                                        list_itemArrayList.add(new FriendDTO(R.mipmap.ic_launcher, userDTO.getName()));
                                        adapter = new FriendAdapter(getActivity(), list_itemArrayList);
                                        listView.setAdapter(adapter);
                                    }

                                    if (userDTO.getEmail() == myName) {
                                        myName = userDTO.getName();
                                    }

                                    count++;

                                } while (cursor.moveToNext() || count > end);

                            }
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            ArrayList<String> al = new ArrayList<>();

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView item = (TextView) view.findViewById(R.id.profileTextView);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("other", item.getText().toString());
                intent.putExtra("myName", myName);
                startActivity(intent);

            }
        });


        return rootView;
    }


    // 현재의 상태를 저장, 저장한 상태를 재사용 가능하게 해줌
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

}