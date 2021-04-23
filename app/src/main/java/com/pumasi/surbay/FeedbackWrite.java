package com.pumasi.surbay;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.ImageAdapter;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.GifSizeFilter;
import com.pumasi.surbay.classfile.PostNonSurvey;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.pumasi.surbay.BoardSurveyTip.listView;
import static com.pumasi.surbay.BoardSurveyTip.listViewAdapter;

public class FeedbackWrite extends AppCompatActivity {
    static final int NEWPOST = 1;
    final String[] spinner_category = {"선택해주세요", "운영정책", "앱 구동 불편사항", "기능 추가/개선", "기타"};

    RecyclerView imagelistview;
    ImageAdapter imageAdapter;
    RelativeLayout bottomlayout;
    EditText titleedit;
    Spinner categoryedit;
    EditText contentedit;
    ImageView imageadd;

    TextView writeBack;
    TextView writeDone;
    AlertDialog dialog;

    ArrayList<Uri> image_uris;
    int category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_write);

        this.getSupportActionBar().hide();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 200
                    );
                }
            }
        }

        titleedit = findViewById(R.id.feedback_write_title);
        categoryedit = findViewById(R.id.feedback_category);
        contentedit = findViewById(R.id.feedback_write_content);
        writeBack = findViewById(R.id.feedback_writeBack);
        writeDone = findViewById(R.id.feedback_writeDone);
        bottomlayout = findViewById(R.id.feedback_writebottom);
        imageadd = findViewById(R.id.feedback_writeImageadd);
        imagelistview = findViewById(R.id.feedback_imagerecycler);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner_category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryedit.setAdapter(adapter);
        categoryedit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = position-1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        categoryedit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu = new PopupMenu(FeedbackWrite.this, v);
//                popupMenu.getMenuInflater().inflate(R.menu.feedback_write_category_menu, popupMenu.getMenu());
//                popupMenu.show();
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()){
//                            case R.id.category1:
//                                category = 0;
//                                break;
//                            case R.id.category2:
//                                category = 1;
//                                break;
//                            case R.id.category3:
//                                category = 2;
//                                break;
//                            case R.id.category4:
//                                category = 3;
//                                break;
//                            default:
//                                break;
//                        }
//                        return true;
//                    }
//                });
//            }
//        });


        writeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Back_survey();
            }
        });

        writeDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Done_survey();
            }
        });

        imageadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
            }
        });
    }

    private void goToAlbum() {
        Matisse.from(FeedbackWrite.this)
                .choose(MimeType.ofImage())
                .theme(R.style.Matisse_White)
                .countable(false)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .maxSelectable(10)
                .originalEnable(true)
                .maxOriginalSize(10)
                .imageEngine(new GlideEngine())
                .setOnSelectedListener((uriList, pathList) -> {
                    Log.e("onSelected", "onSelected: pathList=" + pathList);
                })
                .showSingleMediaType(true)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(isChecked -> {
                    Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                })
                .forResult(13);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13 && resultCode == RESULT_OK){
            image_uris = (ArrayList<Uri>) Matisse.obtainResult(data);
            if (image_uris.size() > 0 ){
                imageAdapter = new ImageAdapter(FeedbackWrite.this, image_uris);
                imagelistview.setAdapter(imageAdapter);
                imagelistview.setLayoutManager(new LinearLayoutManager(FeedbackWrite.this, LinearLayoutManager.HORIZONTAL, false));
                imagelistview.setVisibility(View.VISIBLE);

                imageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Intent intent = new Intent(FeedbackWrite.this, TipImageDetail.class);
                        intent.putExtra("uri", image_uris.get(position));
                        intent.putExtra("position", position);
                        startActivityForResult(intent, 20);
                    }
                });
            }
        } else if (requestCode == 20 && resultCode == RESULT_OK){
            int position = getIntent().getIntExtra("position", 0);
            int delect = getIntent().getIntExtra("delect", 0);
            if (image_uris.size() > 0 && delect == 1){
                image_uris.remove(position);
                imageAdapter.removeItem(position);
                imageAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onBackPressed() {
        Back_survey();
    }

    public void Back_survey(){
        setResult(0);

        CustomDialog customDialog = new CustomDialog(FeedbackWrite.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        customDialog.show();
        customDialog.setMessage("건의/의견 작성을 취소하겠습니까?");
        customDialog.setPositiveButton("확인");
        customDialog.setNegativeButton("아니오");
    }


    public void Done_survey(){
        String title = titleedit.getText().toString(); ///게시글 작성 당시 글쓴이의 레벨이 반영?
        String content = contentedit.getText().toString();
//        category = categoryedit.getSelectedItemPosition();

        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd a KK시");
        Date date = new Date();

        String author = UserPersonalInfo.userID;
        Integer author_lvl = UserPersonalInfo.level;
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));


        if (title.getBytes().length <= 0 || content.getBytes().length <= 0 || category == -1){

            CustomDialog customDialog = new CustomDialog(FeedbackWrite.this, null);
            customDialog.show();
            customDialog.setMessage("입력되지 않은 정보가 있습니다");
            customDialog.setNegativeButton("확인");
        } else {
            PostNonSurvey feedback = new PostNonSurvey(null, title, author, author_lvl, content, date, category, new ArrayList<Reply>());
            feedback.setAuthor_userid(UserPersonalInfo.userID);
            MainActivity.feedbackArrayList.add(0, feedback);
            listViewAdapter.notifyDataSetChanged();
            listView.setAdapter(listViewAdapter);
            Intent intent = new Intent(FeedbackWrite.this, BoardFeedback.class);
            try {
                postPost(title, author, author_lvl, content, date, category);
            } catch (Exception e) {
                e.printStackTrace();
            }
            setResult(NEWPOST, intent);
            finish();
        }
    }

    public void postPost(String title, String author, Integer author_lvl, String content, Date date, Integer category) throws Exception{
        try{
            Log.d("starting request", "post posts");
            String requestURL = getString(R.string.server)+"/api/feedbacks";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("title", title);
            params.put("author", author);
            params.put("author_lvl", String.valueOf(author_lvl));
            params.put("content", content);
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSS");
            params.put("date", fm.format(date));
            params.put("category", String.valueOf(category));
            params.put("comments", String.valueOf(new ArrayList<Reply>()));
            params.put("author_userid", UserPersonalInfo.userID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            String id = resultObj.getString("id");
                            PostNonSurvey item = new PostNonSurvey(id, title, author, author_lvl, content, date, category, new ArrayList<Reply>());
                            item.setAuthor_userid(UserPersonalInfo.userID);
                            MainActivity.feedbackArrayList.add(item);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }
}