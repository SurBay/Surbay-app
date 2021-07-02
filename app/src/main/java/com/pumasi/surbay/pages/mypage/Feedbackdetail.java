package com.pumasi.surbay.pages.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.FeedbackReplyListViewAdapter;
import com.pumasi.surbay.classfile.PostNonSurvey;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.pages.boardpage.BoardFeedback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Feedbackdetail extends AppCompatActivity {
    final String[] spinner_category = {"운영정책", "앱 구동 불편사항", "기능 추가/개선", "기타"};
    TextView author;
    TextView date;
    TextView title;
    TextView content;;
    TextView category;
    EditText reply_enter;
    ImageButton reply_enter_button;
    LinearLayout line;

    ImageView back;
    private PostNonSurvey post;

    private static FeedbackReplyListViewAdapter detail_reply_Adapter;
    private static RecyclerView detail_reply_listView;
    private static ArrayList<Reply> replyArrayList;
    Date today;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbackdetail);

        this.getSupportActionBar().hide();

        back = findViewById(R.id.feedbackdetail_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        author = findViewById(R.id.feedback_detail_auther);
        title = findViewById(R.id.feedback_detail_title);
        date = findViewById(R.id.feedback_detail_date);
        content = findViewById(R.id.feedback_detail_content);
        category = findViewById(R.id.feedback_detail_category);
//        reply_enter = findViewById(R.id.feedback_reply_enter);
//        reply_enter_button = findViewById(R.id.feedback_reply_enter_button);
        detail_reply_listView = findViewById(R.id.feedback_detail_reply_list);
//        line = findViewById(R.id.feedback_detail_line);

        Intent intent = getIntent();

        post = (PostNonSurvey)intent.getParcelableExtra("post");
        position = intent.getIntExtra("position", 0);

//        author.setText(post.getAuthor());
        date.setText(new SimpleDateFormat("MM.dd").format(post.getDate()));
        title.setText(post.getTitle());
        content.setText(post.getContent());
        category.setText(spinner_category[post.getCategory()]);

        replyArrayList = post.getComments();
        Log.d("commentlen", ""+replyArrayList.size());
        detail_reply_Adapter = new FeedbackReplyListViewAdapter(Feedbackdetail.this, replyArrayList, post);
        detail_reply_listView.setLayoutManager(new LinearLayoutManager(this));
        detail_reply_listView.setAdapter(detail_reply_Adapter);


        Intent resultIntent = new Intent(getApplicationContext(), BoardFeedback.class);
        resultIntent.putExtra("position", position);
        setResult(RESULT_OK, resultIntent);
    }

//    private void postReply(String reply) {
//        String requestURL = getString(R.string.server)+"/api/feedbacks/writecomment/"+post.getID();
//        try{
//            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//            JSONObject params = new JSONObject();
//            params.put("writer", UserPersonalInfo.userID);
//            params.put("content",reply);
//
//            Date date = new Date();
//            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");
//            params.put("date", fm.format(date));
//
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                    (Request.Method.PUT, requestURL, params, response -> {
//                        Log.d("response is", ""+response);
//                        try {
//                            JSONObject resultObj = new JSONObject(response.toString());
//                            String id = resultObj.getString("id");
//                            Reply re = new Reply(id, UserPersonalInfo.userID, reply, date, new ArrayList<>(), false);
//                            replyArrayList.add(re);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }, error -> {
//                        Log.d("exception", "volley error");
//                        error.printStackTrace();
//                    });
//            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(jsonObjectRequest);
//        } catch (Exception e){
//            Log.d("exception", "failed posting");
//            e.printStackTrace();
//        }
//    }

}
