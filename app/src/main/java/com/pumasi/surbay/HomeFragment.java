package com.pumasi.surbay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pumasi.surbay.adapter.BannerViewPagerAdapter;
import com.pumasi.surbay.adapter.RecyclerViewDdayAdapter;
import com.pumasi.surbay.adapter.RecyclerViewGoalAdapter;
import com.pumasi.surbay.adapter.RecyclerViewNewAdapter;
import com.pumasi.surbay.adapter.RecyclerViewNoticeAdapter;
import com.pumasi.surbay.classfile.Notice;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.pumasi.surbay.BoardFragment1.frag1datesortbutton;
import static com.pumasi.surbay.BoardFragment1.frag1goalsortbutton;
import static com.pumasi.surbay.BoardFragment1.frag1newsortbutton;
import static com.pumasi.surbay.BoardFragment1.listView;
import static com.pumasi.surbay.BoardFragment1.listViewAdapter;

public class HomeFragment extends Fragment // Fragment 클래스를 상속받아야한다
{
    private static final int NOTICE = 3;
    private static int DO_SURVEY = 2;
    private static final int NEW = 1;
    private static final int GOAL = 2;
    private static final int DEADLINE= 3;

    private static View view;
    public static RecyclerView recyclerView;
    public static RecyclerView recyclerView2;
    public static RecyclerView recyclerView3;
    private static RecyclerView recyclerView4;
    public static RecyclerViewDdayAdapter adapter1;
    public static RecyclerViewGoalAdapter adapter2;
    public static RecyclerViewNewAdapter adapter3;
    private static RecyclerViewNoticeAdapter adapter4;
    private static Context mContext;

    private static ImageButton date_sort_button;
    private static ImageButton goal_sort_button;
    private static ImageButton new_sort_button;
    private static ImageButton notice_sort_button;
    private static TextView main_notice;

    public static ArrayList<Post> list1;
    public static ArrayList<Post> list2;
    public static ArrayList<Post> list3;
    public static ArrayList<Notice> list4;
    private static Comparator<Post> cmpDeadline;
    private static Comparator<Post> cmpNew;
    private static Comparator<Post> cmpGoal;

    private static ViewPager banner;
    private static BannerViewPagerAdapter adapter;
    private static final Integer[] IMAGES= {R.drawable.tutorialbanner1, R.drawable.tutorialbanner2};
    private ArrayList<Integer> ImagesArray = new ArrayList<>();
    private int currentPage = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_home,container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        recyclerView = view.findViewById(R.id.recycler1);
        recyclerView2 = view.findViewById(R.id.recycler2);
        recyclerView3 = view.findViewById(R.id.recycler3);
        recyclerView4 = view.findViewById(R.id.recycler4);
        main_notice = view.findViewById(R.id.home_notice);

        date_sort_button = view.findViewById(R.id.date_sort_button);
        goal_sort_button = view.findViewById(R.id.goal_sort_button);
        new_sort_button = view.findViewById(R.id.new_sort_button);
        notice_sort_button = view.findViewById(R.id.notice_sort_button);

