package com.pumasi.surbay.mypage;

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
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.PostDetailActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.GridAdapter;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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

        back = findViewById(R.id.igotgifts_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gridview = findViewById(R.id.igotgifts_grid);

        uris = UserPersonalInfo.prizes;
        Log.d("i got prizes", ""+uris);
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d("prizes", "prize length" + prizes);
                gridAdapter = new GridAdapter(IGotGifts.this, prizes);
                gridview.setLayoutManager(new LinearLayoutManager(IGotGifts.this, LinearLayoutManager.HORIZONTAL, false));
                gridview.setAdapter(gridAdapter);
                gridAdapter.setOnItemClickListener(new GridAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Bitmap prize_img = gridAdapter.getItem(position);
                        Intent intent = new Intent(getApplicationContext(), GiftDetailActivity.class);
                        Uri uri = getImageUri(IGotGifts.this, prize_img);
                        intent.putExtra("uri", uri);
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
}