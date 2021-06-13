package com.pumasi.surbay.pages.mypage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.GridAdapter;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IGotGifts extends AppCompatActivity {
    private GridAdapter gridAdapter;
    private RecyclerView gridview;

    ArrayList<String> uris;
    ImageView back;
    private Bitmap bm;

    private ArrayList<Bitmap> prizes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_got_gifts);

        getSupportActionBar().hide();

        back = findViewById(R.id.igotgifts_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gridview = findViewById(R.id.igotgifts_grid);
        updatePrizeCheck();

        uris = UserPersonalInfo.prizes;
        Log.d("i got prizes", ""+uris);
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d("prizes", "prize length" + prizes);
                gridAdapter = new GridAdapter(IGotGifts.this, prizes);
                int numOfColums = 2;
                gridview.setLayoutManager(new GridLayoutManager(IGotGifts.this, numOfColums));
                gridview.setAdapter(gridAdapter);
                gridAdapter.setOnItemClickListener(new GridAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Bitmap prize_img = gridAdapter.getItem(position);
                        Intent intent = new Intent(getApplicationContext(), GiftDetailActivity.class);
//                        Uri uri = bitmapToUriConverter(prize_img);
                        saveBitmaptoCache(prize_img);
                        startActivity(intent);
                    }
                });


            }
        };
        for(int i = 0; i<uris.size(); i++) {
            int finalI = i;
            new Thread() {
                Message msg;

                public void run() {
                    try {

                        Log.d("start", "bitmap no." + finalI);
                        String uri = uris.get(finalI);
                        URL url = new
                                URL(uri);
                        URLConnection conn = url.openConnection();
                        conn.connect();
                        BufferedInputStream bis = new
                                BufferedInputStream(conn.getInputStream());
                        bm = BitmapFactory.decodeStream(bis);
                        Log.d("got bitmap", "bitmap no." + finalI + "    "+ bm);
                        prizes.add(bm);
                        bis.close();
                        msg = handler.obtainMessage();
                        handler.sendMessage(msg);
                    } catch (IOException e) {
                    }
                }
            }.start();
        }
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void saveBitmaptoCache(Bitmap bm){
        try {

            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updatePrizeCheck(){
        try{
            String requestURL = getString(R.string.server) + "/api/users/prizecheck";
            RequestQueue requestQueue = Volley.newRequestQueue(IGotGifts.this);
            JSONObject params = new JSONObject();
            params.put("prize_check", UserPersonalInfo.prizes.size());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        UserPersonalInfo.prize_check = UserPersonalInfo.prizes.size();
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + UserPersonalInfo.token);
                    return headers;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
}