package com.pumasi.surbay;

import android.content.Intent;
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

import com.pumasi.surbay.adapter.ListViewAdapter;
import com.pumasi.surbay.classfile.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

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
        list = MainActivity.postArrayList;

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
                //클릭시 글 확대 기능 추가 예정
                Post item = (Post) listViewAdapter.getItem(position);
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putParcelableArrayListExtra("reply", item.getComments());
                Log.d("adapter click", "prize:"+item.getComments().toString());
                intent.putExtra("position", position);
                startActivityForResult(intent, DO_SURVEY);

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

                    list = new ArrayList<>(MainActivity.postArrayList);
                    list.addAll(MainActivity.finishpostArrayList);
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
        list = MainActivity.postArrayList;
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
                        listViewAdapter.changeItem();
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
                    case(DELETE):
                        int pos = data.getIntExtra("position", -1);
                        list.remove(pos);
                        listViewAdapter.remove(pos);
                        return;
                    default:
                        return;
                }
            default:
                return;
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
}
