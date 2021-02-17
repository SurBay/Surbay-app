package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.pumasi.surbay.adapter.SurveyTipListViewAdapter;
import com.pumasi.surbay.classfile.Surveytip;

public class BoardFragment2 extends Fragment {
    static final int WRITE_NEWPOST = 1;
    static final int LIKE_SURVEY = 2;
    static final int NEWPOST = 1;
    static final int LIKED = 5;
    static final int DISLIKED = 4;

    public static SurveyTipListViewAdapter listViewAdapter;
    public static ListView listView;
    private View view;
    TextView writeButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_board2,container,false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        listView = view.findViewById(R.id.list);
        writeButton = view.findViewById(R.id.tipwriteButton);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), TipWriteActivity.class);
                intent.putExtra("purpose", WRITE_NEWPOST);
                startActivityForResult(intent, WRITE_NEWPOST);
            }
        });
        listViewAdapter = new SurveyTipListViewAdapter(MainActivity.surveytipArrayList);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Surveytip item = (Surveytip) listViewAdapter.getItem(position);
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), TipdetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                startActivityForResult(intent, LIKE_SURVEY);
            }
        });
        return view;
    }
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        listView = view.findViewById(R.id.list);
        listViewAdapter = new SurveyTipListViewAdapter(MainActivity.surveytipArrayList);
        listView.setAdapter(listViewAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case WRITE_NEWPOST:
                switch (resultCode) {
                    case NEWPOST:
                        try {
                            MainActivity.getSurveytips();
                            OnRefrech();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    default:
                        return;
                }
            case LIKE_SURVEY:
                switch (resultCode){
                    case LIKED:

                        break;
                    case DISLIKED:
                        break;
                }
            default:
                return;
        }

    }

    public void OnRefrech(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listViewAdapter.notifyDataSetChanged();
                listView.setAdapter(listViewAdapter);
            }
        },300);
    }
}