package com.example.surbay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.surbay.adapter.BannerViewPagerAdapter;
import com.example.surbay.adapter.RecyclerViewDdayAdapter;
import com.example.surbay.adapter.RecyclerViewGoalAdapter;
import com.example.surbay.adapter.RecyclerViewNewAdapter;
import com.example.surbay.adapter.RecyclerViewNoticeAdapter;
import com.example.surbay.classfile.Notice;
import com.example.surbay.classfile.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.surbay.BoardFragment1.frag1datesortbutton;
import static com.example.surbay.BoardFragment1.frag1goalsortbutton;
import static com.example.surbay.BoardFragment1.frag1newsortbutton;
import static com.example.surbay.BoardFragment1.listView;
import static com.example.surbay.BoardFragment1.listViewAdapter;

public class HomeFragment extends Fragment // Fragment 클래스를 상속받아야한다
{
    private Integer DO_SURVEY = 2;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_home,container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        Log.d("title is", ""+((AppCompatActivity) getActivity()).getSupportActionBar().getTitle());

        recyclerView = view.findViewById(R.id.recycler1);
        recyclerView2 = view.findViewById(R.id.recycler2);
        recyclerView3 = view.findViewById(R.id.recycler3);
        recyclerView4 = view.findViewById(R.id.recycler4);

        date_sort_button = view.findViewById(R.id.date_sort_button);
        goal_sort_button = view.findViewById(R.id.goal_sort_button);
        new_sort_button = view.findViewById(R.id.new_sort_button);
        notice_sort_button = view.findViewById(R.id.notice_sort_button);

        cmpGoal = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
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

        list1 = new ArrayList<>(MainActivity.finishpostArrayList);
        list2 = new ArrayList<>(MainActivity.finishpostArrayList);
        list3 = new ArrayList<>(MainActivity.finishpostArrayList);
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
                mContext.startActivity(intent);
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
                mContext.startActivity(intent);
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
                mContext.startActivity(intent);
            }
        });
        adapter4.setOnItemClickListener(new RecyclerViewNoticeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Notice item = (Notice) adapter4.getItem(position);
                Intent intent = new Intent(mContext, NoticeDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
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
                startActivity(intent);
            }
        });

        banner = view.findViewById(R.id.banner);
        for(int i=0;i<IMAGES.length;i++){
            ImagesArray.add(IMAGES[i]);
        }
        adapter = new BannerViewPagerAdapter(mContext, ImagesArray);
        banner.setAdapter(adapter);
        final int[] currentPage = {0};
        int NUM_PAGES = 0;

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage[0] == NUM_PAGES) {
                    currentPage[0] = 0;
                }
                banner.setCurrentItem(currentPage[0]++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);


        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){

    }
    public static void receivedPosts(){
        makeView();
    }
    private static void makeView(){
        list1 = new ArrayList<>(MainActivity.finishpostArrayList);
        list2 = new ArrayList<>(MainActivity.finishpostArrayList);
        list3 = new ArrayList<>(MainActivity.finishpostArrayList);
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
                mContext.startActivity(intent);
            }
        });
        adapter2.setOnItemClickListener(new RecyclerViewGoalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter2.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        adapter3.setOnItemClickListener(new RecyclerViewNewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter3.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        adapter4.setOnItemClickListener(new RecyclerViewNoticeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Notice item = (Notice) adapter4.getItem(position);
                Intent intent = new Intent(mContext, NoticeDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });

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
}