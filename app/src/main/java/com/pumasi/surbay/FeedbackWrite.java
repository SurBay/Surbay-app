package com.pumasi.surbay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.pumasi.surbay.adapter.ImageAdapter;
import com.pumasi.surbay.classfile.PostNonSurvey;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.pumasi.surbay.BoardFragment2.listView;
import static com.pumasi.surbay.BoardFragment2.listViewAdapter;

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
        Config config = new Config();
        config.setSelectionLimit(10);
        ImagePickerActivity.setConfig(config);
        Intent intent  = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent,13);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13 && resultCode == RESULT_OK){
            image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
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



    public void Back_survey(){
        setResult(0);
        String message;
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackWrite.this);
        dialog = builder.setMessage("건의/의견 작성을 취소하겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.teal_200);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.black);
            }
        });
        dialog.show();
    }


    public void Done_survey(){
        String title = titleedit.getText().toString(); ///게시글 작성 당시 글쓴이의 레벨이 반영?
        String content = contentedit.getText().toString();
        category = categoryedit.getSelectedItemPosition();

        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd a KK시");
        Date date = new Date();

        String author = UserPersonalInfo.userID;
        Integer author_lvl = UserPersonalInfo.level;
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));


        if (title.getBytes().length <= 0 || content.getBytes().length <= 0 || category == 0){
            AlertDialog.Builder bu = new AlertDialog.Builder(FeedbackWrite.this);
            dialog = bu.setMessage("입력되지 않은 정보가 있습니다")
                    .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onShow(DialogInterface arg0) {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.black);
                }
            });
            dialog.show();
        } else {
            PostNonSurvey feedback = new PostNonSurvey(null, title, author, author_lvl, content, date, category, new ArrayList<Reply>());
            MainActivity.feedbackArrayList.add(0, feedback);
            listViewAdapter.notifyDataSetChanged();
            listView.setAdapter(listViewAdapter);
            Intent intent = new Intent(FeedbackWrite.this, BoardFragment3.class);
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
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            String id = resultObj.getString("id");
                            PostNonSurvey item = new PostNonSurvey(id, title, author, author_lvl, content, date, category, new ArrayList<Reply>());
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