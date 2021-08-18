package com.pumasi.surbay.pages.homepage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.BannerViewPagerAdapter;
import com.pumasi.surbay.adapter.RecyclerViewDdayAdapter;
import com.pumasi.surbay.adapter.RecyclerViewGoalAdapter;
import com.pumasi.surbay.adapter.RecyclerViewNewAdapter;
import com.pumasi.surbay.adapter.RecyclerViewNoticeAdapter;
import com.pumasi.surbay.classfile.Notice;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.boardpage.BoardPost;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;
import com.pumasi.surbay.pages.homepage.NoticeActivity;
import com.pumasi.surbay.pages.homepage.NoticeDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.pumasi.surbay.pages.boardpage.BoardPost.frag1datesortbutton;
import static com.pumasi.surbay.pages.boardpage.BoardPost.frag1goalsortbutton;
import static com.pumasi.surbay.pages.boardpage.BoardPost.frag1newsortbutton;
import static com.pumasi.surbay.pages.boardpage.BoardPost.listView;
import static com.pumasi.surbay.pages.boardpage.BoardPost.postListViewAdapter;

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

    private ImageView tiger1;
    private ImageView tiger2;
    private ImageView tiger3;

    public static ArrayList<Post> list1;
    public static ArrayList<Post> list2;
    public static ArrayList<Post> list3;
    public static ArrayList<Notice> list4;
    private static Comparator<Post> cmpDeadline;
    private static Comparator<Post> cmpNew;
    private static Comparator<Post> cmpGoal;

    private static ViewPager banner;
    private BannerViewPagerAdapter adapter;
    private static final Integer[] IMAGES= {R.drawable.renewal_banner, R.drawable.tutorialbanner2};
    private ArrayList<Integer> ImagesArray = new ArrayList<>();
    private int currentPage = 0;

    private static Boolean getPostsDone = false;
    private static Boolean getNoticesDone = false;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    ValueHandler handler = new ValueHandler();
    private Comparator<Notice> cmpNoticeNew;

    private BackgroundThread refreshThread;
    private RelativeLayout loading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_home,container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        mContext = getActivity();

        recyclerView = view.findViewById(R.id.recycler1);
        recyclerView2 = view.findViewById(R.id.recycler2);
        recyclerView3 = view.findViewById(R.id.recycler3);
        recyclerView4 = view.findViewById(R.id.recycler4);
        main_notice = view.findViewById(R.id.home_notice);

        date_sort_button = view.findViewById(R.id.date_sort_button);
        goal_sort_button = view.findViewById(R.id.goal_sort_button);
        new_sort_button = view.findViewById(R.id.new_sort_button);
        notice_sort_button = view.findViewById(R.id.notice_sort_button);

        tiger1 = view.findViewById(R.id.none_tiger_1);
        tiger2 = view.findViewById(R.id.none_tiger_2);
        tiger3 = view.findViewById(R.id.none_tiger_3);

        loading = view.findViewById(R.id.loadingPanel);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.home_swipe_container);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshThread = new BackgroundThread();
                refreshThread.start();
            }
        });

        try {
            setRecyclerView();
//            setBanner();
        }catch (Exception e){
            e.printStackTrace();
        }



        return view;
    }

    class BackgroundThread extends Thread {
        public void run() {
            try {
                getPosts();
            } catch (Exception e) {
                e.printStackTrace();
                getPostsDone = true;
            }
            try {
                getNotices();
            } catch (Exception e) {
                e.printStackTrace();
                getNoticesDone = true;
            }
            while(!(getNoticesDone&&getPostsDone)) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            Message message = handler.obtainMessage();
            handler.sendMessage(message);
        }
    }

    class ValueHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("gotmessage", "goetmessage");
            getNoticesDone = false;
            getPostsDone = false;
            setRecyclerView();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setRecyclerView(){
        String[] adminsList = {"SurBay_Admin", "SurBay_dev", "SurBay_dev2", "SurBay_des", "djrobort", "surbaying"};
        ArrayList<String> admins = new ArrayList<>(Arrays.asList(adminsList));
        tiger1.setImageResource(R.drawable.no_selection_surbay_lv1);
        tiger2.setImageResource(R.drawable.no_selection_surbay_lv1);
        tiger3.setImageResource(R.drawable.no_selection_surbay_lv1);
        tiger1.setVisibility(View.GONE);
        tiger2.setVisibility(View.GONE);
        tiger3.setVisibility(View.GONE);
        cmpGoal = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                int ret;
                Date now = new Date();
                if((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))){
                    return 1;
                }else if((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())){
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
                    return 1;
                }else if((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())){
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
        cmpNoticeNew = new Comparator<Notice>() {
            @Override
            public int compare(Notice o1, Notice o2) {
                int ret;
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
        };

        list1 = new ArrayList<>(MainActivity.postArrayList); //dday
        Iterator<Post> iter = list1.iterator();
        while (iter.hasNext()) {
            Post p = iter.next();
            Date now = new Date();
//            if (p.getDeadline().getTime()-now.getTime()>24*60*60*1000 || now.after(p.getDeadline())) iter.remove();
            if (now.after(p.getDeadline())) iter.remove();
        }

        list2 = new ArrayList<>(MainActivity.postArrayList); //goal
        iter = list2.iterator();
        while (iter.hasNext()) {
            Post p = iter.next();
//            if (((float) p.getParticipants() / p.getGoal_participants())<0.7) iter.remove();
            if (((float) p.getParticipants() / p.getGoal_participants())<0.5) iter.remove();
        }

        list3 = new ArrayList<>(MainActivity.postArrayList); //new
        iter = list3.iterator();
        while (iter.hasNext()) {
            Post p = iter.next();
            Date now = new Date();
//            if (now.getTime()-p.getDate().getTime()>(24 * 60 * 60 * 1000)) iter.remove();
        }

        list4 = new ArrayList<>(MainActivity.NoticeArrayList);
        Collections.sort(list1, cmpDeadline);
        Collections.sort(list2, cmpGoal);
        Collections.sort(list3, cmpNew);
        Collections.sort(list4, cmpNoticeNew);
        if(list1.size()>5) list1 = new ArrayList<>(list1.subList(0, 5));
        if(list2.size()>5) list2 = new ArrayList<>(list2.subList(0, 5));
        if(list3.size()>5) list3 = new ArrayList<>(list3.subList(0, 5));

        if(mContext==null) mContext=getActivity();
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
                startActivityForResult(intent, DO_SURVEY);
//                getPost(item.getID(), position);

            }
        });
        adapter2.setOnItemClickListener(new RecyclerViewGoalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter2.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                startActivityForResult(intent, DO_SURVEY);
//                getPost(item.getID(), position);
            }
        });
        adapter3.setOnItemClickListener(new RecyclerViewNewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter3.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                startActivityForResult(intent, DO_SURVEY);
//                getPost(item.getID(), position);
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
                onSort(DEADLINE);
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavi);
                bottomNavigationView.setSelectedItemId(R.id.action_research_board);
            }
        });
        goal_sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSort(GOAL);
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavi);
                bottomNavigationView.setSelectedItemId(R.id.action_research_board);
            }
        });
        new_sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSort(NEW);
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavi);
                bottomNavigationView.setSelectedItemId(R.id.action_research_board);
            }
        });
        notice_sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NoticeActivity.class);
                startActivityForResult(intent, NOTICE);
            }
        });



        if (list4.size() != 0){
            main_notice.setText(list4.get(0).getTitle());
            main_notice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Notice item = (Notice)list4.get(0);
                    Intent intent = new Intent(mContext, NoticeDetailActivity.class);
                    intent.putExtra("post", item);
                    intent.putExtra("position", 0);
                    ((Activity)mContext).startActivityForResult(intent, DO_SURVEY);
                }
            });
        }

        if(list1.size()==0){
            recyclerView.setVisibility(View.GONE);
            tiger1.setVisibility(View.VISIBLE);
        }
        if(list2.size()==0){
            recyclerView2.setVisibility(View.GONE);
            tiger2.setVisibility(View.VISIBLE);
        }
        if(list3.size()==0){
            recyclerView3.setVisibility(View.GONE);
            tiger3.setVisibility(View.VISIBLE);
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


    public void onSort(int sort){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Comparator<Post> cmpGoalBoard = new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        if (o1.getPinned() == 1 && o2.getPinned() == 0) {
                            return -1;
                        } else if (o2.getPinned() == 1 && o1.getPinned() == 0) {
                            return 1;
                        }
                        else if(o1.getPinned()==1 && o1.getPinned()==1){
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
                        Date now = new Date();
                        if ((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))) {
                            return 1;
                        } else if ((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())) {
                            return -1;
                        }
                        int ret;
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
                };
                Comparator<Post> cmpNewBoard = new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        if (o1.getPinned() == 1 && o2.getPinned() == 0) {
                            return -1;
                        } else if (o2.getPinned() == 1 && o1.getPinned() == 0) {
                            return 1;
                        }
                        else if(o1.getPinned()==1 && o1.getPinned()==1){
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
                        Date now = new Date();
                        if ((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))) {
                            return 1;
                        } else if ((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())) {
                            return -1;
                        }
                        int ret;
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
                };
                Comparator<Post> cmpDeadlineBoard = new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        if (o1.getPinned() == 1 && o2.getPinned() == 0) {
                            return -1;
                        } else if (o2.getPinned() == 1 && o1.getPinned() == 0) {
                            return 1;
                        }
                        else if(o1.getPinned()==1 && o1.getPinned()==1){
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
                        int ret;
                        Date now = new Date();
                        if ((now.after(o1.getDeadline()) || o1.isDone()) && (!(now.after(o2.getDeadline()) || o2.isDone()))) {
                            return 1;
                        } else if ((!(now.after(o1.getDeadline()) || o1.isDone())) && (now.after(o2.getDeadline()) || o2.isDone())) {
                            return -1;
                        } else {
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
                switch (sort){
                    case NEW:
                        Collections.sort(BoardPost.list, cmpNewBoard);
                        frag1newselect();
                        break;
                    case GOAL:
                        Collections.sort(BoardPost.list, cmpGoalBoard);
                        frag1goalselect();
                        break;
                    case DEADLINE:
                        Collections.sort(BoardPost.list, cmpDeadlineBoard);
                        frag1dateselect();
                        break;
                    default:
                        break;
                }
                postListViewAdapter.notifyDataSetChanged();
                listView.setAdapter(postListViewAdapter);
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

                            JSONArray ja2 = (JSONArray)user.get("prizes");
                            ArrayList<String> prizearray = new ArrayList<String>();
                            for (int j = 0; j<ja2.length(); j++){
                                prizearray.add(ja2.getString(j));
                            }
                            UserPersonalInfo.prizes = prizearray;
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

//    private void setBanner(){
//        banner = view.findViewById(R.id.banner);
//        for(int i=0;i<IMAGES.length;i++){
//            ImagesArray.add(IMAGES[i]);
//        }
//        adapter = new BannerViewPagerAdapter(getActivity().getApplicationContext(), ImagesArray);
//        banner.setAdapter(adapter);
//
//
//
//        banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                currentPage = position;
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//
//        int NUM_PAGES = IMAGES.length;
//
//        final Handler handler = new Handler();
//        final Runnable Update = new Runnable() {
//            public void run() {
//                if (currentPage == NUM_PAGES) {
//                    currentPage = 0;
//                }
//                banner.setCurrentItem(currentPage++, true);
//            }
//        };
//        Timer swipeTimer = new Timer();
//        swipeTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(Update);
//            }
//        }, 30000, 30000);
//    }

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
                                        String writer_name = null;
                                        try {
                                            writer_name = reply.getString("writer_name");
                                        }catch (Exception e){
                                            writer_name = null;
                                        }
                                        Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide);
                                        re.setWriter_name(writer_name);
                                        if (!replyhide && !replyreports.contains(UserPersonalInfo.userID)){
                                            comments.add(re);
                                        }
                                    }
                                }
                            } catch (Exception e){
                                e.printStackTrace();
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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
    private void getPosts() throws Exception{
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/posts";
            RequestQueue requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            ArrayList<Post> postArrayList = new ArrayList<Post>();
                            ArrayList<Post> notreportedpostArrayList = new ArrayList<Post>();
                            ArrayList<Post> reportpostArrayList = new ArrayList<Post>();
                            JSONArray resultArr = new JSONArray(response.toString());
                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject post = resultArr.getJSONObject(i);
                                String id = post.getString("_id");
                                String title = post.getString("title");
                                String author = post.getString("author");
                                Integer author_lvl = post.getInt("author_lvl");
                                String content = post.getString("content");
                                Integer participants = post.getInt("participants");
                                Integer goal_participants = post.getInt("goal_participants");
                                String url = post.getString("url");
                                SimpleDateFormat fm = new SimpleDateFormat(mContext.getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(post.getString("date"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Date deadline = null;
                                try {
                                    deadline = fm.parse(post.getString("deadline"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Boolean with_prize = post.getBoolean("with_prize");
                                String prize = "none";
                                Integer count= 0;
                                Integer est_time = post.getInt("est_time");
                                String target = post.getString("target");
                                Boolean done = post.getBoolean("done");
                                Boolean hide = post.getBoolean("hide");
                                Integer extended = post.getInt("extended");
                                String author_userid = post.getString("author_userid");
                                if(with_prize) {
                                    prize = post.getString("prize");
                                    count = post.getInt("num_prize");
                                }
                                JSONArray ia = (JSONArray)post.get("participants_userids");

                                ArrayList<String> participants_userids = new ArrayList<String>();
                                for (int j = 0; j<ia.length(); j++){
                                    participants_userids.add(ia.getString(j));
                                }

                                JSONArray ka = (JSONArray)post.get("reports");

                                ArrayList<String> reports = new ArrayList<String>();
                                for (int j = 0; j<ka.length(); j++){
                                    reports.add(ka.getString(j));
                                }

                                ArrayList<Reply> comments = new ArrayList<>();
                                try{
                                    JSONArray ja = (JSONArray)post.get("comments");
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
                                Integer pinned = 0;
                                Boolean annonymous = false;
                                String author_info = "";
                                try {
                                    pinned = post.getInt("pinned");
                                    annonymous = post.getBoolean("annonymous");
                                    author_info = post.getString("author_info");
                                }catch (Exception e){

                                }
                                Post newPost = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, count,comments,done, extended, participants_userids, reports, hide, author_userid, pinned, annonymous, author_info);
                                Date now = new Date();
                                if(reports.contains(UserPersonalInfo.userID) || hide) {
                                    reportpostArrayList.add(newPost);
                                } else if (now.after(newPost.getDeadline()) || newPost.isDone()){
                                    notreportedpostArrayList.add(newPost);
                                }
                                else {
                                    postArrayList.add(newPost);
                                    notreportedpostArrayList.add(newPost);
                                }
                            }
                            MainActivity.reportpostArrayList = reportpostArrayList;
                            MainActivity.notreportedpostArrayList = notreportedpostArrayList;
                            MainActivity.postArrayList = postArrayList;
                            getPostsDone = true;

                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
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
    private void getNotices() throws Exception{
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/notices";
            RequestQueue requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            ArrayList<Notice> NoticeArrayList = new ArrayList<Notice>();
                            JSONArray resultArr = new JSONArray(response.toString());

                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject post = resultArr.getJSONObject(i);
                                String id = post.getString("_id");
                                String title = post.getString("title");
                                String author = post.getString("author");
                                String content = post.getString("content");
                                SimpleDateFormat fm = new SimpleDateFormat(mContext.getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(post.getString("date"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                JSONArray images = (JSONArray)post.get("image_urls");
                                ArrayList<String> imagearray = new ArrayList<>();
                                if(images!=null) {
                                    imagearray = new ArrayList<String>();
                                    for (int j = 0; j < images.length(); j++) {
                                        imagearray.add(images.getString(j));
                                    }
                                }


                                Notice newNotice = new Notice(id, title, author, content, date);

                                if(images!=null){
                                    newNotice.setImages(imagearray);
                                }
                                NoticeArrayList.add(newNotice);
                            }
                            MainActivity.NoticeArrayList = NoticeArrayList;
                            getNoticesDone = true;
                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
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
}