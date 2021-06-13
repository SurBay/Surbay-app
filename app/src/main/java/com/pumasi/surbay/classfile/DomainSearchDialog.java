package com.pumasi.surbay.classfile;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pumasi.surbay.R;
import com.pumasi.surbay.pages.signup.SignupActivityEmail;
import com.pumasi.surbay.adapter.DialogSearchListViewAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class DomainSearchDialog extends Dialog {
    private Context context;

    ImageButton dialog_close;
    ImageButton dialog_search;
    EditText university;
    ListView search_result;
    TextView search_text;

    JSONObject domains = SignupActivityEmail.domains;

    ArrayList<String> search_list = new ArrayList<>();

    DialogSearchListViewAdapter dialogSearchListViewAdapter;

    public DomainSearchDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_search_dialog);

        dialog_close = findViewById(R.id.dialog_close);
        dialog_search = findViewById(R.id.dialog_search);
        university = findViewById(R.id.dialog_university);
        search_result = findViewById(R.id.dialog_search_listview);
        search_text = findViewById(R.id.find_email_text);

        dialogSearchListViewAdapter = new DialogSearchListViewAdapter(search_list);
        search_result.setAdapter(dialogSearchListViewAdapter);

        university.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                do_search();
                return true;
            }
        });


        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        dialog_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                do_search();
            }
        });
    }

    private void do_search(){
        search_text.setVisibility(View.GONE);
        String search_keyword = university.getText().toString();
        if (search_keyword.length() > 0 ){
            search_text.setVisibility(View.GONE);
            search_list.clear();
            Log.d("search", " " + search_keyword);
            Iterator i = domains.keys();
            while(i.hasNext()){
                String key = i.next().toString();
                if(key.toUpperCase().contains(search_keyword.toUpperCase())){
                    search_list.add(key);
                }
                Log.d("searched", ""+key.toUpperCase().contains(search_keyword.toUpperCase())+key.toUpperCase());
            }
            dialogSearchListViewAdapter = new DialogSearchListViewAdapter(search_list);
            Log.d("serarch_list", ""+search_list.size()+search_list);
            if(search_list.size()==0){
                search_text.setText("검색 결과가 없습니다\n대학교 추가를 원하시면 인스타그램 @surbay_official 계정 DM을 통해 연락 바랍니다.");
                search_text.setVisibility(View.VISIBLE);
            }
            search_result.setAdapter(dialogSearchListViewAdapter);
            search_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String university_result = (String) dialogSearchListViewAdapter.getItem(position);
                    SignupActivityEmail.search_result_university = university_result;
                    dismiss();
                }
            });
        }else{
            search_list.clear();
            search_text.setVisibility(View.VISIBLE);
            search_text.setText("현재 서울 소재 대학을 대상으로 서비스를 제공하고 있습니다. 대학교 추가를 원하시면 인스타그랩 @surbay_official 계정 DM을 통해 연락 바랍니다.");

        }
    }
}
