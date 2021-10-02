package com.pumasi.surbay.classfile;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pumasi.surbay.R;

public class MessageDialog extends Dialog {

    private Context context;
    private CustomDialog customDialog;
    private TextView tv_message_negative;
    private TextView tv_message_positive;
    private EditText et_message;
    public String note;
    private View.OnClickListener mPositiveListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_dialog);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_message_negative = findViewById(R.id.tv_message_negative);
        tv_message_positive = findViewById(R.id.tv_message_positive);
        et_message = findViewById(R.id.et_message);

        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                note = et_message.getText().toString();
            }
        });
        tv_message_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_message.getText().toString().length() > 0) {
                    customDialog = new CustomDialog(context, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customDialog.dismiss();
                            cancel();
                        }
                    });
                    customDialog.show();
                    customDialog.setMessage("쪽지 작성을 취소하겠습니까?");
                    customDialog.setPositiveButton("확인");
                    customDialog.setNegativeButton("취소");
                } else {
                    cancel();
                }

            }
        });
        tv_message_positive.setOnClickListener(mPositiveListener);
    }

    public MessageDialog(@NonNull Context context, View.OnClickListener mPositiveListener) {
        super(context);
        this.mPositiveListener = mPositiveListener;
        this.context = context;
    }

    public void setClickable(boolean clickable) {
        tv_message_positive.setClickable(clickable);
        tv_message_negative.setClickable(clickable);
    }

}