        cmpGoal = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                int ret;
                Date now = new Date();
                if((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))){
                    Log.d("comparing", ""+o1.getTitle() + o2.getTitle());
                    return 1;
                }else if((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())){
                    Log.d("comparing2", ""+o1.getTitle() + o2.getTitle());
                    return -1;
                }
                else {
                    float goal1 = ((float) o1.getParticipants() / o1.getGoal_participants());
                    float goal2 = ((float) o2.getParticipants() / o2.getGoal_participants());
                    if (goal1 > goal2)
                        ret = -1;
                    else if (goal1 == goal2)
                        ret = 0;
                    else
                        ret = 1;
                    return ret;
                }
            }
        };
        cmpNew = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                int ret;
                Date now = new Date();
                if((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))){
                    Log.d("comparing", ""+o1.getTitle() + o2.getTitle());
                    return 1;
                }else if((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())){
                    Log.d("comparing2", ""+o1.getTitle() + o2.getTitle());
                    return -1;
                }
                else {
                    Date date1 = o1.getDate();
                    Date date2 = o2.getDate();
                    int compare = date1.compareTo(date2);
                    if (compare > 0)
                        ret = -1; //date2<date1
                    else if (compare == 0)
                        ret = 0;
                    else
                        ret = 1;
                    return ret;
                }
            }
        };
        cmpDeadline = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                int ret;
                Date now = new Date();
                if((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))){
                    Log.d("comparing", ""+o1.getTitle() + o2.getTitle());
                    return 1;
                }else if((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())){
                    Log.d("comparing2", ""+o1.getTitle() + o2.getTitle());
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

        list1 = new ArrayList<>(MainActivity.postArrayList);
        list2 = new ArrayList<>(MainActivity.postArrayList);
        list3 = new ArrayList<>(MainActivity.postArrayList);
        list4 = new ArrayList<>(MainActivity.NoticeArrayList);
        Collections.sort(list1, cmpDeadline);
        Collections.sort(list2, cmpGoal);
        Collections.sort(list3, cmpNew);

        adapter4 = new RecyclerViewNoticeAdapter(mContext, list4);
        adapter1 = new RecyclerViewDdayAdapter(mContext, list1);
        adapter2 = new RecyclerViewGoalAdapter(mContext, list2);
        adapter3 = new RecyclerViewNewAdapter(mContext, list3);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter1);
        recyclerView2.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView3.setAdapter(adapter3);
        recyclerView4.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView4.setAdapter(adapter4);
        adapter1.setOnItemClickListener(new RecyclerViewDdayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter1.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putParcelableArrayListExtra("reply", item.getComments());
                intent.putExtra("position", position);
                startActivityForResult(intent, DO_SURVEY);
            }
        });
        adapter2.setOnItemClickListener(new RecyclerViewGoalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter2.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putParcelableArrayListExtra("reply", item.getComments());
                intent.putExtra("position", position);
                startActivityForResult(intent, DO_SURVEY);
            }
        });
        adapter3.setOnItemClickListener(new RecyclerViewNewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter3.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putParcelableArrayListExtra("reply", item.getComments());
                intent.putExtra("position", position);
                startActivityForResult(intent, DO_SURVEY);
            }
        });
        adapter4.setOnItemClickListener(new RecyclerViewNoticeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Notice item = (Notice) adapter4.getItem(position);
                Intent intent = new Intent(mContext, NoticeDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                startActivityForResult(intent, DO_SURVEY);
            }
        });

        date_sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnRefrech(DEADLINE);
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavi);
                bottomNavigationView.setSelectedItemId(R.id.action_boards);
            }
        });
        goal_sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnRefrech(GOAL);
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavi);
                bottomNavigationView.setSelectedItemId(R.id.action_boards);
            }
        });
        new_sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnRefrech(NEW);
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavi);
                bottomNavigationView.setSelectedItemId(R.id.action_boards);
            } 
        });
        notice_sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, noticeActivity.class);
                startActivityForResult(intent, NOTICE);
            }
        });

        setBanner();

        if (MainActivity.NoticeArrayList.size() != 0){
            main_notice.setText(MainActivity.NoticeArrayList.get(0).getContent());
            main_notice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Notice item = (Notice)MainActivity.NoticeArrayList.get(0);
                    Intent intent = new Intent(mContext, NoticeDetailActivity.class);
                    intent.putExtra("post", item);
                    intent.putExtra("position", 0);
                    ((Activity)mContext).startActivityForResult(intent, DO_SURVEY);
                }
            });
        }

        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){

    }
    public static void receivedPosts(){
        makeView();
    }
    private static void makeView(){
        list1 = new ArrayList<>(MainActivity.postArrayList);
        list2 = new ArrayList<>(MainActivity.postArrayList);
        list3 = new ArrayList<>(MainActivity.postArrayList);
        list4 = new ArrayList<>(MainActivity.NoticeArrayList);
        Collections.sort(list1, cmpDeadline);
        Collections.sort(list2, cmpGoal);
        Collections.sort(list3, cmpNew);
        adapter4 = new RecyclerViewNoticeAdapter(mContext, list4);
        adapter1 = new RecyclerViewDdayAdapter(mContext, list1);
        adapter2 = new RecyclerViewGoalAdapter(mContext, list2);
        adapter3 = new RecyclerViewNewAdapter(mContext, list3);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter1);
        recyclerView2.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView3.setAdapter(adapter3);
        recyclerView4.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView4.setAdapter(adapter4);
        adapter1.setOnItemClickListener(new RecyclerViewDdayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter1.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                ((Activity)mContext).startActivityForResult(intent, DO_SURVEY);
            }
        });
        adapter2.setOnItemClickListener(new RecyclerViewGoalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter2.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                ((Activity)mContext).startActivityForResult(intent, DO_SURVEY);
            }
        });
        adapter3.setOnItemClickListener(new RecyclerViewNewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter3.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                ((Activity)mContext).startActivityForResult(intent, DO_SURVEY);
            }
        });
        adapter4.setOnItemClickListener(new RecyclerViewNoticeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Notice item = (Notice) adapter4.getItem(position);
                Intent intent = new Intent(mContext, NoticeDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                ((Activity)mContext).startActivityForResult(intent, DO_SURVEY);
            }
        });

        if (MainActivity.NoticeArrayList.size() != 0){
            main_notice.setText(MainActivity.NoticeArrayList.get(0).getContent());
            main_notice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Notice item = (Notice)MainActivity.NoticeArrayList.get(0);
                    Intent intent = new Intent(mContext, NoticeDetailActivity.class);
                    intent.putExtra("post", item);
                    intent.putExtra("position", 0);
                    ((Activity)mContext).startActivityForResult(intent, DO_SURVEY);
                }
            });
        }
        view.setVisibility(View.VISIBLE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public void OnRefrech(int sort){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (sort){
                    case NEW:
                        Collections.sort(BoardFragment1.list, cmpNew);
                        frag1newselect();
                        break;
                    case GOAL:
                        Collections.sort(BoardFragment1.list, cmpGoal);
                        frag1goalselect();
                        break;
                    case DEADLINE:
                        Collections.sort(BoardFragment1.list, cmpDeadline);
                        frag1dateselect();
                        break;
                    default:
                        break;
                }
                listViewAdapter.notifyDataSetChanged();
                listView.setAdapter(listViewAdapter);
            }
        },100);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            MainActivity.getPosts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getPersonalInfo();
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


                            if(getActivity()!=null) {
                                SharedPreferences auto = getActivity().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor autoLogin = auto.edit();
                                autoLogin.putString("name", user.getString("name"));
                                autoLogin.commit();
                            }
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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }

    private void setBanner(){
        banner = view.findViewById(R.id.banner);
        for(int i=0;i<IMAGES.length;i++){
            ImagesArray.add(IMAGES[i]);
        }
        adapter = new BannerViewPagerAdapter(mContext, ImagesArray);
        banner.setAdapter(adapter);



        banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        int NUM_PAGES = IMAGES.length;

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                banner.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 30000, 30000);
    }
}