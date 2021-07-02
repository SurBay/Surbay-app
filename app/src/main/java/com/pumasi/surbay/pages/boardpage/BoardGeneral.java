package com.pumasi.surbay.pages.boardpage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.GeneralListViewAdapter;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.Reply;
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

import static com.pumasi.surbay.pages.homepage.HomeFragment.adapter1;
import static com.pumasi.surbay.pages.homepage.HomeFragment.adapter2;
import static com.pumasi.surbay.pages.homepage.HomeFragment.adapter3;
import static com.pumasi.surbay.pages.homepage.HomeFragment.recyclerView;
import static com.pumasi.surbay.pages.homepage.HomeFragment.recyclerView2;
import static com.pumasi.surbay.pages.homepage.HomeFragment.recyclerView3;


public class BoardGeneral extends Fragment// Fragment 클래스를 상속받아야한다
{ // 게시판

    private static final int NEW = 1;
    private static final int PARTI = 2;
    private static final int LIKE = 3;
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
    public static GeneralListViewAdapter generalListViewAdapter;
    public static ListView listView;

    public static ArrayList<General> list;

    private Comparator<General> cmpLike;
    private Comparator<General> cmpNew;
    private Comparator<General> cmpParti;

    public static Button frag2newsortbutton;
    public static ImageButton frag2likesortbutton;
    public static ImageButton frag2partisortbutton;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean getGeneralsDone = false;
    private BackgroundThread refreshThread;
    private refreshHandler handler = new refreshHandler();

    Parcelable state;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_board_general,container,false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d("state", "OnCreate with sort : "+SORT);
        listView = view.findViewById(R.id.list);
        SORT = MainActivity.SORT;
        cmpParti = new Comparator<General>() {
            @Override
            public int compare(General o1, General o2) {
                int ret;
                float goal1 = ((float)o1.getParticipants());
                float goal2 = ((float)o2.getParticipants());
                if(goal1>goal2)
                    ret = -1;
                else if(goal1==goal2)
                    ret = 0;
                else
                    ret = 1;
                return ret;
            }
        };
        cmpNew = new Comparator<General>() {
            @Override
            public int compare(General o1, General o2) {
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
        cmpLike = new Comparator<General>() {
            @Override
            public int compare(General o1, General o2) {
                int ret;
                float goal1 = ((float)o1.getLikes());
                float goal2 = ((float)o2.getLikes());
                if(goal1>goal2)
                    ret = -1;
                else if(goal1==goal2)
                    ret = 0;
                else
                    ret = 1;
                return ret;
            }
        };
        list = MainActivity.generalArrayList;

        switch (SORT){
            case NEW:
                Collections.sort(list, cmpNew);
                break;
            case PARTI:
                Collections.sort(list, cmpParti);
                break;
            case LIKE:
                Collections.sort(list, cmpLike);
                break;
            default:
                break;
        }

        generalListViewAdapter = new GeneralListViewAdapter(list);

        listView.setAdapter(generalListViewAdapter);
        if(state != null) {
            listView.onRestoreInstanceState(state);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(UserPersonalInfo.userID.equals("nonMember")){
                    CustomDialog customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 자유게시판을 이용하실 수 없습니다");
                    customDialog.setNegativeButton("확인");
                    return;
                }
                General general = (General) generalListViewAdapter.getItem(position);
                getGeneral(general.getID(), position);

            }
        });


        frag2newsortbutton = view.findViewById(R.id.fragment1_new_sort_button);
        frag2partisortbutton = view.findViewById(R.id.fragment1_goal_sort_button);
        frag2likesortbutton = view.findViewById(R.id.fragment1_date_sort_button);

