package com.example.jiwon.commonchat;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

//volley 데이터 통신..
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


//친구목록
public class UserActivity extends AppCompatActivity {

    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();    //userList에 쓰일 친구목록 추가 삭제를 위해 사용됨.
    int totalUsers = 0;     // 친구목록 개수의 초기치를 0으로 줌
    ProgressDialog pd;      // 사용자에게 진행상황을 알려주는 속성

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        usersList = (ListView)findViewById(R.id.usersList);     //친구목록리스트, activity_user의 ListView
        noUsersText = (TextView)findViewById(R.id.noUsersText); //activity_user의 TextView

        pd = new ProgressDialog(UserActivity.this);
        pd.setMessage("Loading...");    //  ProgressDialog 내용을 Loading...으로 바궈주기
        pd.show();  //ProgressDialog 내용 보여주기

        String url = "https:/commonchat-58d3d.firebaseio.com/users.json";

        // url을 지정해 json을 서버에 보내 요청을하고, string형식의 응답을 수신함.
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        },new Response.ErrorListener(){ // 응답 도중 오류 발생시,  오류메세지 출력
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        // 하나만 생성하고도 지속적으로 Queue에 데이터를 전달하는 방식
        RequestQueue rQueue = Volley.newRequestQueue(UserActivity.this);
        rQueue.add(request);

        // userList에서 친구 목록 눌렀을 시, UserActivity에서 ChatActivity로 전환(채팅화면으로 전환)
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = al.get(position);
                startActivity(new Intent(UserActivity.this, ChatActivity.class));
            }

        });
    }


    public void doOnSuccess(String s) {
        try {
            // JSON형태의 데이터를 관리해 주는 메서드
            JSONObject obj = new JSONObject(s);

            //키 값을 추출하여 Iterator가 관리해줌
            Iterator i = obj.keys();
            String key = "";

            // i로 obj.keys()에 모든 요소 순차적으로 검색할 때까지 반복
            while(i.hasNext()){
                key = i.next().toString();

                // key값이 username과 같지 않다면 key를 ArrayList에 추가
                if(!key.equals(UserDetails.username)) {
                    al.add(key);
                }
                totalUsers++;      // 친구목록 개수 1증가
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 친구목록 갯수가 1이하일 경우, noUsersText가 보여지고 userList는 보이지않음.
        if(totalUsers <=1){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }
        // noUsersText는 보이지않고, userList는 보이게 됨.
        else{
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            // userList에 사용할 데이터를 넘겨줌.
            // simple_list_item_1:안드로이드에서 기본적으로 제공하는 원소 레이아웃
            // al:리스트의 원소를저장하는 배열, 각각 항목을 보여줄 실제 데이터
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
        }

        pd.dismiss();       // ProgressDialog 종료
    }

}