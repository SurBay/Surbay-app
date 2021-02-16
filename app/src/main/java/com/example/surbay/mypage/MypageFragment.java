package com.example.surbay.mypage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.surbay.MainActivity;
import com.example.surbay.PostDetailActivity;
import com.example.surbay.R;
import com.example.surbay.classfile.UserPersonalInfo;
import com.example.surbay.adapter.RecyclerViewAdapter;
import com.example.surbay.classfile.Post;

import java.util.ArrayList;

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
    ImageView setting;
    ImageView bell;

    TextView upload_2nd;
    TextView parti_2nd;
    TextView can_sur;
    TextView willdo_sur;
    TextView willdo_sur_below;

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

        setting = view.findViewById(R.id.mypage_setting);
        bell = view.findViewById(R.id.mypage_bell);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), MypageSettingMain.class);
                startActivity(intent);
            }
        });
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
            if (list_make.size() > 0){
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
            }
            if (list_parti.size() > 0 ){
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
        }
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
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), Mypage_uploadNParti.class);
                intent.putExtra("what", 2);
                startActivity(intent);
            }
        });


        nameview = view.findViewById(R.id.mypage_myname);
        profileview = view.findViewById(R.id.mypage_profile_image);
        levelview = view.findViewById(R.id.user_level);
        upload_2nd = view.findViewById(R.id.upload_2nd);
        parti_2nd = view.findViewById(R.id.parti_2nd);
        can_sur = view.findViewById(R.id.mypage_can_sur);
        willdo_sur = view.findViewById(R.id.mypage_willdo_sur);
        willdo_sur_below = view.findViewById(R.id.mypage_willdo_sur_below);

        nameview.setText(UserPersonalInfo.name);
        levelview.setText("레벨 " + UserPersonalInfo.level.toString());
        upload_2nd.setText(list_make.size() + "개");
        parti_2nd.setText(list_parti.size() + "개");

        if (list_parti.size()!=0){
            can_sur.setText(list_make.size()/(list_parti.size()*7)+"회");
        } else {
            can_sur.setText("0회");
        }
        willdo_sur.setText((list_parti.size()%7)+"회/7회");

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
        String author = UserPersonalInfo.userID;
        list_make = new ArrayList<Post>();
        list_parti = new ArrayList<Post>();
        for (Post post : MainActivity.postArrayList){
            Log.d("post", "post author is "+post.getAuthor()+" and myname is "+author);
            if (post.getAuthor().equals(author)){
                list_make.add(post);
            } else if (UserPersonalInfo.participations.contains(post.getID())) {
                list_parti.add(post);
            }
        }
    }
}