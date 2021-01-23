package com.example.surbay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class MypageFragment extends Fragment // Fragment 클래스를 상속받아야한다
{


    private Integer DO_SURVEY = 2;

    private static View view;
    private static RecyclerView recyclerView_make;
    private static RecyclerView recyclerView_parti;
    private static RecyclerViewAdapter adapter_make;
    private static RecyclerViewAdapter adapter_parti;
    private static Context mContext;

    private static ArrayList<Post> list_make;
    private static ArrayList<Post> list_parti;

    private static ImageButton i_make_button;
    private static ImageButton i_parti_button;
    private static ImageButton i_get_button;

    TextView nameview;
    ImageView profileview;
    TextView levelview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_mypage,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        recyclerView_make = view.findViewById(R.id.recycler_mypage_make);
        recyclerView_parti = view.findViewById(R.id.recycler_mypage_partic);

        i_make_button = view.findViewById(R.id.mypage_i_make);
        i_parti_button = view.findViewById(R.id.mypage_i_parti);
        i_get_button = view.findViewById(R.id.mypage_i_get);

        getlistofI();

        Log.d("listmake size is", ""+list_make.size());

        adapter_make = new RecyclerViewAdapter(mContext, list_make);
        adapter_parti = new RecyclerViewAdapter(mContext, list_parti);
        if(MainActivity.postArrayList.size()==0) {
            view.setVisibility(View.INVISIBLE);
        }
        else{
            recyclerView_make.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView_make.setAdapter(adapter_make);
            recyclerView_parti.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView_parti.setAdapter(adapter_parti);
            adapter_make.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Post item = (Post) adapter_make.getItem(position);
                    Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), PostDetailActivity.class);
                    intent.putExtra("post", item);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
            adapter_parti.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Post item = (Post) adapter_parti.getItem(position);
                    Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), PostDetailActivity.class);
                    intent.putExtra("post", item);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
        }
        i_make_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        i_parti_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        i_get_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        Button logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SharedPreferences에 저장된 값들을 로그아웃 버튼을 누르면 삭제하기 위해
                //SharedPreferences를 불러옵니다. 메인에서 만든 이름으로
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                SharedPreferences auto = getActivity().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = auto.edit();
                //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                editor.clear();
                editor.commit();
                Toast.makeText(getActivity(), "로그아웃.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

        nameview = view.findViewById(R.id.mypage_myname);
        profileview = view.findViewById(R.id.mypage_profile_image);
        levelview = view.findViewById(R.id.user_level);

        nameview.setText(UserPersonalInfo.name);
        levelview.setText("레벨 " + UserPersonalInfo.level.toString());
        return view;
    }


    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){

    }

    public static void receivedPosts(){
        makeView();
    }
    private static void makeView(){
        list_make = new ArrayList<>(MainActivity.postArrayList);
        list_parti = new ArrayList<>(MainActivity.postArrayList);
        adapter_make = new RecyclerViewAdapter(mContext, list_make);
        adapter_parti = new RecyclerViewAdapter(mContext, list_parti);
        recyclerView_make.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView_make.setAdapter(adapter_make);
        recyclerView_parti.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView_parti.setAdapter(adapter_parti);
        adapter_make.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter_make.getItem(position);
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        adapter_parti.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) adapter_parti.getItem(position);
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

    public void getlistofI(){
        String author = UserPersonalInfo.name;
        list_make = new ArrayList<Post>();
        list_parti = new ArrayList<Post>();
        for (Post post : MainActivity.postArrayList){
            Log.d("post", "post author is "+post.getAuthor()+" and myname is "+author);
            if (post.getAuthor().equals(author)){
                list_make.add(post);
            }
        }
        for (Post post : MainActivity.finishpostArrayList){
            if (post.getAuthor().equals(author)){
                list_make.add(post);
            }
        }
    }
}