package com.example.surbay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static androidx.core.app.ActivityCompat.startActivityForResult;
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
    private static RecyclerView recyclerView;
    private static RecyclerView recyclerView2;
    private static RecyclerView recyclerView3;
    private static RecyclerViewAdapter adapter1;
    private static RecyclerViewAdapter adapter2;
    private static RecyclerViewAdapter adapter3;
    private static Context mContext;

    private static ImageButton date_sort_button;
    private static ImageButton goal_sort_button;
    private static ImageButton new_sort_button;

    private static ArrayList<Post> list1;
    private static ArrayList<Post> list2;
    private static ArrayList<Post> list3;
    private static Comparator<Post> cmpDeadline;
    private static Comparator<Post> cmpNew;
    private static Comparator<Post> cmpGoal;

    private BoardFragment1 boardFragment1;

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

        date_sort_button = view.findViewById(R.id.date_sort_button);
        goal_sort_button = view.findViewById(R.id.goal_sort_button);
        new_sort_button = view.findViewById(R.id.new_sort_button);

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

        list1 = new ArrayList<>(MainActivity.postArrayList);
        list2 = new ArrayList<>(MainActivity.postArrayList);
        list3 = new ArrayList<>(MainActivity.postArrayList);
        Collections.sort(list1, cmpDeadline);
        Collections.sort(list2, cmpGoal);
        Collections.sort(list3, cmpNew);
        adapter1 = new RecyclerViewAdapter(mContext, list1);
        adapter2 = new RecyclerViewAdapter(mContext, list2);
        adapter3 = new RecyclerViewAdapter(mContext, list3);
        if(MainActivity.postArrayList.size()==0) {
            view.setVisibility(View.INVISIBLE);
        }
        else{

            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter1);
            recyclerView2.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView2.setAdapter(adapter2);
            recyclerView3.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView3.setAdapter(adapter3);
            adapter1.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Post item = (Post) adapter1.getItem(position);
                    Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), PostDetailActivity.class);
                    intent.putExtra("post", item);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, DO_SURVEY);
                }
            });
            adapter2.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Post item = (Post) adapter2.getItem(position);
                    Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), PostDetailActivity.class);
                    intent.putExtra("post", item);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, DO_SURVEY);
                }
            });
            adapter3.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Post item = (Post) adapter3.getItem(position);
                    Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), PostDetailActivity.class);
                    intent.putExtra("post", item);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, DO_SURVEY);
                }
            });

        }

        date_sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnRefrech(DEADLINE);
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavi);
                bottomNavigationView.setSelectedItemId(R.id.action_boards);
//                if(BoardsFragment.adapter!=null) {
//                    BoardsFragment.adapter.notifyDataSetChanged();
//                    BoardsFragment.viewPager.setAdapter(null);
//                    BoardsFragment.viewPager.setAdapter(BoardsFragment.adapter);
////                    BoardsFragment.viewPager.setCurrentItem(0);
//                }
            }
        });
        goal_sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnRefrech(GOAL);
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavi);
                bottomNavigationView.setSelectedItemId(R.id.action_boards);
//                if(BoardsFragment.adapter!=null) {
//                    BoardsFragment.adapter.notifyDataSetChanged();
//                    BoardsFragment.viewPager.setAdapter(null);
//                    BoardsFragment.viewPager.setAdapter(BoardsFragment.adapter);
////                    BoardsFragment.viewPager.setCurrentItem(0);
//                }
            }
        });
        new_sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnRefrech(NEW);
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavi);
                bottomNavigationView.setSelectedItemId(R.id.action_boards);
//                if(BoardsFragment.adapter!=null) {
//                    BoardsFragment.adapter.notifyDataSetChanged();
//                    BoardsFragment.viewPager.setAdapter(null);
//                    BoardsFragment.viewPager.setAdapter(BoardsFragment.adapter);
////                    BoardsFragment.viewPager.setCurrentItem(0);
//                }
            } 
        });
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
        Collections.sort(list1, cmpDeadline);
        Collections.sort(list2, cmpGoal);
        Collections.sort(list3, cmpNew);
        adapter1 = new RecyclerViewAdapter(mContext, list1);
        adapter2 = new RecyclerViewAdapter(mContext, list2);
        adapter3 = new RecyclerViewAdapter(mContext, list3);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter1);
        recyclerView2.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView3.setAdapter(adapter3);
        adapter1.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter1.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        adapter2.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter2.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        adapter3.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter3.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
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