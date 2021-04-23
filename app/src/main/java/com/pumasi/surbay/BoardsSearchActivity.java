package com.pumasi.surbay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.adapter.GeneralListViewAdapter;
import com.pumasi.surbay.adapter.PostListViewAdapter;
import com.pumasi.surbay.adapter.SurveyTipListViewAdapter;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.PostNonSurvey;
import com.pumasi.surbay.classfile.Surveytip;

import java.util.ArrayList;

public class BoardsSearchActivity extends AppCompatActivity {
    static final int LIKED = 5;
    static final int DISLIKED = 4;

    public final int SURBAY_SELECT = 0;
    public final int GENERAL_SELECT = 1;
    public final int TIP_SELECT = 2;


    static final int DO_SURVEY = 2;
    static final int DONE = 1;
    static final int LIKE_SURVEY = 3;

    final String[] spinner_context = {"품앗이", "SurBay", "설문 Tip"};
    ListView search_listview;
    EditText search_editview;
    ImageButton search_delete;
    Spinner search_spinner;
    ImageButton back;

    static PostListViewAdapter search_SurveyAdapter;
    static GeneralListViewAdapter search_GeneralAdapter;
    static SurveyTipListViewAdapter search_TipAdapter;
    static ArrayList<Post> search_list_post;
    static ArrayList<General> search_list_general;
    static ArrayList<Surveytip> search_list_tip;

    int selecting_spinner;
    int selected_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boards_search);

        this.getSupportActionBar().hide();


        search_editview = (EditText)findViewById(R.id.search_edittext);
        search_delete = (ImageButton)findViewById(R.id.boards_delete_button);
        search_spinner = (Spinner)findViewById(R.id.search_spinner);
        search_listview = (ListView)findViewById(R.id.search_listview);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_spinner_item_search, spinner_context);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        search_spinner.setAdapter(adapter);

        Intent intent = getIntent();
        selected_spinner = intent.getIntExtra("pos", 0);
        search_spinner.setSelection(selected_spinner);


        search_list_post = new ArrayList<>();
        search_list_tip = new ArrayList<>();
        search_list_general = new ArrayList<>();
        search_SurveyAdapter = new PostListViewAdapter(search_list_post);
        search_listview.setAdapter(search_SurveyAdapter);

        back = findViewById(R.id.go_back_search);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selecting_spinner = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                search_spinner.setSelection(0);
                selecting_spinner = 0;
            }
        });

        search_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_editview.getText().clear();
                search_delete.setVisibility(View.INVISIBLE);
            }
        });

        search_editview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                selected_spinner = selecting_spinner;
                String search_keyword = search_editview.getText().toString();
                if (search_keyword.length() > 0 ){
                    Log.d("search", " " + search_keyword);
                    search_list_post = new ArrayList<Post>();
                    search_list_general = new ArrayList<General>();
                    search_delete.setVisibility(View.VISIBLE);
                    do_search(search_keyword);
                };
            }
        });

        search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭시 글 확대 기능 추가 예정
                if (selected_spinner == SURBAY_SELECT){
                    Post item = (Post)search_SurveyAdapter.getItem(position);
                    Log.d("search", item.toString());
                    Intent intent = new Intent(BoardsSearchActivity.this, PostDetailActivity.class);
                    intent.putExtra("post", item);
                    intent.putExtra("position", position);
                    Log.d("search", item.toString());
                    startActivityForResult(intent, DO_SURVEY);
                }
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    private void do_search(String search_keyword) {
        switch (selected_spinner){
            case SURBAY_SELECT:
                search_list_post.clear();
                for (Post post : MainActivity.notreportedpostArrayList){
                    if (post.getContent().toUpperCase().contains(search_keyword.toUpperCase()) || (post.getTitle().toUpperCase().contains(search_keyword.toUpperCase()))){
                        search_list_post.add(post);
                    }
                }
                search_SurveyAdapter = new PostListViewAdapter(search_list_post);
                search_listview.setAdapter(search_SurveyAdapter);
                search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //클릭시 글 확대 기능 추가 예정
                        if (selected_spinner == SURBAY_SELECT){
                            Post item = (Post)search_SurveyAdapter.getItem(position);
                            Log.d("search", item.toString());
                            Intent intent = new Intent(BoardsSearchActivity.this, PostDetailActivity.class);
                            intent.putExtra("post", item);
                            intent.putExtra("position", position);
                            Log.d("search", item.toString());
                            startActivityForResult(intent, DO_SURVEY);
                        }
                    }
                });

                break;
            case GENERAL_SELECT:
                search_list_general.clear();
                for (General post : MainActivity.generalArrayList){
                    if ((post.getContent().contains(search_keyword)) || (post.getTitle().contains(search_keyword))){
                        search_list_general.add(post);
                    }
                }
                search_GeneralAdapter = new GeneralListViewAdapter(search_list_general);
                search_listview.setAdapter(search_GeneralAdapter);

                search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        General item =  (General) search_GeneralAdapter.getItem(position);
                        Log.d("search", item.toString());
                        Intent intent = new Intent(BoardsSearchActivity.this, GeneralDetailActivity.class);
                        intent.putExtra("general", item);
                        intent.putExtra("position", position);
                        Log.d("search", item.toString());
                        startActivity(intent);
                    }
                });
                break;
            case TIP_SELECT:
                search_list_tip.clear();
                for (Surveytip post : MainActivity.surveytipArrayList){
                    if ((post.getContent().contains(search_keyword)) || (post.getTitle().contains(search_keyword))){
                        search_list_tip.add(post);
                    }
                }
                search_TipAdapter = new SurveyTipListViewAdapter(search_list_tip);
                search_listview.setAdapter(search_TipAdapter);


                search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (selected_spinner == TIP_SELECT){
                            Surveytip item = (Surveytip)search_TipAdapter.getItem(position);
                            Log.d("search", item.toString());
                            Intent intent = new Intent(BoardsSearchActivity.this, TipdetailActivity.class);
                            intent.putExtra("post", item);
                            intent.putExtra("position", position);
                            Log.d("search", item.toString());
                            startActivityForResult(intent, LIKE_SURVEY);
                        }
                    }
                });
                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DO_SURVEY:
                Log.d("result code is", ""+resultCode);
                switch (resultCode){
                    case(DONE):
                        int position = data.getIntExtra("position", -1);
                        int newParticipants = data.getIntExtra("participants", -1);
                        search_SurveyAdapter.updateParticipants(position, newParticipants);
                        Post item = (Post) search_SurveyAdapter.getItem(position);

                        search_listview.setAdapter(search_SurveyAdapter);
                        Log.d("new participant", "new is: " + item.getParticipants());
                        return;
                    default:
                        return;
                }
            case LIKE_SURVEY:
                int pos = data.getIntExtra("position", -1);
                Surveytip surveytip = data.getParcelableExtra("surveyTip");
                search_list_tip.set(pos, surveytip);
                MainActivity.surveytipArrayList.set(pos, surveytip);
                try {
                    MainActivity.getSurveytips();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                search_TipAdapter = new SurveyTipListViewAdapter(search_list_tip);
                search_TipAdapter.notifyDataSetChanged();
                search_listview.setAdapter(search_TipAdapter);
                break;
            default:
                return;
        }

    }
}