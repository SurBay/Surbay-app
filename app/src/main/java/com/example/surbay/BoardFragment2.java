package com.example.surbay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.surbay.adapter.NonSurveyListViewAdapter;

public class BoardFragment2 extends Fragment {

    private NonSurveyListViewAdapter listViewAdapter;
    private ListView listView;
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_board2,container,false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        listView = view.findViewById(R.id.list);
        listViewAdapter = new NonSurveyListViewAdapter(MainActivity.surveytipArrayList);
        listView.setAdapter(listViewAdapter);
        return view;
    }
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        listView = view.findViewById(R.id.list);
        listViewAdapter = new NonSurveyListViewAdapter(MainActivity.surveytipArrayList);
        listView.setAdapter(listViewAdapter);
    }
}