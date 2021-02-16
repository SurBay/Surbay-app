package com.example.surbay;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TipImageDetail extends AppCompatActivity {
    TextView delect;
    ImageView back;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_image_detail);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra("uri");
        int pos = intent.getIntExtra("position", 0);

        delect = findViewById(R.id.tipimageDelect);
        back = findViewById(R.id.tipimageback);
        imageView = findViewById(R.id.tipimagedetail);

        imageView.setImageURI(uri);
        delect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TipImageDetail.this, TipWriteActivity.class);
                intent.putExtra("position", pos);
                intent.putExtra("delect", 1);
                setResult(20, intent);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}