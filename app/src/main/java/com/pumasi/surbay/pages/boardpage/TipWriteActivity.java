package com.pumasi.surbay.pages.boardpage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.ImageAdapter;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.Surveytip;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TipWriteActivity extends AppCompatActivity {
    static final int NEWPOST = 1;

    RecyclerView imagelistview;
    ImageAdapter imageAdapter;
    RelativeLayout bottomlayout;
    EditText titleedit;
    EditText categoryedit;
    EditText contentedit;
    ImageView imageadd;

    TextView writeBack;
    TextView writeDone;
    AlertDialog dialog;

    ArrayList<Uri> image_uris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_write);

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

        titleedit = findViewById(R.id.tip_write_title);
        categoryedit = findViewById(R.id.tip_write_category);
        contentedit = findViewById(R.id.tip_write_content);
        writeBack = findViewById(R.id.tipwriteBack);
        writeDone = findViewById(R.id.tipwriteDone);
        bottomlayout = findViewById(R.id.tipwritebottom);
        imageadd = findViewById(R.id.tipwriteImageadd);
        imagelistview = findViewById(R.id.imagerecycler);


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
                imageAdapter = new ImageAdapter(TipWriteActivity.this, image_uris);
                imagelistview.setAdapter(imageAdapter);
                imagelistview.setLayoutManager(new LinearLayoutManager(TipWriteActivity.this, LinearLayoutManager.HORIZONTAL, false));
                imagelistview.setVisibility(View.VISIBLE);

                imageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Intent intent = new Intent(TipWriteActivity.this, TipImageDetail.class);
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
        CustomDialog customDialog = new CustomDialog(TipWriteActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        customDialog.show();
        customDialog.setMessage("TIP 작성을 취소하겠습니까?");
        customDialog.setPositiveButton("확인");
        customDialog.setNegativeButton("아니오");
    }


    public void Done_survey(){
        String title = titleedit.getText().toString(); ///게시글 작성 당시 글쓴이의 레벨이 반영?
        String content = contentedit.getText().toString();
        String category = categoryedit.getText().toString();

        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd a KK시");
        Date date = new Date();

        String author = UserPersonalInfo.userID;
        Integer author_lvl = UserPersonalInfo.level;
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));


        if (title.getBytes().length <= 0 || content.getBytes().length <= 0 || category.getBytes().length <= 0){

            CustomDialog customDialog = new CustomDialog(TipWriteActivity.this, null);
            customDialog.show();
            customDialog.setMessage("입력되지 않은 정보가 있습니다");
            customDialog.setNegativeButton("확인");
        } else {
            Surveytip tip = new Surveytip(null, title, author, author_lvl, content, date, category, 0, new ArrayList<String>());
            tip.setAuthor_userid(UserPersonalInfo.userID);
            MainActivity.surveytipArrayList.add(0, tip);
            try {
                postPost(title, author, author_lvl, content, date, category, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        }
    }

    public void postPost(String title, String author, Integer author_lvl, String content, Date date, String category, Integer likes) throws Exception{
        try{
            Log.d("starting request", "post posts");
            String requestURL = getString(R.string.server)+"/api/surveytips";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("title", title);
            params.put("author", author);
            params.put("author_lvl", author_lvl);
            params.put("content", content);
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSS");
            params.put("date", fm.format(date));
            params.put("category", category);
            params.put("likes", likes);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            String id = resultObj.getString("id");
                            Surveytip item = new Surveytip(id, title, author, author_lvl, content, date, category, likes, new ArrayList<String>());
                            item.setAuthor_userid(UserPersonalInfo.userID);
                            MainActivity.surveytipArrayList.add(item);

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