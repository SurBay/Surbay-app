package com.pumasi.surbay.classfile;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.pumasi.surbay.R;

public class CustomDialog extends AlertDialog {
    private TextView mPositiveButton;
    private TextView mNegativeButton;
    private TextView content;

    private View.OnClickListener mPositiveListener;
    private View.OnClickListener mNegativeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.custom_dialog);

        //셋팅
        content=(TextView)findViewById(R.id.customdialog_text);
        mPositiveButton=(TextView)findViewById(R.id.customdialog_btnpositive);
        mNegativeButton=(TextView)findViewById(R.id.customdialog_btnnegative);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        mPositiveButton.setOnClickListener(mPositiveListener);
        mNegativeButton.setOnClickListener(mNegativeListener);
    }

    //생성자 생성
    public CustomDialog(@NonNull Context context, View.OnClickListener positiveListener, View.OnClickListener negativeListener) {
        super(context);
        this.mPositiveListener = positiveListener;
        this.mNegativeListener = negativeListener;
    }

    public CustomDialog(@NonNull Context context) {
        super(context);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.custom_dialog);
        content=(TextView)findViewById(R.id.customdialog_text);
        mPositiveButton=(TextView)findViewById(R.id.customdialog_btnpositive);
        mNegativeButton=(TextView)findViewById(R.id.customdialog_btnnegative);

        mPositiveButton.setOnClickListener(mPositiveListener);
        mNegativeButton.setOnClickListener(mNegativeListener);

    }

    public void setMessage(String message) {
        this.content.setText(message);
    }

    public void setNegativeButton(String text, final View.OnClickListener listener) {
        this.mNegativeButton.setVisibility(View.VISIBLE);
        this.mNegativeButton.setText(text);
        this.mNegativeListener = listener;
    }

    public void setPositiveButton(String text, final View.OnClickListener listener) {
        this.mPositiveButton.setVisibility(View.VISIBLE);
        this.mPositiveButton.setText(text);
        this.mPositiveListener = listener;
    }

    public void setNegativeButtonColor(@ColorInt int color) {
        this.mNegativeButton.setTextColor(color);
    }

}
