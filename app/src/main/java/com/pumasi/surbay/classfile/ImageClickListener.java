package com.pumasi.surbay.classfile;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.pumasi.surbay.TipImageDetail;

import java.net.URL;

public class ImageClickListener implements View.OnClickListener {
    Context context;


    URL imageID;

    public ImageClickListener(Context context, URL imageID) {
        this.context = context;
        this.imageID = imageID;
    }



    public void onClick(View v) {

        Intent intent = new Intent(context, TipImageDetail.class);
        intent.putExtra("image ID", imageID);
        context.startActivity(intent);
    }
}
