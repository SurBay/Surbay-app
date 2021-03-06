package com.pumasi.surbay.classfile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pumasi.surbay.R;

public class CustomDialog extends Dialog{
    protected TextView mPositiveButton;
    protected TextView mNegativeButton;
    protected TextView content;

    protected View.OnClickListener mPositiveListener;
    public CustomDialog customDialog;
    protected Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);

        //셋팅
        content=(TextView)findViewById(R.id.customdialog_text);
        mPositiveButton=(TextView)findViewById(R.id.customdialog_btnpositive);
        mNegativeButton=(TextView)findViewById(R.id.customdialog_btnnegative);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        mPositiveButton.setOnClickListener(mPositiveListener);
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    public CustomDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    //생성자 생성
    public CustomDialog(@NonNull Context context, View.OnClickListener positiveListener) {
        super(context);
        this.context = context;
        this.mPositiveListener = positiveListener;
    }


    public void hideNegativeButton(boolean hide){
        if (hide){
            this.mNegativeButton.setVisibility(View.INVISIBLE);
        }
    }

    public CustomDialog(@NonNull Context context, View.OnClickListener positiveListener, View.OnClickListener negetiveListener) {
        super(context);
        this.context = context;
        this.mPositiveListener = positiveListener;
        mNegativeButton.setOnClickListener(negetiveListener);
    }

    public void setMessage(String message) {
        this.content.setText(message);
    }


    public void setNegativeButton(String text) {
        this.mNegativeButton.setVisibility(View.VISIBLE);
        this.mNegativeButton.setText(text);
    }

    @SuppressLint("ResourceAsColor")
    public void setPositiveButton(String text) {
        this.mPositiveButton.setVisibility(View.VISIBLE);
        this.mPositiveButton.setText(text);
        this.mNegativeButton.setTextColor(R.color.gray2);

    }

    public void setmPositiveListener(View.OnClickListener positiveListener){
        this.mPositiveListener = positiveListener;
    }
    public void setmNegativeListener(View.OnClickListener negativeListener){
        this.mNegativeButton.setOnClickListener(negativeListener);
    }

    public void customSimpleDialog(String message, String pos, String neg, View.OnClickListener listener) {
        customDialog = new CustomDialog(this.context, listener);
        customDialog.show();
        customDialog.setMessage(message);
        if (pos != null) {
            customDialog.setPositiveButton(pos);
        }
        customDialog.setNegativeButton(neg);
    }
    public void customRejectDialog(String message, String neg) {
        customDialog = new CustomDialog(this.context, null);
        customDialog.show();
        customDialog.setMessage(message);
        customDialog.setNegativeButton(neg);
    }
}
