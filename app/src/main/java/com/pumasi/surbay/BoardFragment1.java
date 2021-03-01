package com.pumasi.surbay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.ListViewAdapter;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.Surveytip;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.pumasi.surbay.HomeFragment.adapter1;
import static com.pumasi.surbay.HomeFragment.adapter2;
import static com.pumasi.surbay.HomeFragment.adapter3;
import static com.pumasi.surbay.HomeFragment.recyclerView;
import static com.pumasi.surbay.HomeFragment.recyclerView2;
import static com.pumasi.surbay.HomeFragment.recyclerView3;


public class BoardFragment1 extends Fragment// Fragment 클래스를 상속받아야한다
{ // 게시판

    private static final int NEW = 1;
    private static final int GOAL = 2;
    private static final int DEADLINE= 3;
    private static int SORT;
    private View view;
    static final int WRITE_NEWPOST = 1;
    static final int DO_SURVEY = 2;
    static final int NEWPOST = 1;
    static final int DONE = 1;
    static final int DELETE = 4;
    static final int FIX_DONE = 3;
    static final int REPORTED = 5;
    static final int NOT_DONE = 0;
    public static ListViewAdapter listViewAdapter;
    public static ListView listView;

    public static ArrayList<Post> list;

    private Comparator<Post> cmpDeadline;
    private Comparator<Post> cmpNew;
    private Comparator<Post> cmpGoal;

    public static Button frag1newsortbutton;
    public static Button frag1goalsortbutton;
    public static Button frag1datesortbutton;
    private SwipeRefreshLayout refreshLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_board1,container,false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d("state", "OnCreate with sort : "+SORT);
        listView = view.findViewById(R.id.list);
        SORT = MainActivity.SORT;
        cmpGoal = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                if(o1.getPinned()==1 && o2.getPinned()==0){
                    return -1;
                }
                else if(o2.getPinned()==1&& o1.getPinned()==0){
                    return 1;
                }
                Date now = new Date();
                if((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))){
                    return 1;
                }else if((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())){
                    return -1;
                }
                int ret;
                float goal1 = ((float)o1.getParticipants()/o1.getGoal_participants());
                float goal2 = ((float)o2.getParticipants()/o2.getGoal_participants());
                if(goal1>goal2)
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
                if(o1.getPinned()==1 && o2.getPinned()==0){
                    return -1;
                }
                else if(o2.getPinned()==1&& o1.getPinned()==0){
                    return 1;
                }
                Date now = new Date();
                if((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))){
                    return 1;
                }else if((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())){
                    return -1;
                }
                int ret;
                Date date1 = o1.getDate();
                Date date2 = o2.getDate();
                int compare = date1.compareTo(date2);
                Log.d("datecomparing", date1+"   "+date2+"  "+compare);
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
                if(o1.getPinned()==1 && o2.getPinned()==0){
                    return -1;
                }
                else if(o2.getPinned()==1&& o1.getPinned()==0){
                    return 1;
                }
                int ret;
                Date now = new Date();
                if((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))){
                    return 1;
                }else if((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())){
                    return -1;
                }
                else {
                    Date date1 = o1.getDeadline();
                    Date date2 = o2.getDeadline();
                    int compare = date1.compareTo(date2);
                    if (compare < 0)
                        ret = -1; //date2>date1
                    else if (compare == 0)
                        ret = 0;
                    else
                        ret = 1;
                    return ret;
                }
            }
        };
        list = MainActivity.notreportedpostArrayList;

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post item = (Post) listViewAdapter.getItem(position);
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), PostDetailActivity.class);
                String postid = item.getID();
                getPost(postid, position);

            }
        });


        frag1newsortbutton = view.findViewById(R.id.fragment1_new_sort_button);
        frag1goalsortbutton = view.findViewById(R.id.fragment1_goal_sort_button);
        frag1datesortbutton = view.findViewById(R.id.fragment1_date_sort_button);

        TextView writeButton = view.findViewById(R.id.writeButton);
        writeButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), WriteActivity.class);
                        intent.putExtra("purpose", WRITE_NEWPOST);
                        startActivityForResult(intent, WRITE_NEWPOST);
                    }
                }
        );

        frag1datesortbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SORT = DEADLINE;
                OnRefrech();
            }
        });
        frag1goalsortbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SORT = GOAL;
                OnRefrech();
            }
        });
        frag1newsortbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SORT=NEW;
                OnRefrech();
            }
        });
        refreshLayout = view.findViewById(R.id.refresh_boards);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    MainActivity.getPosts();

                    list = MainActivity.notreportedpostArrayList;
                    switch (SORT){
                        case NEW:
                            Collections.sort(list, cmpNew);
                            frag1newselect();
                            break;
                        case GOAL:
                            Collections.sort(list, cmpGoal);
                            frag1goalselect();
                            break;
                        case DEADLINE:
                            Collections.sort(list, cmpDeadline);
                            frag1dateselect();
                            break;
                        default:
                            break;
                    }

                    listViewAdapter = new ListViewAdapter(list);
                    listView.setAdapter(listViewAdapter);
                    adapter1.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter1);
                    adapter2.notifyDataSetChanged();
                    recyclerView2.setAdapter(adapter2);
                    adapter3.notifyDataSetChanged();
                    recyclerView3.setAdapter(adapter3);

                    Log.d("refreshing is", "finish");

                    refreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        listView = view.findViewById(R.id.list);
        SORT = MainActivity.SORT;
