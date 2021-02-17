package com.example.surbay.mypage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.surbay.R;
import com.example.surbay.adapter.GridAdapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class IGotGifts extends AppCompatActivity {
    GridAdapter gridAdapter;
    GridView gridview;

    ArrayList<URL> uris;
    ImageView back;


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

        uris = new ArrayList<>();
        try {
            uris.add(new URL("https://surbayprizebucket.s3.ap-northeast-2.amazonaws.com/3381613395418010.jfif"));
            Log.d("gift", "S");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        gridview = findViewById(R.id.igotgifts_grid);
        gridAdapter = new GridAdapter(this, uris);
        gridview.setAdapter(gridAdapter);
    }


}