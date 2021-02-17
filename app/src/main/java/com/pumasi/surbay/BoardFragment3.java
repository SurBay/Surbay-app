package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.pumasi.surbay.adapter.NonSurveyListViewAdapter;
import com.pumasi.surbay.classfile.PostNonSurvey;

public class BoardFragment3 extends Fragment {

    private NonSurveyListViewAdapter listViewAdapter;
    private ListView listView;
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_board3,container,false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        listView = view.findViewById(R.id.list);
        listViewAdapter = new NonSurveyListViewAdapter(MainActivity.feedbackArrayList);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭시 글 확대 기능 추가 예정
                PostNonSurvey item = (PostNonSurvey) listViewAdapter.getItem(position);
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), Feedbackdetail.class);
                intent.putExtra("post", item);
                intent.putParcelableArrayListExtra("reply", item.getComments());
                Log.d("adapter click", "prize:"+item.getComments().toString());
                intent.putExtra("position", position);
            }
        });

        return view;
    }
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        listView = view.findViewById(R.id.list);
        listViewAdapter = new NonSurveyListViewAdapter(MainActivity.feedbackArrayList);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭시 글 확대 기능 추가 예정
                PostNonSurvey item = (PostNonSurvey) listViewAdapter.getItem(position);
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), Feedbackdetail.class);
                intent.putExtra("post", item);
                intent.putParcelableArrayListExtra("reply", item.getComments());
                Log.d("adapter click", "prize:"+item.getComments().toString());
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }
}