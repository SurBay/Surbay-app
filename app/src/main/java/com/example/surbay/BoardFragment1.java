package com.example.surbay;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class BoardFragment1 extends Fragment // Fragment 클래스를 상속받아야한다
{

    private static final int NEW = 1;
    private static final int GOAL = 2;
    private static final int DEADLINE= 3;
    private static int SORT;
    private View view;
    static final int WRITE_NEWPOST = 1;
    static final int DO_SURVEY = 2;
    static final int NEWPOST = 1;
    static final int DONE = 1;
    static final int NOT_DONE = 0;
    private ListViewAdapter listViewAdapter;
    private ListView listView;

    private ArrayList<Post> list;

    private Comparator<Post> cmpDeadline;
    private Comparator<Post> cmpNew;
    private Comparator<Post> cmpGoal;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_board1,container,false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d("sort as", ""+SORT);
        listView = view.findViewById(R.id.list);
        SORT = MainActivity.SORT;
        cmpGoal = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                int ret;
                float goal1 = ((float)o1.getParticipants()/o1.getGoal_participants());
                float goal2 = ((float)o2.getParticipants()/o2.getGoal_participants());
                if(goal1<goal2)
                    ret = -1;
                else if(goal1==goal2)
                    ret = 0;
                else
                    ret = 1;
                return ret;
            }
        };
        cmpNew = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                int ret;
                Date date1 = o1.getDate();
                Date date2 = o2.getDate();
                int compare = date1.compareTo(date2);
                if(compare>0)
                    ret = -1; //date2<date1
                else if(compare==0)
                    ret = 0;
                else
                    ret = 1;
                return ret;
            }
        };
        cmpDeadline = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                int ret;
                Date date1 = o1.getDeadline();
                Date date2 = o2.getDeadline();
                int compare = date1.compareTo(date2);
                if(compare<0)
                    ret = -1; //date2>date1
                else if(compare==0)
                    ret = 0;
                else
                    ret = 1;
                return ret;
            }
        };
        list = new ArrayList<>(MainActivity.postArrayList);
        switch (SORT){
            case NEW:
                Collections.sort(list, cmpNew);
                break;
            case GOAL:
                Collections.sort(list, cmpGoal);
                break;
            case DEADLINE:
                Collections.sort(list, cmpDeadline);
                break;
            default:
                break;
        }

        listViewAdapter = new ListViewAdapter(list);
        listView.setAdapter(listViewAdapter);


        Log.d("array length", ""+MainActivity.postArrayList.size());
//        try {
//            getPosts();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭시 글 확대 기능 추가 예정
                Post item = (Post) listViewAdapter.getItem(position);
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                startActivityForResult(intent, DO_SURVEY);
            }
        });

        Button writeButton = view.findViewById(R.id.writeButton);
        writeButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), WriteActivity.class);
                        startActivityForResult(intent, WRITE_NEWPOST);
                    }
                }
        );
        return view;
    }
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        listView = view.findViewById(R.id.list);
        SORT = MainActivity.SORT;
        cmpGoal = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                int ret;
                float goal1 = ((float)o1.getParticipants()/o1.getGoal_participants());
                float goal2 = ((float)o2.getParticipants()/o2.getGoal_participants());
                if(goal1<goal2)
                    ret = -1;
                else if(goal1==goal2)
                    ret = 0;
                else
                    ret = 1;
                return ret;
            }
        };
        cmpNew = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                int ret;
                Date date1 = o1.getDate();
                Date date2 = o2.getDate();
                int compare = date1.compareTo(date2);
                if(compare>0)
                    ret = -1; //date2<date1
                else if(compare==0)
                    ret = 0;
                else
                    ret = 1;
                return ret;
            }
        };
        cmpDeadline = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                int ret;
                Date date1 = o1.getDeadline();
                Date date2 = o2.getDeadline();
                int compare = date1.compareTo(date2);
                if(compare<0)
                    ret = -1; //date2>date1
                else if(compare==0)
                    ret = 0;
                else
                    ret = 1;
                return ret;
            }
        };
        list = new ArrayList<>(MainActivity.postArrayList);
        switch (SORT){
            case NEW:
                Collections.sort(list, cmpNew);
                break;
            case GOAL:
                Collections.sort(list, cmpGoal);
                break;
            case DEADLINE:
                Collections.sort(list, cmpDeadline);
                break;
            default:
                break;
        }

        listViewAdapter = new ListViewAdapter(list);
        listView.setAdapter(listViewAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case WRITE_NEWPOST:
                switch (resultCode) {
                    case NEWPOST:
                        Post item = data.getParcelableExtra("post");
                        try {
                            String title = data.getStringExtra("title");
                            String author = data.getStringExtra("author");
                            Integer author_lvl = data.getIntExtra("author_lvl", 1);
                            String content = data.getStringExtra("content");
                            Integer participants = data.getIntExtra("participants", 0);
                            Integer goal_participants = data.getIntExtra("goalParticipants", 0);
                            String url = data.getStringExtra("url");
                            Date date = new SimpleDateFormat(getActivity().getString(R.string.date_format)).parse(data.getStringExtra("date"));
                            Date deadline = new SimpleDateFormat(getActivity().getString(R.string.date_format)).parse(data.getStringExtra("deadline"));
                            Boolean with_prize = data.getBooleanExtra("with_prize", false);
                            String prize = null;
                            if(with_prize) {
                                prize = data.getStringExtra("prize");
                            }
                            Integer est_time = data.getIntExtra("est_time", 5);
                            String target = data.getStringExtra("target");

                            listViewAdapter = new ListViewAdapter(MainActivity.postArrayList);
                            listView.setAdapter(listViewAdapter);
                            MainActivity.getPosts();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    default:
                        return;
                }
            case DO_SURVEY:
                Log.d("result code is", ""+resultCode);
                switch (resultCode){
                    case(DONE):
                        int position = data.getIntExtra("position", -1);
                        int newParticipants = data.getIntExtra("participants", -1);
                        listViewAdapter.updateParticipants(position, newParticipants);
                        Post item = (Post) listViewAdapter.getItem(position);

                        listView.setAdapter(listViewAdapter);
                        Log.d("new participant", "new is: " + item.getParticipants());
                        return;
                    default:
                        return;
                }
            default:
                return;
        }

    }


}