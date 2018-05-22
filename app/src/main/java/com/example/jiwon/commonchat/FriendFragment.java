package com.example.jiwon.commonchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class FriendFragment extends Fragment {
    private DatabaseReference mDatabase;
    private ListView friendListView;

    public FriendFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_friend, container, false);


        friendListView = (ListView) rootView.findViewById(R.id.friendListView);
        ArrayList<FriendDTO> friendlist = new ArrayList<>();

        final FriendAdapter adapter = new FriendAdapter();
        friendListView.setAdapter(adapter);
        adapter.addProfile(new FriendDTO(R.mipmap.ic_launcher,"이지원","하잌ㅋㅋ"));
        mDatabase.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserDTO dto = dataSnapshot.getValue(UserDTO.class);
                adapter.addProfile(new FriendDTO(R.mipmap.ic_launcher, dto.getName(), dto.getState()));
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

        return rootView;
    }

    class FriendAdapter extends BaseAdapter {
        ArrayList<FriendDTO> items = new ArrayList<FriendDTO>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addProfile(FriendDTO item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FrienditemView view = new FrienditemView(getContext());

            FriendDTO item = items.get(position);
            view.setName(item.getName());
            view.setState(item.getStateMessage());
            return view;
        }
    }


}
