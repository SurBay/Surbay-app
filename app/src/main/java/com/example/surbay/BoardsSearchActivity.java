package com.example.surbay;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.surbay.adapter.ListViewAdapter;
import com.example.surbay.adapter.NonSurveyListViewAdapter;
import com.example.surbay.classfile.Post;
import com.example.surbay.classfile.PostNonSurvey;

import java.util.ArrayList;

public class BoardsSearchActivity extends AppCompatActivity {

    public final int SURBAY_SELECT = 0;
    public final int TIP_SELECT = 1;
    public final int FEEDBACK_SELECT = 2;

    static final int DO_SURVEY = 2;
    static final int DONE = 1;

    final String[] spinner_context = {"서베이", "설문 Tip", "건의사항"};
    ListView search_listview;
    EditText search_editview;
    ImageButton search_enter;
    Spinner search_spinner;

    static ListViewAdapter search_SurveyAdapter;
    static NonSurveyListViewAdapter search_NonsurveyAdapter;
    static ArrayList<Post> search_list_post;
    static ArrayList<PostNonSurvey> search_list_postNon;

    int selecting_spinner;
    int selected_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boards_search);

        this.getSupportActionBar().hide();


        search_editview = (EditText)findViewById(R.id.search_edittext);
        search_enter = (ImageButton)findViewById(R.id.boards_enter_button);
        search_spinner = (Spinner)findViewById(R.id.search_spinner);
        search_listview = (ListView)findViewById(R.id.search_listview);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner_context);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        search_spinner.setAdapter(adapter);


        search_list_post = new ArrayList<Post>();
        search_SurveyAdapter = new ListViewAdapter(search_list_post);
        search_listview.setAdapter(search_SurveyAdapter);

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

        search_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_spinner = selecting_spinner;
                String search_keyword = search_editview.getText().toString();
                if (search_keyword.length() > 0 ){
                    Log.d("search", " " + search_keyword);
                    search_list_post = new ArrayList<Post>();
                    search_list_postNon = new ArrayList<PostNonSurvey>();
                    do_search(search_keyword);
                }
            }
        });

        search_editview.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                selected_spinner = selecting_spinner;
                String search_keyword = search_editview.getText().toString();
                switch (actionId){
                    case EditorInfo
                            .IME_ACTION_DONE:
                        break;
                    default:
                        if (search_keyword.length() > 0 ){
                            search_editview.setText(search_keyword);
                            Log.d("search", " " + search_keyword);
                            search_list_post = new ArrayList<Post>();
                            search_list_postNon = new ArrayList<PostNonSurvey>();
                            do_search(search_keyword);
                        }
                        return false;
                }
                return true;
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

    private void do_search(String search_keyword) {
        switch (selected_spinner){
            case SURBAY_SELECT:
                for (Post post : MainActivity.postArrayList){
                    if (post.getContent().contains(search_keyword) || (post.getTitle().contains(search_keyword))){
                        search_list_post.add(post);
                    }
                }
                for (Post post : MainActivity.finishpostArrayList){
                    if ((post.getContent().contains(search_keyword)) || (post.getTitle().contains(search_keyword))){
                        search_list_post.add(post);
                    }
                }
                search_SurveyAdapter = new ListViewAdapter(search_list_post);
                search_listview.setAdapter(search_SurveyAdapter);

                break;
            case TIP_SELECT:
                for (PostNonSurvey post : MainActivity.surveytipArrayList){
                    if ((post.getContent().contains(search_keyword)) || (post.getTitle().contains(search_keyword))){
                        search_list_postNon.add(post);
                    }
                }
                search_NonsurveyAdapter = new NonSurveyListViewAdapter(search_list_postNon);
                search_listview.setAdapter(search_NonsurveyAdapter);


                search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    }
                });
                break;
            case FEEDBACK_SELECT:
                for (PostNonSurvey post : MainActivity.feedbackArrayList){
                    if ((post.getContent().contains(search_keyword)) || (post.getTitle().contains(search_keyword))){
                        search_list_postNon.add(post);
                    }
                }
                search_NonsurveyAdapter = new NonSurveyListViewAdapter(search_list_postNon);
                search_listview.setAdapter(search_NonsurveyAdapter);

                search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            default:
                return;
        }

    }
}