        TextView writeButton = view.findViewById(R.id.writeButton);
        writeButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(UserPersonalInfo.userID.equals("nonMember")){
                            CustomDialog customDialog = new CustomDialog(getActivity(), null);
                            customDialog.show();
                            customDialog.setMessage("비회원은 글을 작성하실 수 없습니다");
                            customDialog.setNegativeButton("확인");
                            return;
                        }
                        Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), GeneralWriteActivity.class);
                        intent.putExtra("purpose", WRITE_NEWPOST);
                        startActivityForResult(intent, WRITE_NEWPOST);
                    }
                }
        );

        frag2likesortbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("loglog", "likesort");
                SORT = LIKE;
                changeSort();
            }
        });
        frag2partisortbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SORT = PARTI;
                changeSort();
            }
        });
        frag2newsortbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SORT=NEW;
                changeSort();
            }
        });
        mSwipeRefreshLayout = view.findViewById(R.id.refresh_boards);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshThread = new BackgroundThread();
                refreshThread.start();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        listView = view.findViewById(R.id.list);
        SORT = MainActivity.SORT;
        list = MainActivity.generalArrayList;
        switch (SORT){
            case NEW:
                Collections.sort(list, cmpNew);
                break;
            case PARTI:
                Collections.sort(list, cmpParti);
                break;
            case LIKE:
                Collections.sort(list, cmpLike);
                break;
            default:
                break;
        }

        generalListViewAdapter = new GeneralListViewAdapter(list);
        listView.setAdapter(generalListViewAdapter);
        if(state != null) {
            listView.onRestoreInstanceState(state);
        }
    }

    class BackgroundThread extends Thread {
        public void run() {
            getGenerals();
            while(!getGeneralsDone) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            Message message = handler.obtainMessage();
            handler.sendMessage(message);
        }
    }

    private class refreshHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            list = MainActivity.generalArrayList;
            switch (SORT){
                case NEW:
                    Collections.sort(list, cmpNew);
                    frag1newselect();
                    break;
                case PARTI:
                    Collections.sort(list, cmpParti);
                    frag1goalselect();
                    break;
                case LIKE:
                    Collections.sort(list, cmpLike);
                    frag1likeselect();
                    break;
                default:
                    break;
            }

            generalListViewAdapter = new GeneralListViewAdapter(list);
            listView.setAdapter(generalListViewAdapter);
            if(state != null) {
                listView.onRestoreInstanceState(state);
            }
            adapter1.notifyDataSetChanged();
            recyclerView.setAdapter(adapter1);
            adapter2.notifyDataSetChanged();
            recyclerView2.setAdapter(adapter2);
            adapter3.notifyDataSetChanged();
            recyclerView3.setAdapter(adapter3);

            Log.d("refreshing is", "finish");

            mSwipeRefreshLayout.setRefreshing(false);
            getGeneralsDone = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("result code is", ""+requestCode+"   "+resultCode);
