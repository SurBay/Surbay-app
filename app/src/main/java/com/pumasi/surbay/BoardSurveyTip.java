package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.SurveyTipListViewAdapter;
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

public class BoardSurveyTip extends Fragment {
    static final int WRITE_NEWPOST = 1;
    static final int LIKE_SURVEY = 2;
    static final int NEWPOST = 1;
    static final int LIKED = 5;
    static final int DISLIKED = 4;

    public static SurveyTipListViewAdapter listViewAdapter;
    public static ListView listView;
    private View view;
    TextView writeButton;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<Surveytip> list;
    private boolean getSurveyTipsDone = false;
    private refreshHandler handler = new refreshHandler();
    private BackgroundThread refreshThread;

    Parcelable state;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_board_surveytip,container,false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        listView = view.findViewById(R.id.list);
        writeButton = view.findViewById(R.id.tipwriteButton);
//        writeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), TipWriteActivity.class);
//                intent.putExtra("purpose", WRITE_NEWPOST);
//                startActivityForResult(intent, WRITE_NEWPOST);
//            }
//        });
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
        if(state != null) {
            listView.onRestoreInstanceState(state);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Surveytip surveytip = (Surveytip) listViewAdapter.getItem(position);
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), TipdetailActivity.class);
                intent.putExtra("post", surveytip);
                intent.putExtra("position", position);
                startActivityForResult(intent, LIKE_SURVEY);
//                getSurveyTip(tipid, position);

            }
        });


        mSwipeRefreshLayout = view.findViewById(R.id.refresh_boards2);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshThread = new BackgroundThread();
                refreshThread.start();
            }
        });
        return view;
    }
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        listView = view.findViewById(R.id.list);
        listViewAdapter = new SurveyTipListViewAdapter(MainActivity.surveytipArrayList);
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
        listView.setAdapter(listViewAdapter);
        if(state != null) {
            listView.onRestoreInstanceState(state);
        }
    }
    class BackgroundThread extends Thread {
        public void run() {
            getSurveytips();
            while(!getSurveyTipsDone) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            Message message = handler.obtainMessage();
            handler.sendMessage(message);
        }
    }

    private class refreshHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

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
            if(state != null) {
                listView.onRestoreInstanceState(state);
            }

            mSwipeRefreshLayout.setRefreshing(false);
            getSurveyTipsDone = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
//            case WRITE_NEWPOST:
//                switch (resultCode) {
//                    case NEWPOST:
//                        try {
//                            MainActivity.getSurveytips();
//                            refreshListview();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        return;
//                    default:
//                        return;
//                }
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
                if(state != null) {
                    listView.onRestoreInstanceState(state);
                }

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
                            String tip_id = res.getString("_id");
                            String title = res.getString("title");
                            String author = res.getString("author");
                            Integer author_lvl = res.getInt("author_lvl");
                            String content = res.getString("content");
                            SimpleDateFormat fm = new SimpleDateFormat(getActivity().getString(R.string.date_format));
                            Date date = null;
                            try {
                                date = fm.parse(res.getString("date"));
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
                            String author_userid = res.getString("author_userid");


                            Surveytip surveytip = new Surveytip(tip_id, title, author, author_lvl, content,  date, category, likes, liked_users);
                            surveytip.setAuthor_userid(author_userid);
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

    public void refreshListview(){
        listViewAdapter.notifyDataSetChanged();
        listView.setAdapter(listViewAdapter);
        if(state != null) {
            listView.onRestoreInstanceState(state);
        }
    }
    private void getSurveytips(){
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/surveytips";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {

                        try {
                            ArrayList<Surveytip> surveytipArrayList = new ArrayList<Surveytip>();
                            JSONArray resultArr = new JSONArray(response.toString());

                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject post = resultArr.getJSONObject(i);
                                String id = post.getString("_id");
                                String title = post.getString("title");
                                String author = post.getString("author");
                                Integer author_lvl = post.getInt("author_lvl");
                                String content = post.getString("content");
                                SimpleDateFormat fm = new SimpleDateFormat(getActivity().getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(post.getString("date"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String category = post.getString("category");
                                Integer likes = post.getInt("likes");
                                JSONArray ja = (JSONArray)post.get("liked_users");

                                ArrayList<String> liked_users = new ArrayList<String>();
                                for (int j = 0; j<ja.length(); j++){
                                    liked_users.add(ja.getString(j));
                                }
                                JSONArray images = (JSONArray)post.get("image_urls");
                                ArrayList<String> imagearray = new ArrayList<>();
                                if(images!=null) {
                                    imagearray = new ArrayList<String>();
                                    for (int j = 0; j < images.length(); j++) {
                                        imagearray.add(images.getString(j));
                                    }
                                }



                                String author_userid = post.getString("author_userid");



                                Surveytip newSurveytip = new Surveytip(id, title, author, author_lvl, content,  date, category, likes, liked_users);
                                newSurveytip.setAuthor_userid(author_userid);
                                if(images!=null){
                                    newSurveytip.setImage_uris(imagearray);
                                }
                                surveytipArrayList.add(newSurveytip);

                            }
                            MainActivity.surveytipArrayList = surveytipArrayList;
                            getSurveyTipsDone = true;

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
    @Override
    public void onPause() {
        state = listView.onSaveInstanceState();
        super.onPause();
    }
}