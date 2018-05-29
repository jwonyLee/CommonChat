package com.example.jiwon.commonchat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ChatFragment extends Fragment {
    private DatabaseReference ref1, ref2;    // 데이터베이스를 참조하기 위한 선언


    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        ref1 = ref.child("messages").child(ChatActivity.roomname);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mAuth != null) {
            ref1.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
}


