package com.example.surbay;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class PostDetailActivity extends AppCompatActivity {
    static final int START_SURVEY = 1;
    static final int DONE = 1;
    static final int NOT_DONE = 0;
    private int doneSurvey;
    private TextView participants;
    private Post post;
    private int position;

    static Button surveyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start app", "hahaha");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail);
        setResult(NOT_DONE);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        TextView author = findViewById(R.id.author);
        TextView level = findViewById(R.id.level);
        TextView deadline = findViewById(R.id.deadline);
        participants = findViewById(R.id.participants);
        TextView title = findViewById(R.id.title);
        TextView target = findViewById(R.id.target);
        TextView content = findViewById(R.id.content);;
        surveyButton = findViewById(R.id.surveyButton);
        LinearLayout prizeLayout = findViewById(R.id.prize_layout);
        TextView prize = findViewById(R.id.prize);

        post = intent.getParcelableExtra("post");
        position = intent.getIntExtra("position", -1);

        author.setText(post.getAuthor());
        level.setText("Lv "+post.getAuthor_lvl());
        deadline.setText(new SimpleDateFormat("MM.dd").format(post.getDeadline()));
        participants.setText(""+post.getParticipants()+"/"+post.getGoal_participants());
        title.setText(post.getTitle());
        target.setText(post.getTarget());
        content.setText(post.getContent());
        if(!post.isWith_prize()){
            prizeLayout.setVisibility(View.GONE);
        }else{
            prize.setText(post.getPrize());
        }
        doneSurvey = NOT_DONE;

        String surveyURL = post.getUrl();
        surveyButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent newIntent = new Intent(getApplicationContext(), SurveyWebActivity.class);
                        newIntent.putExtra("url", surveyURL);
                        startActivityForResult(newIntent, START_SURVEY);
                    }
                }
        );

        setbuttonunable();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case DONE:
                try {
                    int updatedParticipants = post.getParticipants()+1;
                    updateParticipants(updatedParticipants);
                    doneSurvey = DONE;
                    Intent resultIntent = new Intent(getApplicationContext(), BoardFragment1.class);
                    resultIntent.putExtra("position", position);
                    resultIntent.putExtra("participants", updatedParticipants);
                    setResult(DONE, resultIntent);
                    Log.d("result code setting to ", "1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case NOT_DONE:
                setResult(NOT_DONE);
                return;
        }
    }

    private void updateParticipants(int updatedParticipants) throws Exception{
        participants.setText(""+updatedParticipants+"/"+post.getGoal_participants());
        String requestURL = "https://surbay-server.herokuapp.com/api/posts/" + post.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("participants", updatedParticipants);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.post_detail_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.share:
                //select account item
                break;
            case R.id.report:
                //select back button
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setbuttonunable(){
        if (post.getAuthor().equals(UserPersonalInfo.name)){
            surveyButton.setClickable(false);
            surveyButton.setText("본인의 설문입니다");
        }
    }
}
