package com.pumasi.surbay.pages.mypage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.MainActivity;
import com.pumasi.surbay.PostDetailActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.RecyclerViewMakeAdapter;
import com.pumasi.surbay.adapter.RecyclerViewPartiAdapter;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.classfile.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MypageFragment extends Fragment // Fragment 클래스를 상속받아야한다
{


    private Integer DO_SURVEY = 2;

    private static View view;
    private static RecyclerView recyclerView_make;
    private static RecyclerView recyclerView_parti;
    private static RecyclerViewMakeAdapter adapter_make;
    private static RecyclerViewPartiAdapter adapter_parti;
    private static Context mContext;

    private static ArrayList<Post> list_make;
    private static ArrayList<Post> list_parti;

    private static ImageButton i_make_button;
    private static ImageButton i_parti_button;
    private static ImageButton i_get_button;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    mypageRefreshHandler handler = new mypageRefreshHandler();

    TextView nameview;
    ImageView profileview;
    TextView levelview;
    ImageView setting;
    ImageView bell;

    ImageView none_tiger_make;
    ImageView none_tiger_parti;

    TextView upload_2nd;
    TextView parti_2nd;
    TextView can_sur;
    TextView willdo_sur;

    TextView get_2nd;

    TextView parti_3rd;
    private boolean getPersonalInfoDone = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mContext = getActivity();
        view = inflater.inflate(R.layout.fragment_mypage,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        recyclerView_make = view.findViewById(R.id.recycler_mypage_make);
        recyclerView_parti = view.findViewById(R.id.recycler_mypage_partic);

        i_make_button = view.findViewById(R.id.mypage_i_make);
        i_parti_button = view.findViewById(R.id.mypage_i_parti);
        i_get_button = view.findViewById(R.id.mypage_i_get);

        setting = view.findViewById(R.id.mypage_setting);
        bell = view.findViewById(R.id.mypage_bell);

        none_tiger_make = view.findViewById(R.id.none_tiger_make);
        none_tiger_parti = view.findViewById(R.id.none_tiger_parti);

        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), NotificationsActivity.class);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), MypageSettingMain.class);
                startActivity(intent);
            }
        });
        getlistofI();

        Log.d("listmake size is", ""+list_make.size());

        adapter_make = new RecyclerViewMakeAdapter(mContext, list_make);
        adapter_parti = new RecyclerViewPartiAdapter(mContext, list_parti);
        if(MainActivity.notreportedpostArrayList.size()==0) {
            view.setVisibility(View.INVISIBLE);
        }
        else{
            recyclerView_make.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView_make.setAdapter(adapter_make);
            recyclerView_parti.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView_parti.setAdapter(adapter_parti);

        }
        if(list_make.size()==0){
            recyclerView_make.setVisibility(View.GONE);
            none_tiger_make.setVisibility(View.VISIBLE);
        }
        else{
            Log.d("havemake", ""+list_make.size());
            recyclerView_make.setVisibility(View.VISIBLE);
            none_tiger_make.setVisibility(View.GONE);
        }
        if(list_parti.size()==0){
            recyclerView_parti.setVisibility(View.GONE);
            none_tiger_parti.setVisibility(View.VISIBLE);
        }else{
            Log.d("haveparti", ""+list_parti.size());
            recyclerView_parti.setVisibility(View.VISIBLE);
            none_tiger_parti.setVisibility(View.GONE);
        }
        adapter_make.setOnItemClickListener(new RecyclerViewMakeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter_make.getItem(position);
                Log.d("on click", "title is "+item.getTitle());
                getPost(item.getID(), position);

            }
        });
        adapter_parti.setOnItemClickListener(new RecyclerViewPartiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter_parti.getItem(position);
                Log.d("on click parti", "title is "+item.getTitle());
                getPost(item.getID(), position);
            }
        });
        i_make_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), Mypage_uploadNParti.class);
                intent.putExtra("what", 0);
                intent.putExtra("list", list_make);
                startActivity(intent);
            }
        });
        i_parti_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), Mypage_uploadNParti.class);
                intent.putExtra("what", 1);
                intent.putExtra("list", list_parti);
                startActivity(intent);
            }
        });
        i_get_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), IGotGifts.class);
                startActivity(intent);
            }
        });


        nameview = view.findViewById(R.id.mypage_myname);
        profileview = view.findViewById(R.id.mypage_profile_image);
        levelview = view.findViewById(R.id.user_level);
        upload_2nd = view.findViewById(R.id.upload_2nd);
        parti_2nd = view.findViewById(R.id.parti_2nd);
        can_sur = view.findViewById(R.id.mypage_can_sur);

        get_2nd = view.findViewById(R.id.get_2nd);

        parti_3rd = view.findViewById(R.id.parti_3rd);

        nameview.setText(UserPersonalInfo.name);
        levelview.setText("레벨 " + UserPersonalInfo.level.toString());
        upload_2nd.setText(list_make.size() + "개");
        parti_2nd.setText(list_parti.size() + "개");
        parti_3rd.setText("에 참여했네요");
        if(UserPersonalInfo.prize_check<UserPersonalInfo.prizes.size()){
            get_2nd.setVisibility(View.VISIBLE);
        }else{
            get_2nd.setVisibility(View.GONE);
        }

        String[] adminsList = {"SurBay_Admin", "SurBay_dev", "SurBay_dev2", "SurBay_des", "djrobort", "surbaying"};
        ArrayList<String> admins = new ArrayList<>(Arrays.asList(adminsList));
        if(admins.contains(UserPersonalInfo.userID)){
            profileview.setImageResource(R.drawable.surbay_logo_transparent);
            none_tiger_make.setImageResource(R.drawable.no_selection_surbay_lv1);
            none_tiger_parti.setImageResource(R.drawable.no_selection_surbay_lv1);
        }
        else{
            profileview.setImageResource(R.drawable.surbay_character_lv1);
            none_tiger_make.setImageResource(R.drawable.no_selection_surbay_lv1);
            none_tiger_parti.setImageResource(R.drawable.no_selection_surbay_lv1);
        }


        can_sur.setText(UserPersonalInfo.points+"크레딧");


        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mypage_swipe_contatiner);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BackgroundThread refreshThread = new BackgroundThread();
                refreshThread.start();

            }
        });
        

        return view;                     
    }
    class BackgroundThread extends Thread{
        public void run() {
            try {
                getPersonalInfo();
            } catch (Exception e) {
                e.printStackTrace();
                getPersonalInfoDone = true;
            }
            while(!(getPersonalInfoDone)) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            Message message = handler.obtainMessage();
            handler.sendMessage(message);
        }
    }



    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mSwipeRefreshLayout!=null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
    }

    public void getlistofI(){
        String userid = UserPersonalInfo.userID;
        list_make = new ArrayList<Post>();
        list_parti = new ArrayList<Post>();
        try {
            for (Post post : MainActivity.notreportedpostArrayList){
                if (post.getAuthor_userid().equals(userid)){
                    list_make.add(post);
                } else if (UserPersonalInfo.participations.contains(post.getID()) && !post.getAuthor_userid().equals(userid)) {
                    list_parti.add(post);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void getPost(String id, int position) {
        try{
            String requestURL = getString(R.string.server) + "/api/posts/getpost/"+ id;
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
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
                                        }
                                        Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide);
                                        if (!replyhide && !replyreports.contains(UserPersonalInfo.userID)){
                                            comments.add(re);
                                        }
                                    }
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                                Log.d("parsing date", "non reply");
                            }
                            Integer pinned = 0;
                            Boolean annonymous = false;
                            String author_info = "";
                            try {
                                pinned = res.getInt("pinned");
                                annonymous = res.getBoolean("annonymous");
                                author_info = res.getString("author_info");
                            }catch (Exception e){

                            }
                            Post post = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, count,comments,done, extended, participants_userids, reports, hide, author_userid, pinned, annonymous, author_info);
                            if(with_prize) post.setPrize_urls(prize_urls);
                            Intent intent = new Intent(mContext, PostDetailActivity.class);
                            intent.putExtra("post", post);
                            intent.putExtra("position", position);
                            startActivity(intent);


                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }

    class mypageRefreshHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(getFragmentManager()!=null) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(MypageFragment.this).attach(MypageFragment.this).commit();
            }
            getPersonalInfoDone=false;
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
    private void getPersonalInfo() {
        if (UserPersonalInfo.token == null) {
            Log.d("getting info failed", "token is null");
            return;
        }
        String token = UserPersonalInfo.token;
        try{
            String requestURL = getString(R.string.server) + "/personalinfo";
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
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
                            UserPersonalInfo.prize_check = user.getInt("prize_check");



                            SharedPreferences auto = mContext.getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoLogin = auto.edit();
                            autoLogin.putString("name", user.getString("name"));
                            autoLogin.commit();
                            getPersonalInfoDone = true;
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

}