//        mSwipeRefreshLayout.refreshDrawableState();
        getPersonalInfo();
        switch (requestCode) {
            case WRITE_NEWPOST:
                switch (resultCode) {
                    case NEWPOST:
                        Log.d("sizeisisisis", ""+MainActivity.generalArrayList.size());
                        list = MainActivity.generalArrayList;
                        generalListViewAdapter = new GeneralListViewAdapter(list);
                        listView.setAdapter(generalListViewAdapter);
                        if(state != null) {
                            listView.onRestoreInstanceState(state);
                        }
                        changeSort();
                        return;
                    default:
                        return;
                }
            case DO_SURVEY:
                int pos;
                switch (resultCode){
                    case LIKE:
                        pos= data.getIntExtra("position", -1);
                        General general = data.getParcelableExtra("general");
                        MainActivity.generalArrayList.set(pos, general);
                        MainActivity.getGenerals();
                        changeSort();
                        generalListViewAdapter.changeItem();
                        listView.setAdapter(generalListViewAdapter);
                        if(state != null) {
                            listView.onRestoreInstanceState(state);
                        }
                        break;
                    case(DELETE):
                        pos = data.getIntExtra("position", -1);
                        if(pos!=-1) {
                            list.remove(pos);
                        }
                        changeSort();
                        generalListViewAdapter.changeItem();
                        listView.setAdapter(generalListViewAdapter);
                        if(state != null) {
                            listView.onRestoreInstanceState(state);
                        }
                        return;
                    case(REPORTED):
                        int report_pos = data.getIntExtra("position", -1);
                        if(report_pos!=-1) {
                            list.remove(report_pos);
                        }
                        changeSort();
                        generalListViewAdapter.changeItem();
                        listView.setAdapter(generalListViewAdapter);
                        if(state != null) {
                            listView.onRestoreInstanceState(state);
                        }
                    case(DONE):
                        MainActivity.getGenerals();
                        changeSort();
                        generalListViewAdapter.changeItem();
                        listView.setAdapter(generalListViewAdapter);
                        if(state != null) {
                            listView.onRestoreInstanceState(state);
                        }
                        break;
                }
            default:
                return;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(mSwipeRefreshLayout!=null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeRefreshLayout!=null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
        state = listView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(refreshThread!=null) refreshThread.interrupt();
        if (mSwipeRefreshLayout!=null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
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

                            ArrayList<Notification> notifications = new ArrayList<>();
                            try{
                                SimpleDateFormat fm = new SimpleDateFormat(getString(R.string.date_format));
                                JSONArray na = (JSONArray)user.get("notifications");
                                if (na.length() != 0){
                                    for (int j = 0; j<na.length(); j++){
                                        JSONObject notification = na.getJSONObject(j);
                                        String title = notification.getString("title");
                                        String content = notification.getString("content");
                                        String post_id = notification.getString("post_id");
                                        Date date = null;
                                        try {
                                            date = fm.parse(notification.getString("date"));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        Integer post_type = notification.getInt("post_type");
                                        Notification newNotification = new Notification(title, content, post_id, date, post_type);
                                        notifications.add(newNotification);
                                    }
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            UserPersonalInfo.notifications = notifications;
                            UserPersonalInfo.notificationAllow = user.getBoolean("notification_allow");

                            UserPersonalInfo.participations = partiarray;

                            UserPersonalInfo.prize_check = user.getInt("prize_check");
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

    public void changeSort(){
        list = MainActivity.generalArrayList;
        Log.d("inchangesort", "sortis"+SORT);
        switch (SORT) {
            case NEW:
                Collections.sort(list, cmpNew);
                frag1newselect();
                break;
            case PARTI:
                Collections.sort(list, cmpParti);
                frag1goalselect();
                break;
            case LIKE:
                Collections.sort(list, cmpLike);
                frag1likeselect();
                break;
            default:
                break;
        }
        generalListViewAdapter = new GeneralListViewAdapter(list);

        listView.setAdapter(generalListViewAdapter);
        if(state != null) {
            listView.onRestoreInstanceState(state);
        }
    }

    public void frag1likeselect(){
        frag2partisortbutton.setBackgroundResource(R.drawable.ic_tabnoselect);
        frag2likesortbutton.setBackgroundResource(R.drawable.ic_tabselect);
        frag2newsortbutton.setBackgroundResource(R.drawable.ic_tabnoselect);
        frag2partisortbutton.setColorFilter(Color.parseColor("#BDBDBD"));
        frag2likesortbutton.setColorFilter(Color.parseColor("#FFFFFF"));
        frag2newsortbutton.setTextColor(Color.parseColor("#BDBDBD"));
    }

    public void frag1newselect(){
        frag2partisortbutton.setBackgroundResource(R.drawable.ic_tabnoselect);
        frag2likesortbutton.setBackgroundResource(R.drawable.ic_tabnoselect);
        frag2newsortbutton.setBackgroundResource(R.drawable.ic_tabselect);
        frag2partisortbutton.setColorFilter(Color.parseColor("#BDBDBD"));
        frag2likesortbutton.setColorFilter(Color.parseColor("#BDBDBD"));
        frag2newsortbutton.setTextColor(Color.parseColor("#FFFFFF"));
    }

    public void frag1goalselect(){
        frag2partisortbutton.setBackgroundResource(R.drawable.ic_tabselect);
        frag2likesortbutton.setBackgroundResource(R.drawable.ic_tabnoselect);
        frag2newsortbutton.setBackgroundResource(R.drawable.ic_tabnoselect);
        frag2partisortbutton.setColorFilter(Color.parseColor("#FFFFFF"));
        frag2likesortbutton.setColorFilter(Color.parseColor("#BDBDBD"));
        frag2newsortbutton.setTextColor(Color.parseColor("#BDBDBD"));
    }

    private void getGenerals(){
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/generals";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            ArrayList<General> generalArrayList = new ArrayList<>();
                            JSONArray resultArr = new JSONArray(response.toString());
                            Log.d("gettinggeneralresponseis", ""+response);
                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject general = resultArr.getJSONObject(i);
                                String id = general.getString("_id");
                                String title = general.getString("title");
                                String author = general.getString("author");
                                Integer author_lvl = general.getInt("author_lvl");
                                String content = general.getString("content");
                                SimpleDateFormat fm = new SimpleDateFormat(getActivity().getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(general.getString("date"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Date deadline = null;
                                try {
                                    deadline = fm.parse(general.getString("deadline"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                ArrayList<Reply> comments = new ArrayList<>();
                                try{
                                    JSONArray ja = (JSONArray)general.get("comments");
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
                                            }
                                            String writer_name = null;
                                            try {
                                                writer_name = reply.getString("writer_name");
                                            }catch (Exception e){
                                                writer_name = null;
                                            }
                                            Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide);
                                            re.setWriter_name(writer_name);
                                            if ((!replyhide )&& (!replyreports.contains(UserPersonalInfo.userID))){
                                                comments.add(re);
                                            }
                                        }
                                    }

                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                Boolean done = general.getBoolean("done");
                                String author_userid = general.getString("author_userid");
                                JSONArray ka = (JSONArray)general.get("reports");
                                ArrayList<String> reports = new ArrayList<String>();
                                for (int j = 0; j<ka.length(); j++){
                                    reports.add(ka.getString(j));
                                }
                                Boolean multi_response = general.getBoolean("multi_response");
                                Integer participants = general.getInt("participants");
                                JSONArray ia = (JSONArray)general.get("participants_userids");
                                ArrayList<String> participants_userids = new ArrayList<String>();
                                for (int j = 0; j<ia.length(); j++){
                                    participants_userids.add(ia.getString(j));
                                }
                                Boolean with_image = general.getBoolean("with_image");
                                ArrayList<Poll> polls = new ArrayList<>();
                                try{
                                    JSONArray ja = (JSONArray)general.get("polls");
                                    if (ja.length() != 0){
                                        for (int j = 0; j<ja.length(); j++){
                                            JSONObject poll = ja.getJSONObject(j);
                                            String poll_id = poll.getString("_id");
                                            String poll_content = poll.getString("content");
                                            ArrayList<String> poll_participants_userids = new ArrayList<String>();
                                            JSONArray ua = (JSONArray)poll.get("participants_userids");
                                            for (int u = 0; u<ua.length(); u++){
                                                poll_participants_userids.add(ua.getString(u));
                                            }
                                            String image = poll.getString("image");
                                            Poll newpoll = new Poll(poll_id, poll_content, poll_participants_userids, image);
                                            polls.add(newpoll);
                                        }
                                    }

                                } catch (Exception e){
                                    e.printStackTrace();
                                }

                                JSONArray la = (JSONArray)general.get("liked_users");
                                ArrayList<String> liked_users = new ArrayList<String>();
                                for (int j = 0; j<la.length(); j++){
                                    liked_users.add(la.getString(j));
                                }

                                Integer likes = general.getInt("likes");
                                Boolean hide = general.getBoolean("hide");

                                General newGeneral = new General(id, title, author, author_lvl, content,
                                        date, deadline, comments, done, author_userid, reports, multi_response,
                                        participants, participants_userids, with_image, polls, liked_users, likes, hide);
                                if((!newGeneral.getReports().contains(UserPersonalInfo.userID)) && (hide!=true))
                                    generalArrayList.add(newGeneral);
                            }

                            MainActivity.generalArrayList = generalArrayList;
                            getGeneralsDone = true;
                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                        getGeneralsDone = true;
                    }, error -> {
                        getGeneralsDone = true;
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }

    private void getGeneral(String general_id, Integer position){
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/generals/getpost/"+general_id;
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject general = new JSONObject(response.toString());
                            String id = general.getString("_id");
                            String title = general.getString("title");
                            String author = general.getString("author");
                            Integer author_lvl = general.getInt("author_lvl");
                            String content = general.getString("content");
                            SimpleDateFormat fm = new SimpleDateFormat(getActivity().getString(R.string.date_format));
                            Date date = null;
                            try {
                                date = fm.parse(general.getString("date"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date deadline = null;
                            try {
                                deadline = fm.parse(general.getString("deadline"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            ArrayList<Reply> comments = new ArrayList<>();
                            try{
                                JSONArray ja = (JSONArray)general.get("comments");
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
                                        }
                                        String writer_name = null;
                                        try {
                                            writer_name = reply.getString("writer_name");
                                        }catch (Exception e){
                                            writer_name = null;
                                        }
                                        Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide);
                                        re.setWriter_name(writer_name);
                                        if ((!replyhide )&& (!replyreports.contains(UserPersonalInfo.userID))){
                                            comments.add(re);
                                        }
                                    }
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            Boolean done = general.getBoolean("done");
                            String author_userid = general.getString("author_userid");
                            JSONArray ka = (JSONArray)general.get("reports");
                            ArrayList<String> reports = new ArrayList<String>();
                            for (int j = 0; j<ka.length(); j++){
                                reports.add(ka.getString(j));
                            }
                            Boolean multi_response = general.getBoolean("multi_response");
                            Integer participants = general.getInt("participants");
                            JSONArray ia = (JSONArray)general.get("participants_userids");
                            ArrayList<String> participants_userids = new ArrayList<String>();
                            for (int j = 0; j<ia.length(); j++){
                                participants_userids.add(ia.getString(j));
                            }
                            Boolean with_image = general.getBoolean("with_image");
                            ArrayList<Poll> polls = new ArrayList<>();
                            try{
                                JSONArray ja = (JSONArray)general.get("polls");
                                if (ja.length() != 0){
                                    for (int j = 0; j<ja.length(); j++){
                                        JSONObject poll = ja.getJSONObject(j);
                                        String poll_id = poll.getString("_id");
                                        String poll_content = poll.getString("content");
                                        ArrayList<String> poll_participants_userids = new ArrayList<String>();
                                        JSONArray ua = (JSONArray)poll.get("participants_userids");
                                        for (int u = 0; u<ua.length(); u++){
                                            poll_participants_userids.add(ua.getString(u));
                                        }
                                        String image = poll.getString("image");
                                        Poll newpoll = new Poll(poll_id, poll_content, poll_participants_userids, image);
                                        polls.add(newpoll);
                                    }
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }

                            JSONArray la = (JSONArray)general.get("liked_users");
                            ArrayList<String> liked_users = new ArrayList<String>();
                            for (int j = 0; j<la.length(); j++){
                                liked_users.add(la.getString(j));
                            }

                            Integer likes = general.getInt("likes");
                            Boolean hide = general.getBoolean("hide");
                            General newGeneral = new General(id, title, author, author_lvl, content,
                                    date, deadline, comments, done, author_userid, reports, multi_response,
                                    participants, participants_userids, with_image, polls, liked_users, likes, hide);

                            Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), GeneralDetailActivity.class);
                            intent.putExtra("general", newGeneral);
                            intent.putExtra("position", position);
                            intent.putParcelableArrayListExtra("reply", newGeneral.getComments());
                            intent.putParcelableArrayListExtra("polls", newGeneral.getPolls());
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
