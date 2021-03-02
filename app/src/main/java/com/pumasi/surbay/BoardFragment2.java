package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.SurveyTipListViewAdapter;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.PostNonSurvey;
import com.pumasi.surbay.classfile.Surveytip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

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
    private SwipeRefreshLayout refreshLayout;
    ArrayList<Surveytip> list;

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
        list = MainActivity.surveytipArrayList;
        Comparator<Surveytip> cmpNew = new Comparator<Surveytip>() {
            @Override
            public int compare(Surveytip o1, Surveytip o2) {
                int ret;
                Date date1 = o1.getDate();
                Date date2 = o2.getDate();
                int compare = date1.compareTo(date2);
                Log.d("datecomparing", date1 + "   " + date2 + "  " + compare);
                if (compare > 0)
                    ret = -1; //date2<date1
                else if (compare == 0)
                    ret = 0;
                else
                    ret = 1;
                return ret;
            }
        };
        Collections.sort(list, cmpNew);
        listViewAdapter = new SurveyTipListViewAdapter(list);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Surveytip item = (Surveytip) listViewAdapter.getItem(position);
                String tipid = item.getID();
                getSurveyTip(tipid, position);

            }
        });


        refreshLayout = view.findViewById(R.id.refresh_boards2);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    MainActivity.getSurveytips();

                    list = MainActivity.surveytipArrayList;
                    Comparator<Surveytip> cmpNew = new Comparator<Surveytip>() {
                        @Override
                        public int compare(Surveytip o1, Surveytip o2) {
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
                    Collections.sort(list, cmpNew);

                    listViewAdapter = new SurveyTipListViewAdapter(list);
                    listView.setAdapter(listViewAdapter);

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
                int pos = data.getIntExtra("position", -1);
                Surveytip surveytip = data.getParcelableExtra("surveyTip");
                MainActivity.surveytipArrayList.set(pos, surveytip);
                try {
                    MainActivity.getSurveytips();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listViewAdapter = new SurveyTipListViewAdapter(MainActivity.surveytipArrayList);
                listViewAdapter.notifyDataSetChanged();
                listView.setAdapter(listViewAdapter);

            default:
                return;
        }

    }
    private void getSurveyTip(String id, int position) {
        try{
            String requestURL = getString(R.string.server) + "/api/surveytips/getsurveytip/"+ id;
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            Log.d("response is", "surveytip"+response);
                            String tip_id = res.getString("_id");
                            String title = res.getString("title");
                            String author = res.getString("author");
                            Integer author_lvl = res.getInt("author_lvl");
                            String content = res.getString("content");
                            SimpleDateFormat fm = new SimpleDateFormat(getActivity().getString(R.string.date_format));
                            Date date = null;
                            try {
                                date = fm.parse(res.getString("date"));
                                Log.d("parsing date", "success");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String category = res.getString("category");
                            Integer likes = res.getInt("likes");
                            JSONArray ja = (JSONArray)res.get("liked_users");

                            ArrayList<String> liked_users = new ArrayList<String>();
                            for (int j = 0; j<ja.length(); j++){
                                liked_users.add(ja.getString(j));
                            }

                            JSONArray images = (JSONArray)res.get("image_urls");
                            ArrayList<String> imagearray = new ArrayList<>();
                            if(images!=null) {
                                imagearray = new ArrayList<String>();
                                for (int j = 0; j < images.length(); j++) {
                                    imagearray.add(images.getString(j));
                                }
                            }


                            Surveytip surveytip = new Surveytip(tip_id, title, author, author_lvl, content,  date, category, likes, liked_users);
                            if(images!=null){
                                surveytip.setImage_uris(imagearray);
                            }
                            Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), TipdetailActivity.class);
                            intent.putExtra("post", surveytip);
                            intent.putExtra("position", position);
                            startActivityForResult(intent, LIKE_SURVEY);


                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
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