//        cmpGoal = new Comparator<Post>() {
//            @Override
//            public int compare(Post o1, Post o2) {
//                Date now = new Date();
//                if((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))){
//                    return 1;
//                }else if((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())){
//                    return -1;
//                }
//                int ret;
//                float goal1 = ((float)o1.getParticipants()/o1.getGoal_participants());
//                float goal2 = ((float)o2.getParticipants()/o2.getGoal_participants());
//                if(goal1>goal2)
//                    ret = -1;
//                else if(goal1==goal2)
//                    ret = 0;
//                else
//                    ret = 1;
//                return ret;
//            }
//        };
//        cmpNew = new Comparator<Post>() {
//            @Override
//            public int compare(Post o1, Post o2) {
//                Date now = new Date();
//                if((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))){
//                    return 1;
//                }else if((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())){
//                    return -1;
//                }
//                int ret;
//                Date date1 = o1.getDate();
//                Date date2 = o2.getDate();
//                int compare = date1.compareTo(date2);
//                if(compare>0)
//                    ret = -1; //date2<date1
//                else if(compare==0)
//                    ret = 0;
//                else
//                    ret = 1;
//                return ret;
//            }
//        };
//        cmpDeadline = new Comparator<Post>() {
//            @Override
//            public int compare(Post o1, Post o2) {
//                Date now = new Date();
//                if((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))){
//                    return 1;
//                }else if((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())){
//                    return -1;
//                }
//                else {
//                    int ret;
//                    Date date1 = o1.getDeadline();
//                    Date date2 = o2.getDeadline();
//                    int compare = date1.compareTo(date2);
//                    if (compare < 0)
//                        ret = -1; //date2>date1
//                    else if (compare == 0)
//                        ret = 0;
//                    else
//                        ret = 1;
//                    return ret;
//                }
//            }
//        };
        list = MainActivity.notreportedpostArrayList;
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
        Log.d("result code is", ""+requestCode+"   "+resultCode);
        getPersonalInfo();
        switch (requestCode) {
            case WRITE_NEWPOST:
                switch (resultCode) {
                    case NEWPOST:
                        Post post = data.getParcelableExtra("post");
                        list.add(post);
                        OnRefrech();
                        listViewAdapter.changeItem();
                        listView.setAdapter(listViewAdapter);
                        return;
                    default:
                        return;
                }
            case DO_SURVEY:
                switch (resultCode){
                    case(DONE):
                        int position = data.getIntExtra("position", -1);
                        int newParticipants = data.getIntExtra("participants", -1);
                        listViewAdapter.updateParticipants(position, newParticipants);
                        Post item = (Post) listViewAdapter.getItem(position);

                        listView.setAdapter(listViewAdapter);
                        Log.d("new participant", "new is: " + item.getParticipants());
                        return;
                    case(DELETE):
                        int pos = data.getIntExtra("position", -1);
                        if(pos!=-1) {
                            list.remove(pos);
                        }
                        OnRefrech();
                        listViewAdapter.changeItem();
                        listView.setAdapter(listViewAdapter);
                        return;
                    case(FIX_DONE):
                        int fix_pos = data.getIntExtra("position", -1);
                        if(fix_pos!=-1) {
                            Post newpost = data.getParcelableExtra("post");
                            list.set(fix_pos, newpost);
                        }
                        OnRefrech();
                        listViewAdapter.changeItem();
                        listView.setAdapter(listViewAdapter);
                    case(REPORTED):
                        int report_pos = data.getIntExtra("position", -1);
                        if(report_pos!=-1) {
                            list.remove(report_pos);
                        }
                        OnRefrech();
                        listViewAdapter.changeItem();
                        listView.setAdapter(listViewAdapter);
                    default:
                        list = MainActivity.notreportedpostArrayList;
                        OnRefrech();
                        listViewAdapter.changeItem();
                        listView.setAdapter(listViewAdapter);
                        return;
                }
            default:
                return;
        }

    }
    private void getPersonalInfo() {
        if (UserPersonalInfo.token == null) {
            return;
        }
        String token = UserPersonalInfo.token;
        try{
            String requestURL = getString(R.string.server) + "/personalinfo";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            Log.d("response is", ""+response);
                            JSONObject user = res.getJSONObject("data");
                            UserPersonalInfo.name = user.getString("name");
                            UserPersonalInfo.email = user.getString("email");
                            UserPersonalInfo.points = user.getInt("points");
                            UserPersonalInfo.level = user.getInt("level");
                            UserPersonalInfo.userID = user.getString("userID");
                            UserPersonalInfo.userPassword = user.getString("userPassword");
                            UserPersonalInfo.gender = user.getInt("gender");
                            UserPersonalInfo.yearBirth = user.getInt("yearBirth");
                            UserPersonalInfo.phoneNumber = user.getString("phoneNumber");
                            JSONArray ja = (JSONArray)user.get("participations");

                            ArrayList<String> partiarray = new ArrayList<String>();
                            for (int j = 0; j<ja.length(); j++){
                                partiarray.add(ja.getString(j));
                            }

                            UserPersonalInfo.participations = partiarray;
                            Log.d("partiarray", ""+UserPersonalInfo.participations.toString());

                            JSONArray ja2 = (JSONArray)user.get("prizes");
                            ArrayList<String> prizearray = new ArrayList<String>();
                            for (int j = 0; j<ja2.length(); j++){
                                prizearray.add(ja2.getString(j));
                            }
                            UserPersonalInfo.prizes = prizearray;
                            Log.d("prizearray", ""+UserPersonalInfo.prizes.toString());


                            SharedPreferences auto = getActivity().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoLogin = auto.edit();
                            autoLogin.putString("name", user.getString("name"));
                            autoLogin.commit();
                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }

    public void OnRefrech(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (SORT){
                    case NEW:
                        Collections.sort(list, cmpNew);
                        frag1newselect();
                        break;
                    case GOAL:
                        Collections.sort(list, cmpGoal);
                        frag1goalselect();
                        break;
                    case DEADLINE:
                        Collections.sort(list, cmpDeadline);
                        frag1dateselect();
                        break;
                    default:
                        break;
                }
                listViewAdapter.notifyDataSetChanged();
                listView.setAdapter(listViewAdapter);
            }
        },300);
    }

    public void frag1dateselect(){
        frag1goalsortbutton.setBackgroundResource(R.drawable.ic_tabnoselect);
        frag1datesortbutton.setBackgroundResource(R.drawable.ic_tabselect);
        frag1newsortbutton.setBackgroundResource(R.drawable.ic_tabnoselect);
        frag1goalsortbutton.setTextColor(Color.parseColor("#BDBDBD"));
        frag1datesortbutton.setTextColor(Color.parseColor("#FFFFFF"));
        frag1newsortbutton.setTextColor(Color.parseColor("#BDBDBD"));
    }

    public void frag1newselect(){
        frag1goalsortbutton.setBackgroundResource(R.drawable.ic_tabnoselect);
        frag1datesortbutton.setBackgroundResource(R.drawable.ic_tabnoselect);
        frag1newsortbutton.setBackgroundResource(R.drawable.ic_tabselect);
        frag1goalsortbutton.setTextColor(Color.parseColor("#BDBDBD"));
        frag1datesortbutton.setTextColor(Color.parseColor("#BDBDBD"));
        frag1newsortbutton.setTextColor(Color.parseColor("#FFFFFF"));
    }

    public void frag1goalselect(){
        frag1goalsortbutton.setBackgroundResource(R.drawable.ic_tabselect);
        frag1datesortbutton.setBackgroundResource(R.drawable.ic_tabnoselect);
        frag1newsortbutton.setBackgroundResource(R.drawable.ic_tabnoselect);
        frag1goalsortbutton.setTextColor(Color.parseColor("#FFFFFF"));
        frag1datesortbutton.setTextColor(Color.parseColor("#BDBDBD"));
        frag1newsortbutton.setTextColor(Color.parseColor("#BDBDBD"));
    }


    private void getPost(String id, int position) {
        try{
            String requestURL = getString(R.string.server) + "/api/posts/getpost/"+ id;
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            Log.d("response is", "post"+response);
                            String post_id = res.getString("_id");
                            String title = res.getString("title");
                            String author = res.getString("author");
                            Integer author_lvl = res.getInt("author_lvl");
                            String content = res.getString("content");
                            Integer participants = res.getInt("participants");
                            Integer goal_participants = res.getInt("goal_participants");
                            String url = res.getString("url");
                            SimpleDateFormat fm = new SimpleDateFormat(getActivity().getApplicationContext().getString(R.string.date_format));
                            Date date = null;
                            try {
                                date = fm.parse(res.getString("date"));
                                Log.d("parsing date", "success");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date deadline = null;
                            try {
                                deadline = fm.parse(res.getString("deadline"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Boolean with_prize = res.getBoolean("with_prize");
                            String prize = "none";
                            Integer count= 0;
                            Integer est_time = res.getInt("est_time");
                            String target = res.getString("target");
                            Boolean done = res.getBoolean("done");
                            Boolean hide = res.getBoolean("hide");
                            Integer extended = res.getInt("extended");
                            String author_userid = res.getString("author_userid");
                            ArrayList<String> prize_urls = null;
                            if(with_prize) {
                                prize = res.getString("prize");
                                count = res.getInt("num_prize");
                                JSONArray pa = (JSONArray) res.get("prize_urls");
                                prize_urls = new ArrayList<String>();
                                for (int j = 0; j<pa.length(); j++){
                                    prize_urls.add(pa.getString(j));
                                }
                            }
                            JSONArray ia = (JSONArray)res.get("participants_userids");

                            ArrayList<String> participants_userids = new ArrayList<String>();
                            for (int j = 0; j<ia.length(); j++){
                                participants_userids.add(ia.getString(j));
                            }

                            JSONArray ka = (JSONArray)res.get("reports");

                            ArrayList<String> reports = new ArrayList<String>();
                            for (int j = 0; j<ka.length(); j++){
                                reports.add(ka.getString(j));
                            }

                            ArrayList<Reply> comments = new ArrayList<>();
                            try{
                                JSONArray ja = (JSONArray)res.get("comments");
                                if (ja.length() != 0){
                                    for (int j = 0; j<ja.length(); j++){
                                        JSONObject reply = ja.getJSONObject(j);
                                        String reid = reply.getString("_id");
                                        String writer = reply.getString("writer");
                                        String contetn = reply.getString("content");
                                        Date datereply = null;
                                        try {
                                            datereply = fm.parse(reply.getString("date"));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        Boolean replyhide = reply.getBoolean("hide");
                                        JSONArray ua = (JSONArray)reply.get("reports");

                                        ArrayList<String> replyreports = new ArrayList<String>();
                                        for (int u = 0; u<ua.length(); u++){
                                            replyreports.add(ua.getString(u));
                                            Log.d("reported by", "him"+ua.getString(u));
                                        }
                                        Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide);
                                        Log.d("report ", "replry report" + !replyhide + !replyreports.contains(UserPersonalInfo.userID));
                                        if (!replyhide && !replyreports.contains(UserPersonalInfo.userID)){
                                            comments.add(re);
                                        }
                                    }
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                                Log.d("parsing date", "non reply");
                            }
                            Integer pinned = res.getInt("pinned");

                            Post post = new Post(post_id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, count,comments,done, extended, participants_userids, reports, hide, author_userid);
                            post.setPinned(pinned);
                            if(with_prize) post.setPrize_urls(prize_urls);
                            Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), PostDetailActivity.class);
                            intent.putExtra("post", post);
                            intent.putParcelableArrayListExtra("reply", post.getComments());
                            intent.putExtra("position", position);
                            startActivityForResult(intent, DO_SURVEY);


                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
}
